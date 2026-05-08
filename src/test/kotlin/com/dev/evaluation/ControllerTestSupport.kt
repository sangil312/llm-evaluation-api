package com.dev.evaluation

import com.dev.evaluation.api.controller.ApiControllerAdvice
import com.dev.evaluation.api.controller.v1.dataset.DatasetController
import com.dev.evaluation.api.controller.v1.evaluation.EvaluationJobController
import com.dev.evaluation.api.controller.v1.evaluation.usecase.EvaluationJobUseCase
import com.dev.evaluation.service.dataset.DatasetService
import com.ninjasquad.springmockk.MockkBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.test.web.servlet.MockMvc
import tools.jackson.databind.ObjectMapper

@Import(ApiControllerAdvice::class)
@WebMvcTest(controllers = [
    DatasetController::class,
    EvaluationJobController::class
])
abstract class ControllerTestSupport {

    @Autowired
    protected lateinit var mockMvc: MockMvc

    @Autowired
    protected lateinit var objectMapper: ObjectMapper

    @MockkBean
    protected lateinit var datasetService: DatasetService

    @MockkBean
    protected lateinit var evaluationJobUseCase: EvaluationJobUseCase
}