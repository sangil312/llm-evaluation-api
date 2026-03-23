package com.dev.assignment.repository.dataset

import com.dev.assignment.service.dataset.parser.DatasetParseResult
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
class DatasetItemBatchRepository(
    private val jdbcTemplate: JdbcTemplate
) {
    companion object {
        private val INSERT_SQL = """
            insert into dataset_item (dataset_id, query, response, sequence_no, created_at, updated_at)
            values (?, ?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
        """.trimIndent()

        private val DELETE_SQL = """
            delete from dataset_item where dataset_id = ?
        """.trimIndent()
    }

    @Transactional
    fun insertBatch(items: List<DatasetParseResult>) {
        if (items.isEmpty()) return

        jdbcTemplate.batchUpdate(
            INSERT_SQL,
            items,
            items.size
        ) { ps, item ->
            ps.setLong(1, item.datasetId)
            ps.setString(2, item.query)
            ps.setString(3, item.response)
            ps.setLong(4, item.sequenceNo)
        }
    }

    @Transactional
    fun deleteByDatasetId(datasetId: Long) {
        jdbcTemplate.update(
            DELETE_SQL,
            datasetId
        )
    }
}