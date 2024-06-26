package com.example.problem

import com.example.problem.dto.TestCaseDTO
import com.example.problem.model.Problem
import com.example.problem.model.TestCase
import com.example.problem.request.ProblemRequest
import com.example.problem.response.ProblemResponse
import org.springframework.stereotype.Service

@Service
class ProblemService(
    private val problemRepository: ProblemRepository,
) {
    fun createProblem(problemRequest: ProblemRequest): Long {
        val problem =
            Problem(
                number = problemRequest.number,
                name = problemRequest.name,
                description = problemRequest.description,
                inputDescription = problemRequest.inputDescription,
                outputDescription = problemRequest.outputDescription,
            ).apply {
                val testCases =
                    problemRequest.testCases.mapIndexed { index, testCase ->
                        TestCase(
                            number = index + 1,
                            input = testCase.input,
                            output = testCase.output,
                            problem = this,
                        )
                    }
                this.testCases.addAll(testCases)
            }
        return problemRepository.save(problem).id
    }

    fun getProblems(): List<ProblemResponse.ProblemList> {
        val problems = problemRepository.findAll().sortedByDescending { it.number }
        return problems.map { problem ->
            ProblemResponse.ProblemList(
                problemNumber = problem.number,
                problemName = problem.name,
            )
        }
    }

    fun getProblem(problemNumber: String): ProblemResponse.ProblemDetail {
        val problem =
            problemRepository.findByNumber(problemNumber) ?: throw IllegalArgumentException("Problem not found")
        return ProblemResponse.ProblemDetail(
            problemNumber = problem.number,
            problemName = problem.name,
            description = problem.description,
            input = problem.inputDescription,
            output = problem.outputDescription,
            testCases =
                problem.testCases.map { testCase ->
                    TestCaseDTO(
                        input = testCase.input,
                        output = testCase.output,
                    )
                },
        )
    }
}
