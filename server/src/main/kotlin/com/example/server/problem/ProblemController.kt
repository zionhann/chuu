package com.example.server.problem

import com.example.server.problem.request.ProblemRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/problems")
class ProblemController(
    private val problemService: ProblemService,
) {
    @PostMapping
    fun createProblem(
        @RequestBody problemRequest: ProblemRequest,
    ): ResponseEntity<Unit> {
        problemService.createProblem(problemRequest)
        return ResponseEntity.status(HttpStatus.CREATED).build()
    }
}
