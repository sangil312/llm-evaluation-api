package com.dev.assignment.service.evaluation

import com.dev.assignment.IntegrationTestSupport
import com.dev.assignment.domain.evaluation.EvaluationJob
import com.dev.assignment.repository.evaluation.EvaluationJobRepository
import com.dev.assignment.support.error.CoreException
import com.dev.assignment.support.error.ErrorType
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.springframework.transaction.annotation.Transactional

@Transactional
class EvaluationJobReaderTest(
    private val evaluationJobReader: EvaluationJobReader,
    private val evaluationJobRepository: EvaluationJobRepository
) : IntegrationTestSupport() {

    @Test
    fun findFinishedEvaluationJob() {
        val savedEvaluationJob = evaluationJobRepository.save(
            EvaluationJob.register(
                datasetId = 1L,
                modelName = "mock-gpt"
            ).apply { finish(averageScore = 0.8) }
        )

        val findEvaluationJob = evaluationJobReader.findFinishedEvaluationJob(savedEvaluationJob.id)

        assertThat(findEvaluationJob.id).isEqualTo(savedEvaluationJob.id)
        assertThat(findEvaluationJob.datasetId).isEqualTo(savedEvaluationJob.datasetId)
        assertThat(findEvaluationJob.modelName).isEqualTo(savedEvaluationJob.modelName)
    }

    @Test
    fun findFinishedEvaluationJobWithNotFound() {
        assertThatThrownBy { evaluationJobReader.findFinishedEvaluationJob(9999L) }
            .isInstanceOf(CoreException::class.java)
            .hasMessage(ErrorType.NOT_FOUND_DATA.message)
    }

    @Test
    fun existsEvaluationJob() {
        evaluationJobRepository.save(
            EvaluationJob.register(
                datasetId = 1L,
                modelName = "mock-gpt"
            )
        )

        val existsEvaluationJob = evaluationJobReader.existsEvaluationJob(
            datasetId = 1L,
            modelName = "mock-gpt"
        )

        assertThat(existsEvaluationJob).isTrue
    }

    @Test
    fun existsEvaluationJobWithNotFound() {
        evaluationJobRepository.save(
            EvaluationJob.register(
                datasetId = 1L,
                modelName = "mock-gpt"
            )
        )

        val existsEvaluationJob = evaluationJobReader.existsEvaluationJob(
            datasetId = 1L,
            modelName = "mock-gpt-mini"
        )

        assertThat(existsEvaluationJob).isFalse
    }
}
