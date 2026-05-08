package com.dev.evaluation.service.evaluation

import com.dev.evaluation.client.evaluation.EvaluationResultRepository
import com.dev.evaluation.domain.evaluation.EvaluationResult
import org.springframework.stereotype.Component

@Component
class EvaluationResultReader(
    private val evaluationResultRepository: EvaluationResultRepository
) {
    fun findEvaluationResults(
        evaluationJobId: Long,
        datasetItemIds: Collection<Long>
    ): List<EvaluationResult> {
        val evaluationResults = evaluationResultRepository.findByDatasetItemIdInAndEvaluationJobId(
            evaluationJobId = evaluationJobId,
            datasetItemIds = datasetItemIds
        )

        return evaluationResults
    }
}