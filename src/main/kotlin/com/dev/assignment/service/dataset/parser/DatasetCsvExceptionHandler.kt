package com.dev.assignment.service.dataset.parser

import com.opencsv.bean.exceptionhandler.CsvExceptionHandler
import com.opencsv.exceptions.CsvException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.util.StringUtils

class DatasetCsvExceptionHandler : CsvExceptionHandler {
    private val log: Logger = LoggerFactory.getLogger(javaClass)

    override fun handleException(e: CsvException): CsvException? {
        val reason = if (StringUtils.hasText(e.message)) e.message else "CSV 파싱 오류"

        log.debug("csv 파싱 실패: lineNo: {}, reason: {}", e.lineNumber, reason)

        throw e
    }
}