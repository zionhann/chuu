package com.example.problem.response

import com.example.problem.dto.TestCaseDTO

class ProblemResponse {
    data class ProblemList(
        val problemNumber: String,
        val problemName: String,
    )

    data class ProblemDetail(
        val problemNumber: String,
        val problemName: String,
        val description: String,
        val input: String,
        val output: String,
        val testCases: List<TestCaseDTO>,
    )
}
