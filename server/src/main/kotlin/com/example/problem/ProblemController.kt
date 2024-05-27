package com.example.problem

import com.example.common.BaseApiResponse
import com.example.problem.response.ProblemResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/problems")
@CrossOrigin
class ProblemController(
    private val problemService: ProblemService,
) {
    @PostMapping
    fun createProblem(
        @RequestBody problemRequest: com.example.problem.request.ProblemRequest,
    ): ResponseEntity<Unit> {
        problemService.createProblem(problemRequest)
        return ResponseEntity.status(HttpStatus.CREATED).build()
    }

    @GetMapping
    fun getProblems(): BaseApiResponse<ProblemResponse.ProblemList> {
        val problems = problemService.getProblems()
        return BaseApiResponse(problems)
    }

    @GetMapping("/{problemNumber}")
    fun getProblem(
        @PathVariable problemNumber: String,
    ): BaseApiResponse<ProblemResponse.ProblemDetail> {
        val problem = problemService.getProblem(problemNumber)
        return BaseApiResponse(listOf(problem))
    }
}
