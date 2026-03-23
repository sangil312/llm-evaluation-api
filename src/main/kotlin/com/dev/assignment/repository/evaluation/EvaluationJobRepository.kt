package com.dev.assignment.repository.evaluation

import com.dev.assignment.domain.evaluation.EvaluationJob
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query

interface EvaluationJobRepository : JpaRepository<EvaluationJob, Long> {
    @Query(
        value = """
            select id
            from evaluation_job
            where status = :pendingStatus
            order by id asc
            limit :limit
            for update skip locked
        """,
        nativeQuery = true
    )
    fun findIdsByPendingJobs(
        pendingStatus: String,
        limit: Int
    ): List<Long>

    fun existsByDatasetIdAndModelName(datasetId: Long, modelName: String): Boolean

    @Modifying
    @Query(
        value = """
            update evaluation_job
            set status = :updateStatus,
                error_message = null,
                updated_at = CURRENT_TIMESTAMP
            where id in (:evaluationJobIds)
              and status = :conditionStatus
        """,
        nativeQuery = true
    )
    fun updateEvaluationJobStatusByIds(
        evaluationJobIds: List<Long>,
        conditionStatus: String,
        updateStatus: String
    )
}
