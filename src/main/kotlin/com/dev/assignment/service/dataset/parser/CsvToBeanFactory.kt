package com.dev.assignment.service.dataset.parser

import com.opencsv.bean.CsvToBean
import com.opencsv.bean.CsvToBeanBuilder
import com.opencsv.bean.exceptionhandler.CsvExceptionHandler
import com.opencsv.enums.CSVReaderNullFieldIndicator
import org.springframework.stereotype.Component
import java.io.Reader

@Component
class CsvToBeanFactory {

    fun <T> create(clazz: Class<T>, reader: Reader, handler: CsvExceptionHandler): CsvToBean<T> {
        return CsvToBeanBuilder<T>(reader)
            .withType(clazz)
            .withSeparator(',')
            .withQuoteChar('"')
            .withFieldAsNull(CSVReaderNullFieldIndicator.BOTH)
            .withIgnoreLeadingWhiteSpace(true)
            .withExceptionHandler(handler)
            .build()
    }
}