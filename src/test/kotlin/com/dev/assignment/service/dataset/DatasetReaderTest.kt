package com.dev.assignment.service.dataset

import com.dev.assignment.IntegrationTestSupport
import com.dev.assignment.domain.dataset.Dataset
import com.dev.assignment.domain.dataset.DatasetItem
import com.dev.assignment.repository.dataset.DatasetItemRepository
import com.dev.assignment.repository.dataset.DatasetRepository
import com.dev.assignment.support.error.CoreException
import com.dev.assignment.support.error.ErrorType
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageRequest
import org.springframework.transaction.annotation.Transactional

@Transactional
class DatasetReaderTest(
    private val datasetReader: DatasetReader,
    private val datasetRepository: DatasetRepository,
    private val datasetItemRepository: DatasetItemRepository
) : IntegrationTestSupport() {

    @Test
    fun findFinishedDataset() {
        val savedDataset = datasetRepository.save(
            Dataset.create(modelId = 1L).apply { finishUpload(totalCount = 3L) }
        )

        val findDataset = datasetReader.findFinishedDataset(savedDataset.id)

        assertThat(findDataset.id).isEqualTo(savedDataset.id)
        assertThat(findDataset.modelId).isEqualTo(1L)
        assertThat(findDataset.totalCount).isEqualTo(3L)
    }

    @Test
    fun findFinishedDatasetWithNotFound() {
        assertThatThrownBy { datasetReader.findFinishedDataset(9999L) }
            .isInstanceOf(CoreException::class.java)
            .hasMessage(ErrorType.NOT_FOUND_DATA.message)
    }

    @Test
    fun findFinishedDatasetWithUploadingStatus() {
        val savedDataset = datasetRepository.save(Dataset.create(modelId = 1L))

        assertThatThrownBy { datasetReader.findFinishedDataset(savedDataset.id) }
            .isInstanceOf(CoreException::class.java)
            .hasMessage(ErrorType.NOT_FINISHED_DATASET.message)
    }

    @Test
    fun findDatasetItemsWithCursor() {
        datasetItemRepository.saveAll(
            listOf(
                DatasetItem(datasetId = 1L, query = "q1", response = "r1", sequenceNo = 1L),
                DatasetItem(datasetId = 1L, query = "q2", response = "r2", sequenceNo = 2L),
                DatasetItem(datasetId = 1L, query = "q3", response = "r3", sequenceNo = 3L),
                DatasetItem(datasetId = 1L, query = "q4", response = "r4", sequenceNo = 4L),
                DatasetItem(datasetId = 2L, query = "other", response = "other", sequenceNo = 3L)
            )
        )

        val page = datasetReader.findDatasetItems(datasetId = 1L, sequenceNoCursor = 1L, limit = 2)

        assertThat(page.content).hasSize(2)
        assertThat(page.content.map { it.sequenceNo }).containsExactly(2L, 3L)
        assertThat(page.content.map { it.query }).containsExactly("q2", "q3")
        assertThat(page.hasNext).isTrue()
    }

    @Test
    fun findDatasetItemsWithCursorAtLastPage() {
        datasetItemRepository.saveAll(
            listOf(
                DatasetItem(datasetId = 1L, query = "q1", response = "r1", sequenceNo = 1L),
                DatasetItem(datasetId = 1L, query = "q2", response = "r2", sequenceNo = 2L),
                DatasetItem(datasetId = 1L, query = "q3", response = "r3", sequenceNo = 3L)
            )
        )

        val page = datasetReader.findDatasetItems(datasetId = 1L, sequenceNoCursor = 2L, limit = 2)

        assertThat(page.content).hasSize(1)
        assertThat(page.content[0].sequenceNo).isEqualTo(3L)
        assertThat(page.hasNext).isFalse()
    }

    @Test
    fun findDatasetItemsWithPageable() {
        datasetItemRepository.saveAll(
            listOf(
                DatasetItem(datasetId = 1L, query = "q1", response = "r1", sequenceNo = 1L),
                DatasetItem(datasetId = 1L, query = "q2", response = "r2", sequenceNo = 2L),
                DatasetItem(datasetId = 1L, query = "q3", response = "r3", sequenceNo = 3L),
                DatasetItem(datasetId = 2L, query = "q4", response = "r4", sequenceNo = 1L)
            )
        )

        val page = datasetReader.findDatasetItems(datasetId = 1L, pageable = PageRequest.of(0, 2))

        assertThat(page.content).hasSize(2)
        assertThat(page.content.map { it.sequenceNo }).containsExactly(1L, 2L)
        assertThat(page.content.map { it.query }).containsExactly("q1", "q2")
        assertThat(page.hasNext).isTrue()
    }

    @Test
    fun findDatasetItemsWithPageableAtLastPage() {
        datasetItemRepository.saveAll(
            listOf(
                DatasetItem(datasetId = 1L, query = "q1", response = "r1", sequenceNo = 1L),
                DatasetItem(datasetId = 1L, query = "q2", response = "r2", sequenceNo = 2L)
            )
        )

        val page = datasetReader.findDatasetItems(datasetId = 1L, pageable = PageRequest.of(0, 5))

        assertThat(page.content).hasSize(2)
        assertThat(page.content.map { it.sequenceNo }).containsExactly(1L, 2L)
        assertThat(page.hasNext).isFalse()
    }
}
