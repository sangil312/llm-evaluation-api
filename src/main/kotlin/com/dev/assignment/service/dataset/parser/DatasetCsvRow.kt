package com.dev.assignment.service.dataset.parser

import com.opencsv.bean.CsvBindByName

data class DatasetCsvRow(
    @field:CsvBindByName(column = "query", required = true)
    var query: String? = null,

    @field:CsvBindByName(column = "response", required = true)
    var response: String? = null
)
