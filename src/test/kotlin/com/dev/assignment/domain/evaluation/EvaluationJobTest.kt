package com.dev.assignment.domain.evaluation

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class EvaluationJobTest {
    @Test
    fun register() {
        val datasetId = 20L
        val modelName = "gpt"

        val evaluationJob = EvaluationJob.register(datasetId, modelName)

        assertThat(evaluationJob.datasetId).isEqualTo(datasetId)
        assertThat(evaluationJob.modelName).isEqualTo(modelName)
        assertThat(evaluationJob.averageScore).isEqualTo(0.0)
        assertThat(evaluationJob.status).isEqualTo(EvaluationJobStatus.PENDING)
        assertThat(evaluationJob.errorMessage).isNull()
    }

    @Test
    fun finish() {
        val evaluationJob = EvaluationJob(
            datasetId = 3L,
            modelName = "mock-gpt",
            averageScore = 0.0,
            status = EvaluationJobStatus.RUNNING
        )

        evaluationJob.finish(0.72)

        assertThat(evaluationJob.status).isEqualTo(EvaluationJobStatus.FINISHED)
        assertThat(evaluationJob.averageScore).isEqualTo(0.72)
        assertThat(evaluationJob.errorMessage).isNull()
    }

    @Test
    fun fail() {
        val evaluationJob = EvaluationJob(
            datasetId = 3L,
            modelName = "mock-gpt",
            averageScore = 0.0,
            status = EvaluationJobStatus.RUNNING
        )

        evaluationJob.fail("timeout")

        assertThat(evaluationJob.status).isEqualTo(EvaluationJobStatus.FAILED)
        assertThat(evaluationJob.errorMessage).isEqualTo("timeout")
    }
}
