package com.dev.assignment.service.evaluation

import com.dev.assignment.IntegrationTestSupport
import com.dev.assignment.domain.evaluation.EvaluationJob
import com.dev.assignment.domain.evaluation.EvaluationJobStatus
import com.dev.assignment.repository.evaluation.EvaluationJobRepository
import com.dev.assignment.support.error.CoreException
import com.dev.assignment.support.error.ErrorType
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.springframework.transaction.annotation.Transactional

@Transactional
class EvaluationJobWriterTest(
    private val evaluationJobWriter: EvaluationJobWriter,
    private val evaluationJobRepository: EvaluationJobRepository
) : IntegrationTestSupport() {

    @Test
    fun createEvaluationJob() {
        val evaluationJob = evaluationJobWriter.createEvaluationJob(datasetId = 1L, modelName = "mock-gpt")

        val findEvaluationJob = evaluationJobRepository.findById(evaluationJob.id).orElseThrow()

        assertThat(findEvaluationJob.datasetId).isEqualTo(1L)
        assertThat(findEvaluationJob.modelName).isEqualTo("mock-gpt")
        assertThat(findEvaluationJob.status).isEqualTo(EvaluationJobStatus.PENDING)
        assertThat(findEvaluationJob.averageScore).isEqualTo(0.0)
        assertThat(findEvaluationJob.errorMessage).isNull()
    }

    @Test
    fun updatePendingJobs() {
        val pendingJob1 = evaluationJobRepository.save(EvaluationJob.register(datasetId = 1L, modelName = "model-1"))
        val pendingJob2 = evaluationJobRepository.save(EvaluationJob.register(datasetId = 1L, modelName = "model-2"))

        val finishedJob = evaluationJobRepository.save(
            EvaluationJob(
                datasetId = 1L,
                modelName = "model-4",
                averageScore = 0.0,
                status = EvaluationJobStatus.FINISHED
            )
        )

        flushAndClear()

        val updatedJobIds = evaluationJobWriter.updatePendingJobs(limit = 2)

        val updatedJob1 = evaluationJobRepository.findById(pendingJob1.id).orElseThrow()
        val updatedJob2 = evaluationJobRepository.findById(pendingJob2.id).orElseThrow()
        val findFinishedJob = evaluationJobRepository.findById(finishedJob.id).orElseThrow()

        assertThat(updatedJobIds).containsExactlyInAnyOrder(pendingJob1.id, pendingJob2.id)
        assertThat(updatedJob1.status).isEqualTo(EvaluationJobStatus.RUNNING)
        assertThat(updatedJob2.status).isEqualTo(EvaluationJobStatus.RUNNING)
        assertThat(findFinishedJob.status).isEqualTo(EvaluationJobStatus.FINISHED)
    }

    @Test
    fun updatePendingJobsWithNoPendingJob() {
        evaluationJobRepository.save(
            EvaluationJob(
                datasetId = 1L,
                modelName = "model-1",
                averageScore = 0.0,
                status = EvaluationJobStatus.RUNNING
            )
        )

        flushAndClear()

        val updatedJobIds = evaluationJobWriter.updatePendingJobs(limit = 1)

        assertThat(updatedJobIds).isEmpty()
    }

    @Test
    fun evaluationJobFinish() {
        val savedEvaluationJob = evaluationJobRepository.save(EvaluationJob.register(datasetId = 1L, modelName = "mock-gpt"))

        evaluationJobWriter.evaluationJobFinish(savedEvaluationJob.id, averageScore = 0.87)

        val findEvaluationJob = evaluationJobRepository.findById(savedEvaluationJob.id).orElseThrow()

        assertThat(findEvaluationJob.status).isEqualTo(EvaluationJobStatus.FINISHED)
        assertThat(findEvaluationJob.averageScore).isEqualTo(0.87)
        assertThat(findEvaluationJob.errorMessage).isNull()
    }

    @Test
    fun evaluationJobFinishWithNotFound() {
        assertThatThrownBy { evaluationJobWriter.evaluationJobFinish(evaluationJobId = 99999L, averageScore = 0.5) }
            .isInstanceOf(CoreException::class.java)
            .hasMessage(ErrorType.NOT_FOUND_DATA.message)
    }

    @Test
    fun evaluationJobFail() {
        val savedEvaluationJob = evaluationJobRepository.save(EvaluationJob.register(datasetId = 1L, modelName = "mock-gpt"))

        evaluationJobWriter.evaluationJobFail(savedEvaluationJob.id, errorMessage = "error")

        val findEvaluationJob = evaluationJobRepository.findById(savedEvaluationJob.id).orElseThrow()

        assertThat(findEvaluationJob.status).isEqualTo(EvaluationJobStatus.FAILED)
        assertThat(findEvaluationJob.errorMessage).isEqualTo("error")
    }

    @Test
    fun evaluationJobFailWithNotFound() {
        assertThatThrownBy { evaluationJobWriter.evaluationJobFail(evaluationJobId = 99999L, errorMessage = "error") }
            .isInstanceOf(CoreException::class.java)
            .hasMessage(ErrorType.NOT_FOUND_DATA.message)
    }
}
