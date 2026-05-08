package com.dev.evaluation.client.evaluation

import com.dev.evaluation.domain.evaluation.EvaluationResult
import org.springframework.data.jpa.repository.JpaRepository

interface EvaluationResultRepository : JpaRepository<EvaluationResult, Long> {
    fun deleteByEvaluationJobId(evaluationJobId: Long)

    fun findByDatasetItemIdInAndEvaluationJobId(
        datasetItemIds: Collection<Long>,
        evaluationJobId: Long
    ): List<EvaluationResult>
}
