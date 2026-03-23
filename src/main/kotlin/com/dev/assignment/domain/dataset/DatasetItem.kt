package com.dev.assignment.domain.dataset

import com.dev.assignment.domain.BaseEntity
import jakarta.persistence.Entity
import jakarta.persistence.Index
import jakarta.persistence.Table

@Entity
@Table(
    name = "dataset_item",
    indexes = [
        Index(name = "udx_dataset_id_sequence_no", columnList = "dataset_id, sequence_no", unique = true)
    ]
)
class DatasetItem(
    val datasetId: Long,
    val query: String,
    val response: String,
    val sequenceNo: Long,
) : BaseEntity()
