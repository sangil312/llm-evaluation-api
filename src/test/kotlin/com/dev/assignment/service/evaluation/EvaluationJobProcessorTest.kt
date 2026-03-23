package com.dev.assignment.service.evaluation

import com.dev.assignment.domain.dataset.DatasetItem
import com.dev.assignment.domain.evaluation.EvaluationJob
import com.dev.assignment.client.evaluation.EvaluationApiException
import com.dev.assignment.client.evaluation.EvaluationClient
import com.dev.assignment.service.dataset.DatasetService
import com.dev.assignment.support.response.Page
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import org.junit.jupiter.api.Test

class EvaluationJobProcessorTest {
    private val evaluationJobReader: EvaluationJobReader = mockk()
    private val evaluationJobWriter: EvaluationJobWriter = mockk()
    private val evaluationResultWriter: EvaluationResultWriter = mockk()
    private val datasetService: DatasetService = mockk()
    private val evaluationClient: EvaluationClient = mockk()

    private val evaluationJobProcessor = EvaluationJobProcessor(
        evaluationJobReader = evaluationJobReader,
        evaluationJobWriter = evaluationJobWriter,
        evaluationResultWriter = evaluationResultWriter,
        datasetService = datasetService,
        evaluationClient = evaluationClient,
        batchSize = 100
    )

    @Test
    fun process() {
        val responseBody = """{"error":{"type":"invalid_request_error","message":"Missing required parameter: model"}}"""
        val evaluationJob = EvaluationJob.register(datasetId = 1L, modelName = "mock-gpt")
        val datasetItem = DatasetItem(
            datasetId = 1L,
            query = "query",
            response = "response",
            sequenceNo = 1L
        )

        every { evaluationJobReader.findEvaluationJob(10L) } returns evaluationJob
        every { datasetService.findDatasetItems(1L, 0L, 100) } returns Page(listOf(datasetItem), false)
        every { evaluationClient.requestEvaluationScore("mock-gpt", "query", "response") } throws EvaluationApiException(responseBody)
        every { evaluationResultWriter.deleteEvaluationResult(10L) } just runs
        every { evaluationJobWriter.evaluationJobFail(10L, responseBody) } just runs

        evaluationJobProcessor.run(10L)

        verify(exactly = 2) { evaluationResultWriter.deleteEvaluationResult(10L) }
        verify(exactly = 1) { evaluationJobWriter.evaluationJobFail(10L, responseBody) }
        verify(exactly = 0) { evaluationResultWriter.createEvaluationResults(any()) }
        verify(exactly = 0) { evaluationJobWriter.evaluationJobFinish(any(), any()) }
    }
}
