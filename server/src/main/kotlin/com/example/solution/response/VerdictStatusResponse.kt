package com.example.solution.response

import com.example.solution.model.VerdictStatus

data class VerdictStatusResponse(
    val solutionId: Long,
    val verdict: VerdictStatus,
)
