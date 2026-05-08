package com.dev.evaluation.service.dataset.parser

data class DatasetParseResult(
    val datasetId: Long,
    val query: String,
    val response: String,
    val sequenceNo: Long,
)