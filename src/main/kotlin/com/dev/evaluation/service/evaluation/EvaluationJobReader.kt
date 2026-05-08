package com.dev.evaluation.service.evaluation

import com.dev.evaluation.domain.evaluation.EvaluationJob
import com.dev.evaluation.domain.evaluation.EvaluationJobStatus
import com.dev.evaluation.repository.evaluation.EvaluationJobRepository
import com.dev.evaluation.support.error.CoreException
import com.dev.evaluation.support.error.ErrorType
import org.springframework.stereotype.Component

@Component
class EvaluationJobReader(
    private val evaluationJobRepository: EvaluationJobRepository
) {
    fun findEvaluationJob(evaluationJobId: Long): EvaluationJob {
        return evaluationJobRepository.findById(evaluationJobId)
            .orElseThrow { CoreException(ErrorType.NOT_FOUND_DATA) }
    }
    fun findFinishedEvaluationJob(evaluationJobId: Long): EvaluationJob {
        val evaluationJob = evaluationJobRepository.findById(evaluationJobId)
            .orElseThrow { CoreException(ErrorType.NOT_FOUND_DATA) }

        if (evaluationJob.status == EvaluationJobStatus.FAILED) throw CoreException(ErrorType.FAILED_EVALUATION)
        if (evaluationJob.status != EvaluationJobStatus.FINISHED) throw CoreException(ErrorType.NOT_FINISHED_EVALUATION)

        return evaluationJob
    }

    fun existsEvaluationJob(datasetId: Long, modelName: String): Boolean {
        return evaluationJobRepository.existsByDatasetIdAndModelName(datasetId, modelName)
    }
}
