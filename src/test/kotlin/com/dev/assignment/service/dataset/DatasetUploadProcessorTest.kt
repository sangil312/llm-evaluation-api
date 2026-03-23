package com.dev.assignment.service.dataset

import com.dev.assignment.domain.dataset.Dataset
import com.dev.assignment.domain.dataset.DatasetStatus
import com.dev.assignment.repository.dataset.DatasetItemBatchRepository
import com.dev.assignment.repository.dataset.DatasetRepository
import com.dev.assignment.service.dataset.parser.CsvToBeanFactory
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import org.springframework.mock.web.MockMultipartFile
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.Optional

class DatasetUploadProcessorTest {
    private val csvToBeanFactory = CsvToBeanFactory()
    private val datasetItemBatchRepository: DatasetItemBatchRepository = mockk()
    private val datasetRepository: DatasetRepository = mockk()

    private val datasetUploadProcessor = DatasetUploadProcessor(
        csvToBeanFactory = csvToBeanFactory,
        datasetItemBatchRepository = datasetItemBatchRepository,
        datasetRepository = datasetRepository,
        batchSize = 1000
    )

    @TempDir
    lateinit var tempDir: Path

    @Test
    fun createTempFileWithDirectory() {
        val originalTmpDir = System.getProperty("java.io.tmpdir")
        val datasetId = 1L
        val content = "query,response\nq1,r1"
        val datasetFile = MockMultipartFile(
            "file",
            "dataset.csv",
            "text/csv",
            content.toByteArray(StandardCharsets.UTF_8)
        )

        try {
            System.setProperty("java.io.tmpdir", tempDir.toString())
            val expectedDirectory = Paths.get(tempDir.toString(), "assignment-temp")
            assertThat(Files.exists(expectedDirectory)).isFalse

            val filePath = datasetUploadProcessor.createTempFileWithDirectory(datasetId, datasetFile)
            val savedFile = Paths.get(filePath)

            assertThat(Files.exists(expectedDirectory)).isTrue
            assertThat(Files.exists(savedFile)).isTrue
            assertThat(savedFile.parent).isEqualTo(expectedDirectory)
            assertThat(savedFile.fileName.toString()).startsWith("$datasetId-").endsWith(".csv")
            assertThat(Files.readString(savedFile, StandardCharsets.UTF_8)).isEqualTo(content)
        } finally {
            System.setProperty("java.io.tmpdir", originalTmpDir)
        }
    }

    @Test
    fun processUploadSuccess() {
        val datasetId = 100L
        val dataset = Dataset(modelId = 1L, status = DatasetStatus.UPLOADING, totalCount = 0L)
        val csvPath = tempDir.resolve("success.csv")

        Files.writeString(
            csvPath,
            "query,response\nq1,r1\nq2,r2",
            StandardCharsets.UTF_8
        )

        every { datasetItemBatchRepository.insertBatch(any()) } just Runs
        every { datasetItemBatchRepository.deleteByDatasetId(any()) } just Runs
        every { datasetRepository.findById(datasetId) } returns Optional.of(dataset)
        every { datasetRepository.save(any()) } returns dataset
        every { datasetRepository.deleteById(any()) } just Runs

        datasetUploadProcessor.processUpload(datasetId, csvPath.toString())

        assertThat(dataset.status).isEqualTo(DatasetStatus.FINISHED)
        assertThat(dataset.totalCount).isEqualTo(2L)
        assertThat(Files.exists(csvPath)).isFalse

        verify(exactly = 1) { datasetItemBatchRepository.insertBatch(any()) }
        verify(exactly = 1) { datasetRepository.findById(datasetId) }
        verify(exactly = 1) { datasetRepository.save(dataset) }
        verify(exactly = 0) { datasetItemBatchRepository.deleteByDatasetId(any()) }
        verify(exactly = 0) { datasetRepository.deleteById(any()) }
    }
}
