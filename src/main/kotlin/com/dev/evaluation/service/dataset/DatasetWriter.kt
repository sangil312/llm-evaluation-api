package com.dev.evaluation.service.dataset

import com.dev.evaluation.domain.dataset.Dataset
import com.dev.evaluation.repository.dataset.DatasetRepository
import org.springframework.stereotype.Component

@Component
class DatasetWriter(
    private val datasetRepository: DatasetRepository
) {
    fun createDataset(modelId: Long): Dataset {
        return datasetRepository.save(Dataset.create(modelId))
    }
}