package com.example.solution

import com.example.problem.model.Problem
import com.example.solution.model.Solution
import org.springframework.data.jpa.repository.JpaRepository

interface SolutionRepository : JpaRepository<Solution, Long> {
    fun findAllByProblem(problem: Problem): List<Solution>
}
