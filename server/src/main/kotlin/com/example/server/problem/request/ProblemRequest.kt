package com.example.server.problem.request

data class ProblemRequest(
    val code: String,
    val name: String,
    val description: String,
    val inputDescription: String,
    val outputDescription: String,
    val testCases: List<TestCaseRequest>,
) {
    class TestCaseRequest(
        val input: List<String>,
        val output: String,
    )
}
