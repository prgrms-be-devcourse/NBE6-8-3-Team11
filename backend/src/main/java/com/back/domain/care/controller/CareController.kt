package com.back.domain.care.controller

import CareRequestDto
import com.back.domain.care.dto.response.CareResponseDto
import com.back.domain.care.service.CareService
import com.back.global.common.ApiResponse
import io.swagger.v3.oas.annotations.Operation
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
class CareController(
    private val careService: CareService
) {

    @PostMapping("/applies/care")
    @Operation(summary = "돌봄 신청", description = "돌봄 신청을 처리합니다.")
    fun applyCare(
        @RequestBody careRequestDto: @Valid CareRequestDto,
        @AuthenticationPrincipal userDetails: UserDetails
    ): ResponseEntity<ApiResponse<CareResponseDto>> {
        val careResponseDto = careService.applyCare(careRequestDto, userDetails.username)
        return ResponseEntity.ok(ApiResponse.success(careResponseDto))
    }
}
