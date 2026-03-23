package com.dev.assignment.service.evaluation

import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class EvaluationJobScheduler(
    private val evaluationJobService: EvaluationJobService,
    @Value($$"${evaluation.job.limit}") private val limit: Int
) {
    @Scheduled(fixedDelayString = $$"${evaluation.job.schedule-delay-ms}")
    fun schedulePendingJobs() {
        evaluationJobService.runPendingEvaluationJobs(limit)
    }
}
