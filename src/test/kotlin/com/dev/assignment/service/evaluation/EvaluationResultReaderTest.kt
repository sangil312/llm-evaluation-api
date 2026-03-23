package com.dev.assignment.service.evaluation

import com.dev.assignment.IntegrationTestSupport
import com.dev.assignment.client.evaluation.EvaluationResultRepository
import com.dev.assignment.domain.evaluation.EvaluationResult
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.transaction.annotation.Transactional

@Transactional
class EvaluationResultReaderTest(
    private val evaluationResultRepository: EvaluationResultRepository,
    private val evaluationResultReader: EvaluationResultReader
) : IntegrationTestSupport() {

    @Test
    fun findEvaluationResults() {
        val evaluationResult1 = EvaluationResult(evaluationJobId = 1L, datasetItemId = 1L, score = 0.1)
        val evaluationResult2 = EvaluationResult(evaluationJobId = 1L, datasetItemId = 2L, score = 0.2)
        val otherEvaluationResult = EvaluationResult(evaluationJobId = 2L, datasetItemId = 3L, score = 0.3)

        evaluationResultRepository.saveAll(listOf(evaluationResult1, evaluationResult2, otherEvaluationResult))

        val evaluationResults = evaluationResultReader.findEvaluationResults(
            evaluationJobId = 1L,
            datasetItemIds = listOf(1L, 2L)
        )

        assertThat(evaluationResults).hasSize(2)
        assertThat(evaluationResults.map { it.evaluationJobId }).containsOnly(1L)
        assertThat(evaluationResults.map { it.datasetItemId }).containsExactlyInAnyOrder(1L, 2L)
        assertThat(evaluationResults.map { it.score }).containsExactlyInAnyOrder(0.1, 0.2)
    }
}
