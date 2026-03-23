package com.dev.assignment.service.evaluation

import com.dev.assignment.IntegrationTestSupport
import com.dev.assignment.client.evaluation.EvaluationResultRepository
import com.dev.assignment.domain.evaluation.EvaluationJob
import com.dev.assignment.domain.evaluation.EvaluationResult
import com.dev.assignment.repository.evaluation.EvaluationJobRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.transaction.annotation.Transactional

@Transactional
class EvaluationResultWriterTest(
    private val evaluationResultWriter: EvaluationResultWriter,
    private val evaluationJobRepository: EvaluationJobRepository,
    private val evaluationResultRepository: EvaluationResultRepository
) : IntegrationTestSupport() {

    @Test
    fun createEvaluationResults() {
        val savedEvaluationJob = evaluationJobRepository.save(EvaluationJob.register(datasetId = 1L, modelName = "mock-gpt"))

        evaluationResultWriter.createEvaluationResults(
            listOf(
                EvaluationResult(evaluationJobId = savedEvaluationJob.id, datasetItemId = 10L, score = 0.1),
                EvaluationResult(evaluationJobId = savedEvaluationJob.id, datasetItemId = 11L, score = 0.2)
            )
        )

        val findResults = evaluationResultRepository.findAll()

        assertThat(findResults).hasSize(2)
        assertThat(findResults.map { it.evaluationJobId }).containsOnly(savedEvaluationJob.id)
        assertThat(findResults.map { it.datasetItemId }).containsExactlyInAnyOrder(10L, 11L)
        assertThat(findResults.map { it.score }).containsExactlyInAnyOrder(0.1, 0.2)
    }

    @Test
    fun deleteEvaluationResult() {
        val targetEvaluationJob = evaluationJobRepository.save(EvaluationJob.register(datasetId = 1L, modelName = "mock-gpt"))
        val evaluationJob = evaluationJobRepository.save(EvaluationJob.register(datasetId = 1L, modelName = "mock-claude"))

        evaluationResultRepository.saveAll(
            listOf(
                EvaluationResult(evaluationJobId = targetEvaluationJob.id, datasetItemId = 10L, score = 0.1),
                EvaluationResult(evaluationJobId = targetEvaluationJob.id, datasetItemId = 11L, score = 0.2),
                EvaluationResult(evaluationJobId = evaluationJob.id, datasetItemId = 12L, score = 0.3)
            )
        )

        evaluationResultWriter.deleteEvaluationResult(targetEvaluationJob.id)

        val remainResults = evaluationResultRepository.findAll()

        assertThat(remainResults).hasSize(1)
        assertThat(remainResults[0].evaluationJobId).isEqualTo(evaluationJob.id)
        assertThat(remainResults[0].datasetItemId).isEqualTo(12L)
    }
}