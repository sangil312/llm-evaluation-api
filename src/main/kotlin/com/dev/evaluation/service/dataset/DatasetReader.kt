package com.dev.evaluation.service.dataset

import com.dev.evaluation.domain.dataset.Dataset
import com.dev.evaluation.domain.dataset.DatasetItem
import com.dev.evaluation.domain.dataset.DatasetStatus
import com.dev.evaluation.repository.dataset.DatasetItemRepository
import com.dev.evaluation.repository.dataset.DatasetRepository
import com.dev.evaluation.support.error.CoreException
import com.dev.evaluation.support.error.ErrorType
import com.dev.evaluation.support.response.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component

@Component
class DatasetReader(
    private val datasetRepository: DatasetRepository,
    private val datasetItemRepository: DatasetItemRepository
) {
    fun findFinishedDataset(datasetId: Long): Dataset {
        val dataset = datasetRepository.findById(datasetId)
            .orElseThrow { throw CoreException(ErrorType.NOT_FOUND_DATA) }

        if (dataset.status != DatasetStatus.FINISHED) throw CoreException(ErrorType.NOT_FINISHED_DATASET)

        return dataset
    }

    fun findDatasetItems(datasetId: Long, sequenceNoCursor: Long, limit: Int): Page<DatasetItem> {
        val pageable = PageRequest.of(0, limit)
        val datasetItems = datasetItemRepository.findAllByDatasetIdAndSequenceNoGreaterThanOrderBySequenceNoAsc(
                datasetId = datasetId,
                sequenceNo = sequenceNoCursor,
                pageable = pageable
            )

        return Page(datasetItems.content, datasetItems.hasNext())
    }

    fun findDatasetItems(datasetId: Long, pageable: Pageable): Page<DatasetItem> {
        val datasetItems = datasetItemRepository.findAllByDatasetIdOrderBySequenceNoAsc(
            datasetId = datasetId,
            pageable = pageable
        )

        return Page(datasetItems.content, datasetItems.hasNext())
    }
}
