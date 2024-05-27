package com.example.problem

import com.example.problem.model.Problem
import org.springframework.data.jpa.repository.JpaRepository

interface ProblemRepository : JpaRepository<Problem, Long> {
    fun findByNumber(problemNumber: String): Problem?
}
