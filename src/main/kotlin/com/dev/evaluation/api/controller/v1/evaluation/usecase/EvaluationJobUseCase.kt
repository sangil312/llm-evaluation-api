package com.dev.evaluation.api.controller.v1.evaluation.usecase

import com.dev.evaluation.api.controller.v1.evaluation.response.CreateEvaluationJobResponse
import com.dev.evaluation.api.controller.v1.evaluation.response.DatasetItemResponse
import com.dev.evaluation.api.controller.v1.evaluation.response.EvaluationResultResponse
import com.dev.evaluation.service.dataset.DatasetService
import com.dev.evaluation.service.evaluation.EvaluationJobService
import com.dev.evaluation.service.evaluation.EvaluationResultService
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component

@Component
class EvaluationJobUseCase(
    private val datasetService: DatasetService,
    private val evaluationService: EvaluationJobService,
    private val evaluationResultService: EvaluationResultService
) {
    fun createEvaluationJob(datasetId: Long, modelName: String): CreateEvaluationJobResponse {
        val finishedDataset = datasetService.findFinishedDataset(datasetId)

        val evaluationJob = evaluationService.createEvaluationJob(finishedDataset.id, modelName)

        return CreateEvaluationJobResponse(evaluationJob.id)
    }

    fun findEvaluationResults(evaluationJobId: Long, pageable: Pageable): EvaluationResultResponse {
        val finishedEvaluationJob = evaluationService.findFinishedEvaluationJob(evaluationJobId)

        val datasetItems = datasetService.findDatasetItems(finishedEvaluationJob.datasetId, pageable)

        val datasetItemIds = datasetItems.content.map { it.id }

        val evaluationResults = evaluationResultService.findEvaluationResults(
            finishedEvaluationJob.id,
            datasetItemIds
        )

        return EvaluationResultResponse(
            finishedEvaluationJob.averageScore,
            DatasetItemResponse.of(
                datasetItems.content,
                evaluationResults
            ),
            datasetItems.hasNext
        )
    }
}