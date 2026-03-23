package com.dev.assignment.domain.model

import com.dev.assignment.domain.BaseEntity
import jakarta.persistence.Entity
import jakarta.persistence.Index
import jakarta.persistence.Table

@Entity
@Table(
    name = "model",
    indexes = [
        Index(name = "udx_model_name_api_url", columnList = "name, apiUrl", unique = true)
    ],
)
class Model(
    name: String,
    description: String,
    apiUrl: String,
) : BaseEntity() {
    var name: String = name
        protected set

    var description: String = description
        protected set

    var apiUrl: String = apiUrl
        protected set

    fun update(name: String, description: String, apiUrl: String) {
        this.name = name
        this.description = description
        this.apiUrl = apiUrl
    }
}