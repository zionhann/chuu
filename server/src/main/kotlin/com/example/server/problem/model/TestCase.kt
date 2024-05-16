package com.example.server.problem.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.ManyToOne

@Entity
class TestCase(
    @Column
    val number: Int,
    @Column
    var input: String,
    @Column
    var output: String,
    @ManyToOne(fetch = FetchType.LAZY)
    var problem: Problem,
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L
}
