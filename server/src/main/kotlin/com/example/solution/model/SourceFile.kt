package com.example.solution.model

import com.example.common.BaseTime
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.ManyToOne

@Entity
class SourceFile(
    @Column
    var workingDir: String,
    @Column
    var filename: String,
    @Column
    var pathname: String,
    @ManyToOne(fetch = FetchType.LAZY)
    val solution: Solution,
) : BaseTime() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L
}
