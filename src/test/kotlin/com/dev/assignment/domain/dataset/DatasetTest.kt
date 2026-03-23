package com.dev.assignment.domain.dataset

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class DatasetTest {

    @Test
    fun create() {
        val modelId = 1L

        val dataset = Dataset.create(modelId)

        assertThat(dataset.modelId).isEqualTo(modelId)
        assertThat(dataset.status).isEqualTo(DatasetStatus.UPLOADING)
        assertThat(dataset.totalCount).isEqualTo(0L)
    }

    @Test
    fun finishUpload() {
        val dataset = Dataset.create(1L)

        dataset.finishUpload(100L)

        assertThat(dataset.status).isEqualTo(DatasetStatus.FINISHED)
        assertThat(dataset.totalCount).isEqualTo(100L)
    }
}
