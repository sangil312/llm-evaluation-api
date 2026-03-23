package com.dev.assignment.domain.evaluation

import com.dev.assignment.domain.BaseEntity
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Index
import jakarta.persistence.Table

@Entity
@Table(
    name = "evaluation_job",
    indexes = [
        Index(name = "udx_dataset_id_model_name", columnList = "dataset_id, model_name", unique = true)
    ]
)
class EvaluationJob(
    val datasetId: Long,
    val modelName: String,
    averageScore: Double,
    status: EvaluationJobStatus,
    errorMessage: String? = null,
) : BaseEntity() {
    var averageScore: Double = averageScore
        protected set

    @Enumerated(EnumType.STRING)
    var status: EvaluationJobStatus = status
        protected set

    var errorMessage: String? = errorMessage
        protected set

    companion object {
        fun register(datasetId: Long, modelName: String): EvaluationJob {
            return EvaluationJob(
                datasetId = datasetId,
                modelName = modelName,
                averageScore = 0.0,
                status = EvaluationJobStatus.PENDING
            )
        }
    }

    fun finish(averageScore: Double) {
        this.averageScore = averageScore
        this.status = EvaluationJobStatus.FINISHED
    }

    fun fail(errorMessage: String?) {
        this.status = EvaluationJobStatus.FAILED
        this.errorMessage = errorMessage
    }
}
