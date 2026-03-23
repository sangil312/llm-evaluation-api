package com.dev.assignment.api.controller.v1.evaluation.response

import com.dev.assignment.domain.dataset.DatasetItem
import com.dev.assignment.domain.evaluation.EvaluationResult

data class EvaluationResultResponse(
    val averageScore: Double,
    val content: List<DatasetItemResponse>,
    val hasNext: Boolean,
)

data class DatasetItemResponse(
    val query: String,
    val response: String,
    val score: Double,
) {
    companion object {
        fun of(
            datasetItems: List<DatasetItem>,
            evaluationResults: List<EvaluationResult>
        ): List<DatasetItemResponse> {
            val evaluationResultMap = evaluationResults.associateBy { it.datasetItemId }

            return datasetItems.map {
                DatasetItemResponse(
                    query = it.query,
                    response = it.response,
                    score = evaluationResultMap[it.id]!!.score
                )
            }
        }
    }
}
