package com.dev.assignment.api.controller.v1.evaluation

import com.dev.assignment.ControllerTestSupport
import com.dev.assignment.api.controller.v1.evaluation.request.CreateEvaluationJobRequest
import com.dev.assignment.api.controller.v1.evaluation.response.CreateEvaluationJobResponse
import com.dev.assignment.api.controller.v1.evaluation.response.DatasetItemResponse
import com.dev.assignment.api.controller.v1.evaluation.response.EvaluationResultResponse
import com.dev.assignment.support.error.CoreException
import com.dev.assignment.support.error.ErrorType
import io.mockk.every
import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageRequest
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class EvaluationJobControllerTest : ControllerTestSupport() {

    @Test
    fun createEvaluationJob() {
        val request = CreateEvaluationJobRequest(
            datasetId = 1L,
            modelName = "gpt"
        )

        every {
            evaluationJobUseCase.createEvaluationJob(1L, "gpt")
        } returns CreateEvaluationJobResponse(10L)

        mockMvc.perform(
            post("/v1/evaluation-jobs")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.result").value("SUCCESS"))
            .andExpect(jsonPath("$.data.evaluationJobId").value(10L))
    }

    @Test
    fun createEvaluationJobWithInvalidRequest() {
        val request = CreateEvaluationJobRequest(
            datasetId = 0L,
            modelName = "gpt"
        )

        mockMvc.perform(
            post("/v1/evaluation-jobs")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.result").value("ERROR"))
            .andExpect(jsonPath("$.error.code").value("INVALID_REQUEST"))
            .andExpect(jsonPath("$.error.message").value("필수 값을 입력해주세요."))
    }

    @Test
    fun createEvaluationJobWithInvalidModel() {
        val request = CreateEvaluationJobRequest(
            datasetId = 1L,
            modelName = "unknown-model"
        )

        every {
            evaluationJobUseCase.createEvaluationJob(1L, "unknown-model")
        } throws CoreException(ErrorType.INVALID_MODEL)

        mockMvc.perform(
            post("/v1/evaluation-jobs")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.result").value("ERROR"))
            .andExpect(jsonPath("$.error.code").value("INVALID_MODEL"))
            .andExpect(jsonPath("$.error.message").value(ErrorType.INVALID_MODEL.message))
    }

    @Test
    fun findEvaluationJobs() {
        val response = EvaluationResultResponse(
            averageScore = 0.75,
            content = listOf(
                DatasetItemResponse(
                    query = "q1",
                    response = "r1",
                    score = 1.0
                ),
                DatasetItemResponse(
                    query = "q2",
                    response = "r2",
                    score = 0.5
                )
            ),
            hasNext = true
        )

        every {
            evaluationJobUseCase.findEvaluationResults(1L, PageRequest.of(0, 1))
        } returns response

        mockMvc.perform(
            get("/v1/evaluation-jobs/1")
                .param("page", "1")
                .param("size", "1")
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.result").value("SUCCESS"))
            .andExpect(jsonPath("$.data.averageScore").value(0.75))
            .andExpect(jsonPath("$.data.content.length()").value(2))
            .andExpect(jsonPath("$.data.content[0].query").value("q1"))
            .andExpect(jsonPath("$.data.content[0].response").value("r1"))
            .andExpect(jsonPath("$.data.content[0].score").value(1.0))
            .andExpect(jsonPath("$.data.hasNext").value(true))
    }

    @Test
    fun findEvaluationJobsWithInvalidPathVariable() {
        mockMvc.perform(get("/v1/evaluation-jobs/invalid-id"))
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.result").value("ERROR"))
            .andExpect(jsonPath("$.error.code").value("INVALID_REQUEST"))
            .andExpect(jsonPath("$.error.message").value(ErrorType.INVALID_REQUEST.message))
    }
}
