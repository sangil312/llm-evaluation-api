package com.dev.assignment.service.dataset

import com.dev.assignment.domain.dataset.Dataset
import com.dev.assignment.repository.dataset.DatasetRepository
import org.springframework.stereotype.Component

@Component
class DatasetWriter(
    private val datasetRepository: DatasetRepository
) {
    fun createDataset(modelId: Long): Dataset {
        return datasetRepository.save(Dataset.create(modelId))
    }
}