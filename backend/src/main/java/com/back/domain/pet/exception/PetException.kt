package com.back.domain.pet.exception

import org.springframework.http.HttpStatus

class PetException(
    val httpStatus: HttpStatus,
    val code: String,
    val errorMessage: String
) : RuntimeException(errorMessage) {

    constructor(errorCode: PetErrorCode) : this(
        httpStatus = errorCode.httpStatus,
        code = errorCode.code,
        errorMessage = errorCode.message
    )
}