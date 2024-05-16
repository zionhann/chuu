package com.example.server.problem.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import org.hibernate.annotations.Cascade
import org.hibernate.annotations.CascadeType

@Entity
class Problem(
    @Column(unique = true)
    var code: String,
    @Column(length = 1020)
    var description: String,
    @Column
    var inputDescription: String,
    @Column
    var outputDescription: String,
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L

    @OneToMany
    @Cascade(CascadeType.ALL)
    val testCases: MutableList<TestCase> = mutableListOf()
}
