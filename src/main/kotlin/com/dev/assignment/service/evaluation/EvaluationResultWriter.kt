package com.dev.assignment.service.evaluation

import com.dev.assignment.client.evaluation.EvaluationResultRepository
import com.dev.assignment.domain.evaluation.EvaluationResult
import org.springframework.stereotype.Component

@Component
class EvaluationResultWriter(
    private val evaluationResultRepository: EvaluationResultRepository
) {
    fun createEvaluationResults(results: List<EvaluationResult>) {
        evaluationResultRepository.saveAll(results)
    }

    fun deleteEvaluationResult(evaluationJobId: Long) {
        evaluationResultRepository.deleteByEvaluationJobId(evaluationJobId)
    }
}