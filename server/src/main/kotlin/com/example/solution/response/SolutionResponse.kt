package com.example.solution.response

import com.example.solution.model.Language
import com.example.solution.model.VerdictStatus
import java.time.LocalDateTime

data class SolutionResponse(
    val solutionId: Long,
    val submissionDate: LocalDateTime,
    val author: String,
    val problemNumber: String,
    val language: Language,
    val verdict: VerdictStatus,
)
