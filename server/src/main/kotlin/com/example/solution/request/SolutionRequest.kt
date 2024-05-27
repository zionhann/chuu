package com.example.solution.request

import com.example.solution.model.Language

data class SolutionRequest(
    val author: String,
    val language: Language,
)
