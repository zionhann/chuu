package com.example.server.solution

import com.example.server.common.BaseApiResponse
import com.example.server.solution.request.SearchFilter
import com.example.server.solution.request.SolutionId
import com.example.server.solution.request.SolutionRequest
import com.example.server.solution.response.SolutionResponse
import com.example.server.solution.response.StatusResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/solutions")
@CrossOrigin
class SolutionController(
    private val solutionService: SolutionService,
) {
    @PostMapping("/{solutionId}/grade")
    fun startGrading(
        @PathVariable solutionId: Long,
    ) {
        solutionService.startGrading(solutionId)
    }

    @PostMapping("/{problemNumber}")
    fun submit(
        @PathVariable problemNumber: String,
        @RequestBody submission: SolutionRequest,
    ): ResponseEntity<BaseApiResponse<SolutionId>> {
        val solutionId = solutionService.submitSolution(problemNumber, submission)
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(BaseApiResponse(listOf(solutionId)))
    }

    @GetMapping
    fun getSolutions(
        @ModelAttribute searchFilter: SearchFilter,
    ): BaseApiResponse<SolutionResponse> {
        val solutions = solutionService.getSolutions(searchFilter)
        return BaseApiResponse(solutions)
    }

    @GetMapping("/{solutionId}/status")
    fun getStatusDetail(
        @PathVariable solutionId: Long,
    ): BaseApiResponse<StatusResponse> {
        val solution = solutionService.getStatusOf(solutionId)
        return BaseApiResponse(listOf(solution))
    }
}
