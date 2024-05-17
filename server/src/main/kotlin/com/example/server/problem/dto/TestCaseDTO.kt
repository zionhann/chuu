package com.example.server.problem.dto

data class TestCaseDTO<T>(
    val input: T,
    val output: String,
)
