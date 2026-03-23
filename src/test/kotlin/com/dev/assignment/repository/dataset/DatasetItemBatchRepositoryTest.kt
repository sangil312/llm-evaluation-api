package com.dev.assignment.repository.dataset

import com.dev.assignment.IntegrationTestSupport
import com.dev.assignment.domain.dataset.DatasetItem
import com.dev.assignment.service.dataset.parser.DatasetParseResult
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.transaction.annotation.Transactional

@Transactional
class DatasetItemBatchRepositoryTest(
    private val datasetItemBatchRepository: DatasetItemBatchRepository,
    private val datasetItemRepository: DatasetItemRepository
) : IntegrationTestSupport() {

    @Test
    fun insertBatch() {
        val datasetId = 100L

        datasetItemBatchRepository.insertBatch(
            listOf(
                DatasetParseResult(
                    datasetId = datasetId,
                    query = "q1",
                    response = "r1",
                    sequenceNo = 1L
                ),
                DatasetParseResult(
                    datasetId = datasetId,
                    query = "q2",
                    response = "r2",
                    sequenceNo = 2L
                ),
                DatasetParseResult(
                    datasetId = datasetId,
                    query = "q3",
                    response = "r3",
                    sequenceNo = 3L
                )
            )
        )

        val insertedItems = datasetItemRepository.findAll()

        assertThat(insertedItems).hasSize(3)
        assertThat(insertedItems.map { it.datasetId }).containsOnly(datasetId)
        assertThat(insertedItems.map { it.sequenceNo }).containsExactlyInAnyOrder(1L, 2L, 3L)
        assertThat(insertedItems.map { it.query }).containsExactlyInAnyOrder("q1", "q2", "q3")
        assertThat(insertedItems.map { it.response }).containsExactlyInAnyOrder("r1", "r2", "r3")
    }

    @Test
    fun insertBatchWithEmptyItems() {
        datasetItemBatchRepository.insertBatch(emptyList())

        val insertedItems = datasetItemRepository.findAll()

        assertThat(insertedItems).isEmpty()
    }

    @Test
    fun deleteByDatasetId() {
        datasetItemRepository.saveAll(
            listOf(
                DatasetItem(
                    datasetId = 1L,
                    query = "q1",
                    response = "r1",
                    sequenceNo = 1L
                ),
                DatasetItem(
                    datasetId = 1L,
                    query = "q2",
                    response = "r2",
                    sequenceNo = 2L
                ),
                DatasetItem(
                    datasetId = 2L,
                    query = "q3",
                    response = "r3",
                    sequenceNo = 1L
                )
            )
        )

        datasetItemBatchRepository.deleteByDatasetId(1L)

        val remainItems = datasetItemRepository.findAll()

        assertThat(remainItems).hasSize(1)
        assertThat(remainItems[0].datasetId).isEqualTo(2L)
        assertThat(remainItems[0].query).isEqualTo("q3")
        assertThat(remainItems[0].response).isEqualTo("r3")
        assertThat(remainItems[0].sequenceNo).isEqualTo(1L)
    }

    @Test
    fun deleteByDatasetIdWithNoMatch() {
        datasetItemRepository.saveAll(
            listOf(
                DatasetItem(
                    datasetId = 2L,
                    query = "q1",
                    response = "r1",
                    sequenceNo = 1L
                ),
                DatasetItem(
                    datasetId = 2L,
                    query = "q2",
                    response = "r2",
                    sequenceNo = 2L
                )
            )
        )

        datasetItemBatchRepository.deleteByDatasetId(999L)

        val remainItems = datasetItemRepository.findAll()

        assertThat(remainItems).hasSize(2)
        assertThat(remainItems.map { it.datasetId }).containsOnly(2L)
        assertThat(remainItems.map { it.sequenceNo }).containsExactlyInAnyOrder(1L, 2L)
    }
}
