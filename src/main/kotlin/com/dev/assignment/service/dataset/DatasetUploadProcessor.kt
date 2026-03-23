package com.dev.assignment.service.dataset

import com.dev.assignment.repository.dataset.DatasetItemBatchRepository
import com.dev.assignment.repository.dataset.DatasetRepository
import com.dev.assignment.service.dataset.parser.CsvToBeanFactory
import com.dev.assignment.service.dataset.parser.DatasetCsvExceptionHandler
import com.dev.assignment.service.dataset.parser.DatasetCsvRow
import com.dev.assignment.service.dataset.parser.DatasetParseResult
import com.dev.assignment.support.error.CoreException
import com.dev.assignment.support.error.ErrorType
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Paths
import java.util.UUID

@Component
class DatasetUploadProcessor(
    private val csvToBeanFactory: CsvToBeanFactory,
    private val datasetItemBatchRepository: DatasetItemBatchRepository,
    private val datasetRepository: DatasetRepository,
    @Value($$"${dataset.batch-size}") private val batchSize: Int
) {
    private val log: Logger = LoggerFactory.getLogger(javaClass)

    /**
     * S3 스토리지 대신 로컬 임시파일 생성으로 대체
     */
    fun createTempFileWithDirectory(datasetId: Long, datasetFile: MultipartFile): String {
        val tempDirectory = Paths.get(System.getProperty("java.io.tmpdir"), "assignment-temp")
        Files.createDirectories(tempDirectory)

        val tempPath = tempDirectory.resolve("${datasetId}-${UUID.randomUUID()}.csv")
        datasetFile.transferTo(tempPath.toFile())

        return tempPath.toString()
    }

    @Async
    fun processUpload(datasetId: Long, filePath: String) {
        log.info("dataset file 업로드 시작: datasetId= ${datasetId}")

        val path = Paths.get(filePath)
        var sequenceNo = 1L
        val datasetParseResults = mutableListOf<DatasetParseResult>()

        try {
            Files.newBufferedReader(path, StandardCharsets.UTF_8).use { reader ->
                val rows = csvToBeanFactory.create(
                    DatasetCsvRow::class.java,
                    reader,
                    DatasetCsvExceptionHandler()
                )

                for (row in rows) {
                    if (row.query.isNullOrBlank() || row.response.isNullOrBlank()) throw CoreException(ErrorType.INVALID_DATASET)

                    datasetParseResults.add(
                        DatasetParseResult(
                            datasetId = datasetId,
                            query = row.query!!,
                            response = row.response!!,
                            sequenceNo = sequenceNo++
                        )
                    )

                    if (datasetParseResults.size == batchSize) {
                        datasetItemBatchRepository.insertBatch(datasetParseResults)
                        datasetParseResults.clear()
                    }
                }
            }

            if (datasetParseResults.isNotEmpty()) {
                datasetItemBatchRepository.insertBatch(datasetParseResults)
                datasetParseResults.clear()
            }

            finishUpload(datasetId, sequenceNo - 1)
        } catch (e: Exception) {
            rollbackUploadedDataset(datasetId)
            log.error("dataset file 업로드 실패 datasetId: {}, Exception: {}", datasetId, e.message, e)
            throw CoreException(ErrorType.DEFAULT_ERROR)
        } finally {
            Files.deleteIfExists(path)
        }
    }

    private fun finishUpload(datasetId: Long, totalCount: Long) {
        if (totalCount == 0L) throw CoreException(ErrorType.INVALID_REQUEST)

        val dataset = datasetRepository.findById(datasetId).orElseThrow()

        dataset.finishUpload(totalCount)

        datasetRepository.save(dataset)
    }

    private fun rollbackUploadedDataset(datasetId: Long) {
        datasetItemBatchRepository.deleteByDatasetId(datasetId)

        datasetRepository.deleteById(datasetId)
    }
}