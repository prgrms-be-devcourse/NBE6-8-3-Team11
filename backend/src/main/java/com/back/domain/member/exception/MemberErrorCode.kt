package com.back.domain.member.exception

import org.springframework.http.HttpStatus


enum class MemberErrorCode(
    val httpStatus: HttpStatus,
    val code: String,
    val message: String
) {
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "MEMBER-404", "회원을 찾을 수 없습니다."),
    EMAIL_ALREADY_EXISTS(HttpStatus.CONFLICT, "MEMBER-001", "이미 가입된 이메일입니다."),
    AUTH_LOGIN_FAILED(HttpStatus.UNAUTHORIZED, "AUTH-001", "아이디 또는 비밀번호가 일치하지 않습니다."),
    FORBIDDEN_ACCESS(HttpStatus.FORBIDDEN, "AUTH-002", "권한이 없습니다."), // FORBIDDEN(403)이 더 정확한 상태 코드

    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH-003", "유효하지 않은 토큰입니다."),
    TOKEN_NOT_FOUND(HttpStatus.NOT_FOUND, "AUTH-004", "토큰을 찾을 수 없습니다.");
}