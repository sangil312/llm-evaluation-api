package com.dev.assignment.service.dataset

import com.dev.assignment.domain.dataset.Dataset
import com.dev.assignment.domain.dataset.DatasetItem
import com.dev.assignment.service.model.ModelManager
import com.dev.assignment.support.response.Page
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import io.mockk.verifySequence
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageRequest
import org.springframework.mock.web.MockMultipartFile

class DatasetServiceTest {
    private val datasetWriter: DatasetWriter = mockk()
    private val datasetUploadProcessor: DatasetUploadProcessor = mockk()
    private val datasetReader: DatasetReader = mockk()
    private val modelManager: ModelManager = mockk()

    private val datasetService = DatasetService(
        datasetWriter = datasetWriter,
        datasetUploadProcessor = datasetUploadProcessor,
        datasetReader = datasetReader,
        modelManager = modelManager
    )

    @Test
    fun createDataset() {
        val dataset = Dataset.create(modelId = 1L)
        val datasetFile = MockMultipartFile(
            "file",
            "dataset.csv",
            "text/csv",
            "query,response\nq1,r1".toByteArray()
        )

        every { modelManager.validateModel(1L) } just Runs
        every { datasetWriter.createDataset(1L) } returns dataset
        every { datasetUploadProcessor.createTempFileWithDirectory(dataset.id, datasetFile) } returns "temp/dataset.csv"
        every { datasetUploadProcessor.processUpload(dataset.id, "temp/dataset.csv") } just Runs

        val datasetId = datasetService.createDataset(modelId = 1L, datasetFile = datasetFile)

        assertThat(datasetId).isEqualTo(dataset.id)

        verifySequence {
            datasetWriter.createDataset(1L)
            datasetUploadProcessor.createTempFileWithDirectory(dataset.id, datasetFile)
            datasetUploadProcessor.processUpload(dataset.id, "temp/dataset.csv")
        }
    }

    @Test
    fun createDatasetWithCreateTempFileFail() {
        val dataset = Dataset.create(modelId = 1L)
        val datasetFile = MockMultipartFile(
            "file",
            "dataset.csv",
            "text/csv",
            "query,response\nq1,r1".toByteArray()
        )

        every { modelManager.validateModel(1L) } just Runs
        every { datasetWriter.createDataset(1L) } returns dataset
        every { datasetUploadProcessor.createTempFileWithDirectory(dataset.id, datasetFile) } throws RuntimeException()

        assertThatThrownBy { datasetService.createDataset(modelId = 1L, datasetFile = datasetFile) }
            .isInstanceOf(RuntimeException::class.java)

        verify(exactly = 1) { modelManager.validateModel(1L)}
        verify(exactly = 1) { datasetWriter.createDataset(1L) }
        verify(exactly = 1) { datasetUploadProcessor.createTempFileWithDirectory(dataset.id, datasetFile) }
        verify(exactly = 0) { datasetUploadProcessor.processUpload(any(), any()) }
    }

    @Test
    fun findFinishedDataset() {
        val dataset = Dataset.create(modelId = 1L).apply { finishUpload(totalCount = 2L) }

        every { datasetReader.findFinishedDataset(1L) } returns dataset

        val result = datasetService.findFinishedDataset(datasetId = 1L)

        assertThat(result).isSameAs(dataset)
        verify(exactly = 1) { datasetReader.findFinishedDataset(1L) }
    }

    @Test
    fun findDatasetItemsWithCursor() {
        val expectedPage = Page(
            content = listOf(
                DatasetItem(
                    datasetId = 1L,
                    query = "query-1",
                    response = "response-1",
                    sequenceNo = 11L
                ),
                DatasetItem(
                    datasetId = 1L,
                    query = "query-2",
                    response = "response-2",
                    sequenceNo = 12L
                )
            ),
            hasNext = false
        )

        every { datasetReader.findDatasetItems(1L, 10L, 2) } returns expectedPage

        val result = datasetService.findDatasetItems(
            datasetId = 1L,
            sequenceNoCursor = 10L,
            limit = 2
        )

        assertThat(result.content).hasSize(2)
        assertThat(result.content.map { it.sequenceNo }).containsExactly(11L, 12L)
        assertThat(result.hasNext).isFalse()

        verify(exactly = 1) { datasetReader.findDatasetItems(1L, 10L, 2) }
    }

    @Test
    fun findDatasetItemsWithPageable() {
        val pageable = PageRequest.of(0, 5)
        val expectedPage = Page(
            content = emptyList<DatasetItem>(),
            hasNext = false
        )

        every { datasetReader.findDatasetItems(1L, pageable) } returns expectedPage

        val result = datasetService.findDatasetItems(datasetId = 1L, pageable = pageable)

        assertThat(result.content).isEmpty()
        assertThat(result.hasNext).isFalse()

        verify(exactly = 1) { datasetReader.findDatasetItems(1L, pageable) }
    }
}
