package com.dev.assignment.service.evaluation

import com.dev.assignment.domain.evaluation.EvaluationJob
import com.dev.assignment.support.error.CoreException
import com.dev.assignment.support.error.ErrorType
import org.springframework.stereotype.Service

@Service
class EvaluationJobService(
    private val evaluationJobWriter: EvaluationJobWriter,
    private val evaluationJobReader: EvaluationJobReader,
    private val evaluationJobProcessor: EvaluationJobProcessor
) {
    fun findFinishedEvaluationJob(evaluationJobId: Long): EvaluationJob {
        return evaluationJobReader.findFinishedEvaluationJob(evaluationJobId)
    }

    fun createEvaluationJob(datasetId: Long, modelName: String): EvaluationJob {
        if (evaluationJobReader.existsEvaluationJob(datasetId, modelName)) {
            throw CoreException(ErrorType.ALREADY_CREATED_EVALUATION)
        }

        return evaluationJobWriter.createEvaluationJob(datasetId, modelName)
    }

    fun runPendingEvaluationJobs(limit: Int) {
        val evaluationJobIds = evaluationJobWriter.updatePendingJobs(limit)
        for (evaluationJobId in evaluationJobIds) {
            evaluationJobProcessor.run(evaluationJobId)
        }
    }
}