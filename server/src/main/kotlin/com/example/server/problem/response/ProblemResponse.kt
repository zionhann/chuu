package com.example.server.problem.response

class ProblemResponse {
    data class ProblemList(
        val problemCode: String,
        val problemName: String,
    )
}
