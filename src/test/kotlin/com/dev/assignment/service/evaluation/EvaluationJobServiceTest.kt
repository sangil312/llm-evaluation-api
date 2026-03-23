package com.dev.assignment.service.evaluation

import com.dev.assignment.domain.evaluation.EvaluationJob
import com.dev.assignment.support.error.CoreException
import com.dev.assignment.support.error.ErrorType
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import io.mockk.verifySequence
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test

class EvaluationJobServiceTest {
    private val evaluationJobWriter: EvaluationJobWriter = mockk()
    private val evaluationJobReader: EvaluationJobReader = mockk()
    private val evaluationJobProcessor: EvaluationJobProcessor = mockk()

    private val evaluationJobService = EvaluationJobService(
        evaluationJobWriter = evaluationJobWriter,
        evaluationJobReader = evaluationJobReader,
        evaluationJobProcessor = evaluationJobProcessor,
    )

    @Test
    fun createEvaluationJob() {
        val evaluationJob = EvaluationJob.register(datasetId = 1L, modelName = "mock-gpt")

        every { evaluationJobReader.existsEvaluationJob(1L, "mock-gpt") } returns false
        every { evaluationJobWriter.createEvaluationJob(1L, "mock-gpt") } returns evaluationJob

        val result = evaluationJobService.createEvaluationJob(datasetId = 1L, modelName = "mock-gpt")

        assertThat(result).isSameAs(evaluationJob)
        verifySequence {
            evaluationJobReader.existsEvaluationJob(1L, "mock-gpt")
            evaluationJobWriter.createEvaluationJob(1L, "mock-gpt")
        }
    }

    @Test
    fun createEvaluationJobWithAlreadyExists() {
        every { evaluationJobReader.existsEvaluationJob(1L, "mock-gpt") } returns true

        assertThatThrownBy {
            evaluationJobService.createEvaluationJob(datasetId = 1L, modelName = "mock-gpt")
        }
            .isInstanceOf(CoreException::class.java)
            .hasMessage(ErrorType.ALREADY_CREATED_EVALUATION.message)

        verify(exactly = 1) { evaluationJobReader.existsEvaluationJob(1L, "mock-gpt") }
        verify(exactly = 0) { evaluationJobWriter.createEvaluationJob(any(), any()) }
    }

    @Test
    fun runPendingEvaluationJobs() {
        val limit = 10

        every { evaluationJobWriter.updatePendingJobs(10) } returns listOf(10L, 20L, 30L)
        every { evaluationJobProcessor.run(any()) } just runs

        evaluationJobService.runPendingEvaluationJobs(limit)

        verifySequence {
            evaluationJobWriter.updatePendingJobs(limit)
            evaluationJobProcessor.run(10L)
            evaluationJobProcessor.run(20L)
            evaluationJobProcessor.run(30L)
        }
    }

    @Test
    fun runPendingJobsWithNoPendingEvaluationJob() {
        val limit = 10

        every { evaluationJobWriter.updatePendingJobs(limit) } returns emptyList()

        evaluationJobService.runPendingEvaluationJobs(limit)

        verify(exactly = 1) { evaluationJobWriter.updatePendingJobs(limit) }
        verify(exactly = 0) { evaluationJobProcessor.run(any()) }
    }
}
