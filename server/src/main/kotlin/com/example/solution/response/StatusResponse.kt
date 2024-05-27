package com.example.solution.response

data class StatusResponse(
    val solutionId: Long,
    val sourceCode: List<String>,
    val report: String,
)
