package com.dev.assignment.repository.model

import com.dev.assignment.domain.model.Model
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.jpa.repository.JpaRepository

interface ModelRepository : JpaRepository<Model, Long> {
    fun existsByName(name: String): Boolean
    fun findAllBy(pageable: Pageable): Slice<Model>
}
