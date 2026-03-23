package com.dev.assignment.service.evaluation

import com.dev.assignment.domain.evaluation.EvaluationJobStatus
import com.dev.assignment.repository.evaluation.EvaluationJobRepository
import com.dev.assignment.domain.evaluation.EvaluationJob
import com.dev.assignment.support.error.CoreException
import com.dev.assignment.support.error.ErrorType
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class EvaluationJobWriter(
    private val evaluationJobRepository: EvaluationJobRepository
) {
    fun createEvaluationJob(datasetId: Long, modelName: String): EvaluationJob {
        return evaluationJobRepository.save(
            EvaluationJob.register(
                datasetId = datasetId,
                modelName = modelName
            )
        )
    }

    @Transactional
    fun updatePendingJobs(limit: Int): List<Long> {
        val pendingJobIds = evaluationJobRepository.findIdsByPendingJobs(
            pendingStatus = EvaluationJobStatus.PENDING.name,
            limit = limit
        )

        if (pendingJobIds.isEmpty()) return emptyList()

        evaluationJobRepository.updateEvaluationJobStatusByIds(
            evaluationJobIds = pendingJobIds,
            conditionStatus = EvaluationJobStatus.PENDING.name,
            updateStatus = EvaluationJobStatus.RUNNING.name
        )

        return pendingJobIds
    }

    @Transactional
    fun evaluationJobFinish(evaluationJobId: Long, averageScore: Double) {
        val evaluationJob = evaluationJobRepository.findById(evaluationJobId)
            .orElseThrow { CoreException(ErrorType.NOT_FOUND_DATA) }

        evaluationJob.finish(averageScore)
    }

    @Transactional
    fun evaluationJobFail(evaluationJobId: Long, errorMessage: String?) {
        val evaluationJob = evaluationJobRepository.findById(evaluationJobId)
            .orElseThrow { CoreException(ErrorType.NOT_FOUND_DATA) }

        evaluationJob.fail(errorMessage)
    }
}
