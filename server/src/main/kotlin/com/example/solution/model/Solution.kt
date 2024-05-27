package com.example.solution.model

import com.example.problem.model.Problem
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany

@Entity
class Solution(
    @Column
    val author: String,
    @Column
    val language: Language,
    @ManyToOne(fetch = FetchType.LAZY)
    val problem: Problem,
    @Column
    @Enumerated(value = EnumType.STRING)
    var status: VerdictStatus = VerdictStatus.PENDING,
) : com.example.common.BaseTime() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L

    @Column(length = 1020)
    var report: String = ""

    @OneToMany(mappedBy = "solution", cascade = [CascadeType.ALL])
    val sourceFiles: MutableList<SourceFile> = mutableListOf()
}
