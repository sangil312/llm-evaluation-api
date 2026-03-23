package com.dev.assignment.api.controller.v1.dataset

import com.dev.assignment.ControllerTestSupport
import com.dev.assignment.support.error.CoreException
import com.dev.assignment.support.error.ErrorType
import io.mockk.every
import org.junit.jupiter.api.Test
import org.springframework.mock.web.MockMultipartFile
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class DatasetControllerTest : ControllerTestSupport() {

    @Test
    fun createDataset() {
        val file = MockMultipartFile(
            "file",
            "dataset.csv",
            "text/csv",
            "query,response\nq1,r1".toByteArray()
        )

        every { datasetService.createDataset(any(), any()) } returns 1L

        mockMvc.perform(
            multipart("/v1/datasets/upload")
                .file(file)
                .param("modelId", "1")
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.result").value("SUCCESS"))
            .andExpect(jsonPath("$.data.datasetId").value(1L))
    }

    @Test
    fun createDatasetWithInvalidDataset() {
        val file = MockMultipartFile(
            "file",
            "dataset.csv",
            "text/csv",
            "invalid".toByteArray()
        )

        every { datasetService.createDataset(any(), any()) } throws CoreException(ErrorType.INVALID_DATASET)

        mockMvc.perform(
            multipart("/v1/datasets/upload")
                .file(file)
                .param("modelId", "1")
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.result").value("ERROR"))
            .andExpect(jsonPath("$.error.code").value("INVALID_DATASET"))
            .andExpect(jsonPath("$.error.message").value(ErrorType.INVALID_DATASET.message))
    }
}
