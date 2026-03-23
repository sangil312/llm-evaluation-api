package com.dev.assignment.service.evaluation

import com.dev.assignment.domain.evaluation.EvaluationResult
import org.springframework.stereotype.Service

@Service
class EvaluationResultService(
    private val evaluationResultReader: EvaluationResultReader
) {
    fun findEvaluationResults(
        evaluationJobId: Long,
        datasetItemIds: Collection<Long>
    ): List<EvaluationResult> {
        return evaluationResultReader.findEvaluationResults(evaluationJobId, datasetItemIds)
    }
}