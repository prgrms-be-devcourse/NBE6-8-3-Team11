package com.back.global.exception

import org.springframework.http.HttpStatus

// 해당 커스텀 예외처럼 패키지별로 작업하시면서 MemberErrorException 이런 식으로 작성하시면 됩니다.
// 생성자의 인자는 처리하고자 하는 커스텀 예외 코드를 사용하시면 됩니다.
class CustomException(errorCode: ErrorCode) : RuntimeException(errorCode.getMessage()) {
    val httpStatus: HttpStatus = errorCode.getHttpStatus()
    val code: String = errorCode.getCode()
    val message: String = errorCode.getMessage()
}