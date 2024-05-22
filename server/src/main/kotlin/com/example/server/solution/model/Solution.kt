package com.example.server.solution.model

import com.example.server.common.BaseTime
import com.example.server.problem.model.Problem
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.ManyToOne

@Entity
class Solution(
    @Column
    val author: String,
    @Column
    val language: Language,
    @Column(length = 1020)
    val sourceCode: String,
    @ManyToOne(fetch = FetchType.LAZY)
    val problem: Problem,
    @Column
    @Enumerated(value = EnumType.STRING)
    var status: VerdictStatus = VerdictStatus.PENDING,
) : BaseTime() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L

    @Column
    var report: String = ""
}
