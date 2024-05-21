package com.example.server.solution

import com.example.server.problem.model.Problem
import com.example.server.solution.model.Solution
import org.springframework.data.jpa.repository.JpaRepository

interface SolutionRepository : JpaRepository<Solution, Long> {
    fun findAllByProblem(problem: Problem): List<Solution>
}
