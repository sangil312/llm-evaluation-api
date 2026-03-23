package com.dev.assignment.repository.evaluation

import com.dev.assignment.IntegrationTestSupport
import com.dev.assignment.domain.evaluation.EvaluationJob
import com.dev.assignment.domain.evaluation.EvaluationJobStatus
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.transaction.annotation.Transactional

@Transactional
class EvaluationJobRepositoryTest(
    private val evaluationJobRepository: EvaluationJobRepository
) : IntegrationTestSupport() {

    @Test
    fun findIdsByPendingJobs() {
        val pendingJob1 = evaluationJobRepository.save(EvaluationJob.register(datasetId = 1L, modelName = "mock-gpt"))
        val pendingJob2 = evaluationJobRepository.save(EvaluationJob.register(datasetId = 1L, modelName = "mock-gemini"))
        val pendingJob3 = evaluationJobRepository.save(EvaluationJob.register(datasetId = 1L, modelName = "mock-claude"))

        val runningJob = EvaluationJob(
            datasetId = 1L,
            modelName = "model-1",
            averageScore = 0.0,
            status = EvaluationJobStatus.RUNNING
        )

        evaluationJobRepository.save(runningJob)

        val pendingJobIds = evaluationJobRepository.findIdsByPendingJobs(
            pendingStatus = EvaluationJobStatus.PENDING.name,
            limit = 2
        )

        assertThat(pendingJobIds).containsExactly(pendingJob1.id, pendingJob2.id)
        assertThat(pendingJobIds).doesNotContain(runningJob.id, pendingJob3.id)
    }

    @Test
    fun updateEvaluationJobStatusByIds() {
        val pendingJob1 = evaluationJobRepository.save(EvaluationJob.register(datasetId = 2L, modelName = "mock-gpt"))
        val pendingJob2 = evaluationJobRepository.save(EvaluationJob.register(datasetId = 2L, modelName = "mock-claude"))
        val failedJob = evaluationJobRepository.save(
            EvaluationJob.register(datasetId = 2L, modelName = "model-1").apply { fail("error") }
        )
        val pendingJobNotInTarget = evaluationJobRepository.save(
            EvaluationJob.register(datasetId = 2L, modelName = "model-2")
        )

        flushAndClear()

        evaluationJobRepository.updateEvaluationJobStatusByIds(
            evaluationJobIds = listOf(pendingJob1.id, pendingJob2.id, failedJob.id),
            conditionStatus = EvaluationJobStatus.PENDING.name,
            updateStatus = EvaluationJobStatus.RUNNING.name
        )

        val updatedJob1 = evaluationJobRepository.findById(pendingJob1.id).orElseThrow()
        val updatedJob2 = evaluationJobRepository.findById(pendingJob2.id).orElseThrow()
        val notUpdatedFailedJob = evaluationJobRepository.findById(failedJob.id).orElseThrow()
        val notTargetJob = evaluationJobRepository.findById(pendingJobNotInTarget.id).orElseThrow()

        assertThat(updatedJob1.status).isEqualTo(EvaluationJobStatus.RUNNING)
        assertThat(updatedJob2.status).isEqualTo(EvaluationJobStatus.RUNNING)
        assertThat(notUpdatedFailedJob.status).isEqualTo(EvaluationJobStatus.FAILED)
        assertThat(notUpdatedFailedJob.errorMessage).isEqualTo("error")
        assertThat(notTargetJob.status).isEqualTo(EvaluationJobStatus.PENDING)
    }
}
