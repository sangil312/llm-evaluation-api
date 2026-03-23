package com.dev.assignment.domain.evaluation

import com.dev.assignment.domain.BaseEntity
import jakarta.persistence.Entity
import jakarta.persistence.Index
import jakarta.persistence.Table

@Entity
@Table(
    name = "evaluation_result",
    indexes = [
        Index(name = "udx_evaluation_job_id_dataset_item_id", columnList = "evaluation_job_id, dataset_item_id", unique = true)
    ]
)
class EvaluationResult(
    val evaluationJobId: Long,
    val datasetItemId: Long,
    val score: Double,
) : BaseEntity() {
}