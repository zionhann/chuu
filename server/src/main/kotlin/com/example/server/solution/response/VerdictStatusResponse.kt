package com.example.server.solution.response

import com.example.server.solution.model.VerdictStatus

data class VerdictStatusResponse(
    val solutionId: Long,
    val verdict: VerdictStatus,
)
