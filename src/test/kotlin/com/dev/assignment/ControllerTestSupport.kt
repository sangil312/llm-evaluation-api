package com.dev.assignment

import com.dev.assignment.api.controller.ApiControllerAdvice
import com.dev.assignment.api.controller.v1.auth.AuthController
import com.dev.assignment.api.controller.v1.dataset.DatasetController
import com.dev.assignment.api.controller.v1.evaluation.EvaluationJobController
import com.dev.assignment.api.controller.v1.evaluation.usecase.EvaluationJobUseCase
import com.dev.assignment.service.auth.AuthService
import com.dev.assignment.service.dataset.DatasetService
import com.ninjasquad.springmockk.MockkBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.test.web.servlet.MockMvc
import tools.jackson.databind.ObjectMapper

@Import(ApiControllerAdvice::class)
@WebMvcTest(controllers = [
    AuthController::class,
    DatasetController::class,
    EvaluationJobController::class
])
abstract class ControllerTestSupport {

    @Autowired
    protected lateinit var mockMvc: MockMvc

    @Autowired
    protected lateinit var objectMapper: ObjectMapper

    @MockkBean
    protected lateinit var authService: AuthService

    @MockkBean
    protected lateinit var datasetService: DatasetService

    @MockkBean
    protected lateinit var evaluationJobUseCase: EvaluationJobUseCase
}