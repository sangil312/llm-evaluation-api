package com.dev.assignment.repository.dataset

import com.dev.assignment.domain.dataset.DatasetItem
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.jpa.repository.JpaRepository

interface DatasetItemRepository : JpaRepository<DatasetItem, Long> {
    fun findAllByDatasetIdAndSequenceNoGreaterThanOrderBySequenceNoAsc(
        datasetId: Long,
        sequenceNo: Long,
        pageable: Pageable
    ): Slice<DatasetItem>

    fun findAllByDatasetIdOrderBySequenceNoAsc(datasetId: Long, pageable: Pageable): Slice<DatasetItem>
}
