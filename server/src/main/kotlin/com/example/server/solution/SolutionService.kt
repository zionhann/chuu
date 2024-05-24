package com.example.server.solution

import com.example.server.problem.ProblemRepository
import com.example.server.solution.model.Solution
import com.example.server.solution.model.SourceFile
import com.example.server.solution.model.VerdictStatus
import com.example.server.solution.request.SearchFilter
import com.example.server.solution.request.SolutionId
import com.example.server.solution.request.SolutionRequest
import com.example.server.solution.response.SolutionResponse
import com.example.server.solution.response.StatusResponse
import com.example.server.solution.response.VerdictStatusResponse
import jakarta.transaction.Transactional
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.io.PrintWriter
import java.nio.file.Files
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.regex.Pattern

@Service
@Transactional
class SolutionService(
    private val problemRepository: ProblemRepository,
    private val solutionRepository: SolutionRepository,
    private val template: SimpMessagingTemplate,
) {
    fun startGrading(solutionId: Long) {
        val solution =
            solutionRepository.findById(solutionId)
                .orElseThrow(::IllegalArgumentException)
        compileCode(solution)?.let { mainFile ->
            runTestCases(solution, mainFile)
        }
        template.convertAndSend("/topic/status", VerdictStatusResponse(solution.id, solution.status))
    }

    private fun findClassName(src: String): String? {
        val pattern = Pattern.compile("class\\s+(\\w+)")
        val matcher = pattern.matcher(src)
        return if (matcher.find()) matcher.group(1) else null
    }

    private fun findEntryPoint(sourceFiles: List<SourceFile>): SourceFile? {
        val regex = "public\\s+static\\s+void\\s+main\\s*\\(\\s*String\\s*\\[\\]\\s*args\\s*\\)"
        val pattern = Pattern.compile(regex)

        sourceFiles.forEach { sourceFile ->
            val file = File(sourceFile.pathname)
            val matcher = pattern.matcher(file.readText())
            if (matcher.find()) return sourceFile
        }
        return null
    }

    private fun compileCode(solution: Solution): SourceFile? {
        val sourceFiles =
            solution.sourceFiles.map {
                val file = File(it.pathname)

                if (solution.sourceFiles.size == 1 && file.nameWithoutExtension == "tmp") {
                    // If the solution is submitted as a single file with a temporary name, rename it to the class name
                    // It will be triggered when the solution is submitted via the web editor
                    val className =
                        findClassName(file.readText()) ?: let { _ ->
                            solution.status = VerdictStatus.COMPILE_ERROR
                            solution.report = "No class name found"
                            return null
                        }
                    val targetFile = File(file.parent, "$className.java")
                    Files.move(file.toPath(), targetFile.toPath())

                    solution.sourceFiles
                        .first()
                        .apply {
                            filename = filename.replace("tmp", className)
                            pathname = targetFile.path
                        }
                } else {
                    it
                }
            }

        val filenames = sourceFiles.map(SourceFile::filename)
        val process =
            ProcessBuilder("javac", *filenames.toTypedArray())
                .directory(File(sourceFiles.first().workingDir))
                .redirectError(ProcessBuilder.Redirect.PIPE)
                .start()

        process.waitFor()

        val error = process.errorStream.bufferedReader().readText()
        if (error.isNotEmpty()) {
            solution.status = VerdictStatus.COMPILE_ERROR
            solution.report = error
            return null
        }
        return findEntryPoint(sourceFiles) ?: let { _ ->
            solution.status = VerdictStatus.COMPILE_ERROR
            solution.report = "No entry point found"
            null
        }
    }

    private fun runTestCases(
        solution: Solution,
        mainFile: SourceFile,
    ) {
        solution.status = VerdictStatus.RUNNING
        template.convertAndSend("/topic/status", VerdictStatusResponse(solution.id, solution.status))

        solution.problem.testCases.map { testCase ->
            val nameWithoutExtension =
                mainFile.filename
                    .removeSuffix(".java")
            val process =
                ProcessBuilder("java", nameWithoutExtension)
                    .directory(File(mainFile.workingDir))
                    .redirectOutput(ProcessBuilder.Redirect.PIPE)
                    .redirectError(ProcessBuilder.Redirect.PIPE)
                    .start()

            PrintWriter(process.outputStream).use { out ->
                out.println(testCase.input ?: "")
            }
            process.waitFor()

            val error =
                process.errorStream.bufferedReader()
                    .readText()
                    .trim()

            if (error.isNotEmpty()) {
                solution.status = VerdictStatus.RUNTIME_ERROR
                solution.report = error
                return
            }
            val output =
                process.inputStream.bufferedReader()
                    .readText()
                    .trim()

            solution.report =
                solution.report.plus(
                    """
                            |[Test case ${testCase.number}]
                            
                            |Input:
                            |${testCase.input ?: "<Empty>"}
                            
                            |Expected:
                            |${testCase.output}
                            
                            |Actual:
                            |$output
                            |
                    """.trimMargin(),
                )

            val actual = output.split("\n")
            val expected = testCase.output.split("\n")

            if (actual.containsAll(expected)) {
                solution.report =
                    solution.report.plus(
                        """
                                    |Result: Passed
                                    |============
                        """.trimMargin(),
                    )
            } else {
                solution.report =
                    solution.report.plus(
                        """
                                    |Result: Failed
                                    |============
                        """.trimMargin(),
                    )
                solution.status = VerdictStatus.WRONG_ANSWER
                return
            }
        }
        solution.status = VerdictStatus.ACCEPTED
    }

    fun getSolutions(searchFilter: SearchFilter): List<SolutionResponse> {
        val submissions =
            searchFilter.problemNumber?.let {
                val problem =
                    problemRepository.findByNumber(it) ?: throw IllegalArgumentException("Problem not found")
                solutionRepository.findAllByProblem(problem)
            } ?: solutionRepository.findAll()
        return submissions.map { submission ->
            SolutionResponse(
                solutionId = submission.id,
                submissionDate = submission.createdDate,
                author = submission.author,
                problemNumber = submission.problem.number,
                language = submission.language,
                verdict = submission.status,
            )
        }
    }

    private fun findPackageName(sourceCode: String): String {
        val pattern = Pattern.compile("package\\s+([\\w.]+)")
        val matcher = pattern.matcher(sourceCode)
        return if (matcher.find()) matcher.group(1).plus("/") else ""
    }

    fun submitSolution(
        problemNumber: String,
        metadata: SolutionRequest,
        sourceCode: String,
    ): SolutionId {
        val problem =
            problemRepository.findByNumber(problemNumber) ?: throw IllegalArgumentException("Problem not found")

        val workingDir = "solutions/${metadata.author}/${problem.number}/${
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HH_mm_ss"))
        }/java/"

        val filename =
            findPackageName(sourceCode).replace(".", "/")
                .plus("tmp.java")

        File(workingDir, filename).apply {
            parentFile.mkdirs()
            writeText(sourceCode)
        }.createNewFile()

        val solution =
            Solution(
                author = metadata.author,
                language = metadata.language,
                problem = problem,
            ).apply {
                val sourceFile =
                    SourceFile(
                        workingDir = workingDir,
                        filename = filename,
                        pathname = workingDir.plus(filename),
                        solution = this,
                    )
                this.sourceFiles.add(sourceFile)
            }
        val saved = solutionRepository.save(solution)
        return SolutionId(saved.id)
    }

    fun submitSolution(
        problemNumber: String,
        metadata: SolutionRequest,
        multipartFiles: List<MultipartFile>,
    ): SolutionId {
        val problem =
            problemRepository.findByNumber(problemNumber) ?: throw IllegalArgumentException("Problem not found")

        val workingDir =
            "solutions/${metadata.author}/${problem.number}/${
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HH_mm_ss"))
            }/java/"

        val solution =
            Solution(
                author = metadata.author,
                language = metadata.language,
                problem = problem,
            ).apply {
                multipartFiles.forEach { multipartFile ->
                    val sourceCode = multipartFile.inputStream.bufferedReader().readText()
                    val filename =
                        findPackageName(sourceCode).replace(".", "/")
                            .plus(multipartFile.originalFilename)

                    File(workingDir, filename).apply {
                        parentFile.mkdirs()
                        writeText(sourceCode)
                    }.createNewFile()

                    val sourceFile =
                        SourceFile(
                            workingDir = workingDir,
                            filename = filename,
                            pathname = workingDir.plus(filename),
                            solution = this,
                        )
                    this.sourceFiles.add(sourceFile)
                }
            }
        val saved = solutionRepository.save(solution)
        return SolutionId(saved.id)
    }

    fun getStatusOf(solutionId: Long): StatusResponse {
        val solution =
            solutionRepository
                .findById(solutionId)
                .orElseThrow(::IllegalArgumentException)
        return StatusResponse(
            solutionId = solution.id,
            sourceCode = solution.sourceFiles.map { File(it.pathname).readText() },
            report = solution.report,
        )
    }
}
