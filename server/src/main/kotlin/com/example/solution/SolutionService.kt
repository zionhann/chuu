package com.example.solution

import com.example.problem.ProblemRepository
import com.example.solution.model.Solution
import com.example.solution.model.SourceFile
import com.example.solution.model.VerdictStatus
import com.example.solution.request.SearchFilter
import com.example.solution.request.SolutionId
import com.example.solution.request.SolutionRequest
import com.example.solution.response.SolutionResponse
import com.example.solution.response.StatusResponse
import com.example.solution.response.VerdictStatusResponse
import jakarta.transaction.Transactional
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.nio.file.Files
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.regex.Matcher
import java.util.regex.Pattern

@Service
@Transactional
class SolutionService(
    private val problemRepository: ProblemRepository,
    private val solutionRepository: SolutionRepository,
    private val template: SimpMessagingTemplate,
) {
    fun startGrading(solutionId: Long) {
        val (solution, mainFile) =
            solutionRepository
                .findById(solutionId)
                .orElseThrow(::IllegalArgumentException)
                .also(::unifyInputStream)
                .let(::compileCode)

        mainFile?.let {
            runTestCases(solution, it)
        }
        template.convertAndSend("/topic/status", VerdictStatusResponse(solution.id, solution.status))
    }

    private fun unifyInputStream(solution: Solution) {
        val isScannerFound =
            solution.sourceFiles.any { sourceFile ->
                val file = File(sourceFile.pathname)
                val sourceCode = file.readText()
                val (isScannerFound, modifiedCode) = invalidateScannerDeclaration(sourceCode)

                file.writeText(modifiedCode)
                isScannerFound
            }

        if (isScannerFound) {
            val mainSourceFile = findEntryPoint(solution.sourceFiles) ?: return
            val mainSourceCode = File(mainSourceFile.pathname).readText()
            val className = findClassName(mainSourceCode) ?: return

            solution.sourceFiles.forEach { sourceFile ->
                val file = File(sourceFile.pathname)
                val sourceCode = file.readText()
                val modifiedCode = renameVariable(sourceCode, className)
                val finalCode = if (sourceFile == mainSourceFile) declareGlobalScanner(modifiedCode) else modifiedCode

                file.writeText(finalCode)
            }
        }
    }

    private fun invalidateScannerDeclaration(src: String): Pair<Boolean, String> {
        val regex = "Scanner\\s+\\w+\\s*=\\s*new\\s+Scanner\\(System\\.in\\);"
        val matcher =
            Pattern
                .compile(regex)
                .matcher(src)

        return matcher.find() to
            matcher.replaceAll { replacer ->
                "/// [Auto-generated] ".plus(replacer.group())
            }
    }

    private fun renameVariable(
        src: String,
        className: String,
    ): String {
        val regex = "\\w+\\.(next\\w*\\(\\))"

        return Pattern
            .compile(regex)
            .matcher(src)
            .replaceAll { result ->
                "$className.GLOBAL_IN.${result.group(1)}"
            }
    }

    private fun declareGlobalScanner(src: String): String {
        val regex = "\\w*\\s*class\\s+\\w+\\s*\\{"
        val matcher =
            Pattern
                .compile(regex)
                .matcher(src)

        return matcher
            .replaceFirst { replacer ->
                val scannerDeclaration = "static final Scanner GLOBAL_IN = new Scanner(System.in); /// [Auto-generated]"

                replacer
                    .group()
                    .plus("\n\t$scannerDeclaration\n")
            }
            .let(::importScanner)
    }

    private fun importScanner(src: String): String {
        return if (isScannerImported(src)) src else addScannerImport(src)
    }

    private fun isScannerImported(src: String): Boolean {
        val regex = "import\\s+java\\.util\\.Scanner\\s*;"
        return Pattern
            .compile(regex)
            .matcher(src)
            .find()
    }

    private fun addScannerImport(src: String): String {
        val regex = "package\\s+\\w+\\s*;"
        return Pattern
            .compile(regex)
            .matcher(src)
            .takeIf(Matcher::find)
            ?.replaceFirst { result ->
                result
                    .group()
                    .plus("\n\nimport java.util.Scanner;\n\n")
            } ?: "import java.util.Scanner;\n".plus(src)
    }

    private fun findClassName(src: String): String? {
        val pattern = Pattern.compile("class\\s+(\\w+)")
        val matcher = pattern.matcher(src)
        return if (matcher.find()) matcher.group(1) else null
    }

    private fun findEntryPoint(sourceFiles: List<SourceFile>): SourceFile? {
        val regex = "public\\s+static\\s+void\\s+main\\s*\\(\\s*String\\s*\\[]\\s*args\\s*\\)"
        val pattern = Pattern.compile(regex)

        sourceFiles.forEach { sourceFile ->
            val file = File(sourceFile.pathname)
            val matcher = pattern.matcher(file.readText())
            if (matcher.find()) return sourceFile
        }
        return null
    }

    private fun compileCode(solution: Solution): Pair<Solution, SourceFile?> {
        val sourceFiles =
            solution.sourceFiles.map {
                val file = File(it.pathname)

                if (solution.sourceFiles.size == 1 && file.nameWithoutExtension == "tmp") {
                    // If the solution is submitted as a single file with a temporary name
                    // Then rename it to the class name
                    val className =
                        findClassName(file.readText()) ?: let { _ ->
                            solution.status = VerdictStatus.COMPILE_ERROR
                            solution.report = "No class name found"
                            return Pair(solution, null)
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

        val error =
            process.errorStream
                .bufferedReader()
                .readText()
                .take(1000)

        if (error.isNotEmpty()) {
            solution.status = VerdictStatus.COMPILE_ERROR
            solution.report = error
            return Pair(solution, null)
        }
        val entryPoint =
            findEntryPoint(sourceFiles) ?: let { _ ->
                solution.status = VerdictStatus.COMPILE_ERROR
                solution.report = "No entry point found"
                return Pair(solution, null)
            }
        return Pair(solution, entryPoint)
    }

    private fun runTestCases(
        solution: Solution,
        mainFile: SourceFile,
    ) {
        solution.status = VerdictStatus.RUNNING
        template.convertAndSend("/topic/status", VerdictStatusResponse(solution.id, solution.status))

        solution.problem.testCases.forEach { testCase ->
            val nameWithoutExtension = mainFile.filename.removeSuffix(".java")
            val process =
                ProcessBuilder("java", nameWithoutExtension)
                    .directory(File(mainFile.workingDir))
                    .start()

            process.outputWriter().use { out ->
                out.write(testCase.input ?: "")
                out.flush()
            }
            process.waitFor()

            val error =
                process.errorStream.bufferedReader()
                    .readText()
                    .take(1000)

            if (error.isNotEmpty()) {
                solution.status = VerdictStatus.RUNTIME_ERROR
                solution.report = error
                return
            }
            val expected = testCase.output.split("\n")
            val actual =
                process.inputStream.bufferedReader()
                    .readText()
                    .trim()
            val isPassed = expected.all { actual.contains(it) }

            solution.report =
                solution.report.plus(
                    """
                            |[Test case ${testCase.number}]
                            
                            |Input:
                            |${testCase.input ?: "<empty>"}
                            
                            |Expected:
                            |${testCase.output}
                            
                            |Actual:
                            |$actual
                            |
                            |Result: ${if (isPassed) "Passed" else "Failed"}
                            |============
                            |
                    """.trimMargin(),
                )
            if (!isPassed) {
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
