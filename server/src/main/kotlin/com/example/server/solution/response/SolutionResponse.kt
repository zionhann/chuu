package com.example.server.solution.response

import com.example.server.solution.model.Language
import com.example.server.solution.model.VerdictStatus
import java.time.LocalDateTime

data class SolutionResponse(
    val solutionId: Long,
    val submissionDate: LocalDateTime,
    val author: String,
    val problemNumber: String,
    val language: Language,
    val verdict: VerdictStatus,
)
