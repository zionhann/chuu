package com.example.problem.request

import com.example.problem.dto.TestCaseDTO

data class ProblemRequest(
    val number: String,
    val name: String,
    val description: String,
    val inputDescription: String,
    val outputDescription: String,
    val testCases: List<TestCaseDTO>,
)
