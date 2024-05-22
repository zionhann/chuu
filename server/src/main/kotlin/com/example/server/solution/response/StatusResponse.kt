package com.example.server.solution.response

data class StatusResponse(
    val solutionId: Long,
    val sourceCode: String,
    val report: String,
)
