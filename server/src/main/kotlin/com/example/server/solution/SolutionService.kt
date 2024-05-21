package com.example.server.solution

import com.example.server.problem.ProblemRepository
import com.example.server.solution.model.Solution
import com.example.server.solution.model.VerdictStatus
import com.example.server.solution.request.SearchFilter
import com.example.server.solution.request.SolutionId
import com.example.server.solution.request.SolutionRequest
import com.example.server.solution.response.SolutionResponse
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
            solutionRepository
                .findById(solutionId)
                .orElseThrow(::IllegalArgumentException)
        runCode(solution)
        template.convertAndSend("/topic/status", VerdictStatusResponse(solution.id, solution.status))
    }

    private fun runCode(solution: Solution) {
        val className = findClassName(solution.sourceCode)
        val parentPath = "${solution.author}/${solution.problem.number}/${solution.id}"
        val sourceFile =
            File(parentPath, "$className.java").apply {
                File(parentPath).mkdirs()
                this.writeText(solution.sourceCode)
            }
        println("source code: ${sourceFile.readText()}")
        println("path: ${sourceFile.absolutePath}")

        compileCode(sourceFile, solution)

        if (solution.status != VerdictStatus.COMPILE_ERROR) {
            runTestCases(solution, sourceFile)
        }
    }

    private fun findClassName(src: String): String {
        val pattern = Pattern.compile("class\\s+(\\w+)")
        val matcher = pattern.matcher(src)
        return if (matcher.find()) {
            matcher.group(1)
        } else {
            throw IllegalArgumentException("No class name found in the source code.")
        }
    }

    private fun compileCode(
        sourceFile: File,
        solution: Solution,
    ) {
        println("Compiling...")
        val err = File(sourceFile.parent, "error.txt")
        ProcessBuilder("javac", sourceFile.name)
            .directory(sourceFile.parentFile)
            .redirectError(err)
            .start()
            .waitFor()

        val message = err.readText()
        if (message.isNotEmpty()) {
            println("Compilation error: $message")
            solution.status = VerdictStatus.COMPILE_ERROR
            return
        }
        println("Compilation successful")
    }

    private fun runTestCases(
        solution: Solution,
        sourceFile: File,
    ) {
        solution.status = VerdictStatus.RUNNING
        template.convertAndSend("/topic/status", VerdictStatusResponse(solution.id, solution.status))

        solution.problem.testCases.map { testCase ->
            println("Running on test case ${testCase.number}")
            val outputFile = File(sourceFile.parent, "output.txt").apply { createNewFile() }
            val errorFile = File(sourceFile.parent, "error.txt")
            val process =
                ProcessBuilder("java", sourceFile.nameWithoutExtension)
                    .directory(sourceFile.parentFile)
                    .redirectOutput(ProcessBuilder.Redirect.PIPE)
                    .redirectError(errorFile)
                    .start()

            println("input: ${testCase.input}")
            PrintWriter(process.outputStream).use { out -> out.println(testCase.input) }
            process.waitFor()

            val error = errorFile.readText()

            if (error.isNotEmpty()) {
                println("Runtime error: $error")
                solution.status = VerdictStatus.RUNTIME_ERROR
                return
            }
            val output = process.inputStream.bufferedReader().readText()
            outputFile.writeText(
                outputFile.readText() +
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
                outputFile.writeText(
                    outputFile.readText() +
                        """
                        |Result: Passed
                        |============
                        """.trimMargin(),
                )
                println("Test case  ${testCase.number} passed")
            } else {
                outputFile.writeText(
                    outputFile.readText() +
                        """
                        |Result: Failed
                        |============
                        """.trimMargin(),
                )
                println("Test case ${testCase.number} failed")
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
}
