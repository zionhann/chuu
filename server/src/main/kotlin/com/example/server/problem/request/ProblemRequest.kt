package com.example.server.problem.request

import com.example.server.problem.dto.TestCaseDTO

data class ProblemRequest(
    val number: String,
    val name: String,
    val description: String,
    val inputDescription: String,
    val outputDescription: String,
    val testCases: List<TestCaseDTO<List<String>>>,
)
