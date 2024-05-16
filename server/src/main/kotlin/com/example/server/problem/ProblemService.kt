package com.example.server.problem

import com.example.server.problem.model.Problem
import com.example.server.problem.model.TestCase
import com.example.server.problem.request.ProblemRequest
import org.springframework.stereotype.Service

@Service
class ProblemService(
    private val problemRepository: ProblemRepository,
) {
    fun createProblem(problemRequest: ProblemRequest): Long {
        val problem =
            Problem(
                code = problemRequest.code,
                description = problemRequest.description,
                inputDescription = problemRequest.inputDescription,
                outputDescription = problemRequest.outputDescription,
            ).apply {
                val testCases =
                    problemRequest.testCases.mapIndexed { index, testCaseRequest ->
                        TestCase(
                            number = index + 1,
                            input = testCaseRequest.input.joinToString("\n"),
                            output = testCaseRequest.output,
                            problem = this,
                        )
                    }
                this.testCases.addAll(testCases)
            }
        return problemRepository.save(problem).id
    }
}
