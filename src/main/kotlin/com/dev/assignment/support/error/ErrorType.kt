package com.dev.assignment.support.error

import org.springframework.boot.logging.LogLevel
import org.springframework.http.HttpStatus

enum class ErrorType(
    val status: HttpStatus,
    val message: String,
    val logLevel: LogLevel
) {
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "요청이 올바르지 않습니다.", LogLevel.INFO),
    DEFAULT_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "요청을 처리하는 중 오류가 발생했습니다. 잠시 후 다시 시도해주세요.", LogLevel.ERROR),
    NOT_FOUND_DATA(HttpStatus.NOT_FOUND, "요청하신 데이터를 찾을 수 없습니다.", LogLevel.ERROR),

    // 모델
    DUPLICATED_MODEL(HttpStatus.BAD_REQUEST, "이미 존재하는 모델 이름입니다.", LogLevel.INFO),
    INVALID_MODEL(HttpStatus.NOT_FOUND, "등록되지 않은 평가 모델입니다.", LogLevel.INFO),

    // 데이터셋
    INVALID_DATASET(HttpStatus.BAD_REQUEST, "올바르지 않은 데이터셋 입니다.", LogLevel.INFO),
    NOT_FINISHED_DATASET(HttpStatus.BAD_REQUEST, "업로드가 완료된 데이터셋만 평가할 수 있습니다.", LogLevel.INFO),

    // 평가
    ALREADY_CREATED_EVALUATION(HttpStatus.BAD_REQUEST, "이미 등록된 평가 작업입니다.", LogLevel.INFO),
    NOT_FINISHED_EVALUATION(HttpStatus.BAD_REQUEST, "완료되지 않은 평가 작업입니다.", LogLevel.INFO),
    FAILED_EVALUATION(HttpStatus.BAD_REQUEST, "완료되지 않은 평가 작업입니다.", LogLevel.WARN)
}
