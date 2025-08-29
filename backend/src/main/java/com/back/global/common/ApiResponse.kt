package com.back.global.common

// API 응답을 표준화하기 위한 클래스
// 성공 여부, 상태 코드, 메시지, 콘텐츠를 포함
// 제네릭 타입 T를 사용하여 다양한 타입의 콘텐츠(반환 타입)를 처리할 수 있도록 함
// 사용 - ResponseEntity<ApiResponse<T>> 형태로 사용
data class ApiResponse<T>(
    val success: Boolean,
    val code: String,
    val message: String,
    val content: T?
) {
    companion object {
        private const val SUCCESS_CODE = "200"

        // 성공 응답 생성 메서드
        fun <T> success(content: T?): ApiResponse<T> {
            return ApiResponse(
                success = true,
                code = SUCCESS_CODE,
                message = "Success",
                content = content
            )
        }

        fun <T> success(message: String, content: T?): ApiResponse<T> {
            return ApiResponse(
                success = true,
                code = SUCCESS_CODE,
                message = message,
                content = content
            )
        }

        // 실패 응답 생성 메서드
        fun <T> fail(code: String, message: String): ApiResponse<T?> {
            return ApiResponse(
                success = false,
                code = code,
                message = message,
                content = null
            )
        }
    }
}
