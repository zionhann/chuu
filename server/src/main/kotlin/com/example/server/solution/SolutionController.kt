package com.example.server.solution

import com.example.server.common.BaseApiResponse
import com.example.server.solution.request.SearchFilter
import com.example.server.solution.request.SolutionId
import com.example.server.solution.request.SolutionRequest
import com.example.server.solution.response.SolutionResponse
import com.example.server.solution.response.StatusResponse
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

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

    @PostMapping("/{problemNumber}", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun submit(
        @PathVariable problemNumber: String,
        @ModelAttribute metadata: SolutionRequest,
        @RequestParam(required = false) sourceCode: String?,
        @RequestParam(required = false) sourceFiles: List<MultipartFile> = mutableListOf(),
    ): ResponseEntity<BaseApiResponse<SolutionId>> {
        val solutionId =
            sourceCode?.let {
                solutionService.submitSolution(problemNumber, metadata, it)
            } ?: let { _ ->
                println("sourceFiles: $sourceFiles")
                solutionService.submitSolution(problemNumber, metadata, sourceFiles)
            }
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
