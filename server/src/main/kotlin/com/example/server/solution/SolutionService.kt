package com.example.server.solution

import com.example.server.problem.ProblemRepository
import com.example.server.solution.model.Solution
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
import java.io.File
import java.io.PrintWriter
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
        compileCode(solution)?.let { file ->
            runTestCases(solution, file)
        }
        template.convertAndSend("/topic/status", VerdictStatusResponse(solution.id, solution.status))
    }

    private fun findEntryPoint(src: String): String? {
        val pattern = Pattern.compile("class\\s+(\\w+)")
        val matcher = pattern.matcher(src)
        return if (matcher.find()) matcher.group(1) else null
    }

    private fun compileCode(solution: Solution): File? {
        val className =
            findEntryPoint(solution.sourceCode) ?: run {
                solution.status = VerdictStatus.COMPILE_ERROR
                solution.report = "No entry point found"
                return null
            }
        val parentPath = "${solution.author}/${solution.problem.number}/${solution.id}"
        val sourceFile =
            File(parentPath, "$className.java").apply {
                File(parentPath).mkdirs()
                writeText(solution.sourceCode)
            }

        val process =
            ProcessBuilder("javac", sourceFile.name)
                .directory(sourceFile.parentFile)
                .redirectError(ProcessBuilder.Redirect.PIPE)
                .start()

        process.waitFor()
        process.inputStream.bufferedReader()
            .readText()
            .let { output ->
                if (output.isNotEmpty()) {
                    solution.status = VerdictStatus.COMPILE_ERROR
                    solution.report = output
                    return null
                }
            }
        return sourceFile
    }

    private fun runTestCases(
        solution: Solution,
        sourceFile: File,
    ) {
        solution.status = VerdictStatus.RUNNING
        template.convertAndSend("/topic/status", VerdictStatusResponse(solution.id, solution.status))

        solution.problem.testCases.map { testCase ->
            val err = File.createTempFile("report", null)
            val process =
                ProcessBuilder("java", sourceFile.nameWithoutExtension)
                    .directory(sourceFile.parentFile)
                    .redirectOutput(ProcessBuilder.Redirect.PIPE)
                    .redirectError(err)
                    .start()

            PrintWriter(process.outputStream).use { out -> out.println(testCase.input) }
            process.waitFor()

            val error = err.readText()

            if (error.isNotEmpty()) {
                solution.status = VerdictStatus.RUNTIME_ERROR
                solution.report = error
                return
            }
            val output = process.inputStream.bufferedReader().readText()
            solution.report.plus(
                """
                            |[Test case ${testCase.number}]
                            
                            |Input:
                            |${testCase.input}
                            
                            |Expected:
                            |${testCase.output}
                            
                            |Actual:
                            |$output
                            |
                """.trimMargin(),
            )

            if (output.contains(testCase.output)) {
                solution.report.plus(
                    """
                                    |Result: Passed
                                    |============
                    """.trimMargin(),
                )
            } else {
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

    fun submitSolution(
        problemNumber: String,
        submission: SolutionRequest,
    ): SolutionId {
        val problem =
            problemRepository.findByNumber(problemNumber) ?: throw IllegalArgumentException("Problem not found")
        val solution =
            Solution(
                author = submission.author,
                language = submission.language,
                sourceCode = submission.sourceCode,
                problem = problem,
            )
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
            sourceCode = solution.sourceCode,
            report = solution.report,
        )
    }
}
