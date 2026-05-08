package com.dev.evaluation.service.dataset

import com.dev.evaluation.domain.dataset.Dataset
import com.dev.evaluation.domain.dataset.DatasetItem
import com.dev.evaluation.service.model.ModelManager
import com.dev.evaluation.support.response.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile

@Service
class DatasetService(
    private val datasetWriter: DatasetWriter,
    private val datasetUploadProcessor: DatasetUploadProcessor,
    private val datasetReader: DatasetReader,
    private val modelManager: ModelManager
) {
    fun createDataset(modelId: Long, datasetFile: MultipartFile): Long {
        modelManager.validateModel(modelId)

        val dataset = datasetWriter.createDataset(modelId)

        val tempFilePath = datasetUploadProcessor.createTempFileWithDirectory(dataset.id, datasetFile)

        datasetUploadProcessor.processUpload(dataset.id, tempFilePath)

        return dataset.id
    }

    fun findFinishedDataset(datasetId: Long): Dataset {
        return datasetReader.findFinishedDataset(datasetId)
    }

    fun findDatasetItems(datasetId: Long, sequenceNoCursor: Long, limit: Int): Page<DatasetItem> {
        return datasetReader.findDatasetItems(datasetId, sequenceNoCursor, limit)
    }

    fun findDatasetItems(datasetId: Long, pageable: Pageable): Page<DatasetItem> {
        return datasetReader.findDatasetItems(datasetId, pageable)
    }
}
