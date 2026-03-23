package com.dev.assignment.domain.dataset

import com.dev.assignment.domain.BaseEntity
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Table

@Entity
@Table(name = "dataset")
class Dataset(
    val modelId: Long,
    status: DatasetStatus,
    totalCount: Long,
) : BaseEntity() {
    @Enumerated(EnumType.STRING)
    var status: DatasetStatus = status
        protected set

    var totalCount: Long = totalCount
        protected set

    companion object {
        fun create(modelId: Long): Dataset {
            return Dataset(
                modelId = modelId,
                status = DatasetStatus.UPLOADING,
                totalCount = 0L
            )
        }
    }

    fun finishUpload(totalCount: Long) {
        this.totalCount = totalCount
        this.status = DatasetStatus.FINISHED
    }
}
