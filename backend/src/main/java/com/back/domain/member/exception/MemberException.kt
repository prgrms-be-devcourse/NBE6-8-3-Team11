package com.back.domain.member.exception


class MemberException(
    val memberErrorCode: MemberErrorCode
) : RuntimeException(memberErrorCode.message) {
    val httpStatus = memberErrorCode.httpStatus
    val code = memberErrorCode.code
    override val message: String = memberErrorCode.message
}