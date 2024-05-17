package com.example.server.problem

import com.example.server.problem.model.Problem
import org.springframework.data.jpa.repository.JpaRepository

interface ProblemRepository : JpaRepository<Problem, Long> {
    fun findByNumber(problemNumber: String): Problem?
}
