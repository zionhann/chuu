package com.example.server.solution.request

import com.example.server.solution.model.Language

data class SolutionRequest(
    val author: String,
    val language: Language,
    val sourceCode: String,
)
