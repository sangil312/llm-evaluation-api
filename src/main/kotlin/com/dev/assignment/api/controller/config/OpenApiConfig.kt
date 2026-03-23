package com.dev.assignment.api.controller.config

import com.dev.assignment.support.error.ErrorType
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.examples.Example
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.media.Content
import io.swagger.v3.oas.models.media.MediaType
import io.swagger.v3.oas.models.responses.ApiResponse
import io.swagger.v3.oas.models.responses.ApiResponses
import org.springdoc.core.customizers.OperationCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.AnnotatedElementUtils
import org.springframework.http.HttpStatus
import org.springframework.web.method.HandlerMethod

@Configuration
class OpenApiConfig {

    @Bean
    fun analysisOpenAPI(): OpenAPI =
        OpenAPI().info(
            Info()
                .title("데이터셋 API")
                .version("v1")
        )

    @Bean
    fun endpointErrorResponseCustomizer(): OperationCustomizer =
        OperationCustomizer { operation, handlerMethod ->
            val errorTypes = extractErrorType(handlerMethod)

            if (errorTypes.isEmpty()) {
                return@OperationCustomizer operation
            }

            val responses: ApiResponses = operation.responses ?: ApiResponses().also {
                operation.responses = it
            }

            val errorTypeListMap = groupByStatus(errorTypes)

            errorTypeListMap.forEach { (status, types) ->
                val apiResponse = ApiResponse()
                    .description(status.reasonPhrase)
                    .content(errorContent(types))

                responses.putIfAbsent(status.value().toString(), apiResponse)
            }

            operation
        }

    private fun extractErrorType(handlerMethod: HandlerMethod): List<ErrorType> {
        val classDoc = AnnotatedElementUtils.findMergedAnnotation(
            handlerMethod.beanType,
            ErrorDocumented::class.java
        )
        val methodDoc = AnnotatedElementUtils.findMergedAnnotation(
            handlerMethod.method,
            ErrorDocumented::class.java
        )

        return listOfNotNull(classDoc, methodDoc)
            .flatMap { it.value.toList() }
            .distinct()
    }

    private fun groupByStatus(errorTypes: List<ErrorType>): Map<HttpStatus, List<ErrorType>> =
        errorTypes
            .sortedBy { it.status.value() }
            .groupByTo(linkedMapOf()) { it.status }

    private fun errorContent(errorTypes: List<ErrorType>): Content {
        val mediaType = MediaType()
        val examples = linkedMapOf<String, Example>()

        errorTypes.forEach { errorType ->
            examples[errorType.name] = Example()
                .summary(errorType.name)
                .value(errorExample(errorType))
        }

        mediaType.examples = examples
        return Content().addMediaType("application/json", mediaType)
    }

    private fun errorExample(errorType: ErrorType): Map<String, Any?> {
        val error = linkedMapOf<String, Any>(
            "statusCode" to errorType.status.value(),
            "message" to errorType.message
        )

        return linkedMapOf(
            "resultType" to "ERROR",
            "data" to null,
            "error" to error
        )
    }
}