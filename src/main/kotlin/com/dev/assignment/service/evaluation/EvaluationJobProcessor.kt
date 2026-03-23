package com.dev.assignment.service.evaluation

import com.dev.assignment.domain.evaluation.EvaluationResult
import com.dev.assignment.client.evaluation.EvaluationApiException
import com.dev.assignment.client.evaluation.EvaluationClient
import com.dev.assignment.service.dataset.DatasetService
import com.dev.assignment.support.error.CoreException
import com.dev.assignment.support.error.ErrorType
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component

@Component
class EvaluationJobProcessor(
    private val evaluationJobReader: EvaluationJobReader,
    private val evaluationJobWriter: EvaluationJobWriter,
    private val evaluationResultWriter: EvaluationResultWriter,
    private val datasetService: DatasetService,
    private val evaluationClient: EvaluationClient,
    @Value($$"${evaluation.job.batch-size}") private val batchSize: Int
) {
    private val log: Logger = LoggerFactory.getLogger(javaClass)

    @Async
    fun run(evaluationJobId: Long) {
        try {
            process(evaluationJobId)
        } catch (e: Exception) {
            evaluationResultWriter.deleteEvaluationResult(evaluationJobId)
            evaluationJobWriter.evaluationJobFail(evaluationJobId, e.message)
            log.error("평가 작업 처리 실패: evaluationJobId={}, message={}", evaluationJobId, e.message, e)
        } catch (e: EvaluationApiException) {
            evaluationResultWriter.deleteEvaluationResult(evaluationJobId)
            evaluationJobWriter.evaluationJobFail(evaluationJobId, e.responseBody)
            log.error("평가 작업 처리 실패: evaluationJobId={}, message={}", evaluationJobId, e.message, e)
        }
    }

    private fun process(evaluationJobId: Long) {
        log.info("평가 작업 시작: evaluationJobId=${evaluationJobId}")

        evaluationResultWriter.deleteEvaluationResult(evaluationJobId)

        val evaluationJob = evaluationJobReader.findEvaluationJob(evaluationJobId)

        var sequenceNoCursor = 0L
        var totalScore = 0.0
        var evaluatedCount = 0L

        while (true) {
            val datasetItems = datasetService.findDatasetItems(
                datasetId = evaluationJob.datasetId,
                sequenceNoCursor = sequenceNoCursor,
                limit = batchSize
            )

            if (datasetItems.content.isEmpty()) break

            val evaluationResults = mutableListOf<EvaluationResult>()
            for (datasetItem in datasetItems.content) {
                val score = evaluationClient.requestEvaluationScore(
                    modelName = evaluationJob.modelName,
                    query = datasetItem.query,
                    response = datasetItem.response
                )

                evaluationResults.add(
                    EvaluationResult(
                        evaluationJobId = evaluationJobId,
                        datasetItemId = datasetItem.id,
                        score = score
                    )
                )

                totalScore += score
                evaluatedCount++
                sequenceNoCursor = datasetItem.sequenceNo
            }

            evaluationResultWriter.createEvaluationResults(evaluationResults)

            if (!datasetItems.hasNext) break
        }

        if (evaluatedCount == 0L) throw CoreException(ErrorType.NOT_FINISHED_DATASET)

        evaluationJobWriter.evaluationJobFinish(evaluationJobId, totalScore / evaluatedCount)

        log.info("평가 작업 완료: evaluationJobId=${evaluationJobId}, evaluatedCount=${evaluatedCount}")
    }
}
