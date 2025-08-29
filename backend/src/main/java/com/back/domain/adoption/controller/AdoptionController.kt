package com.back.domain.adoption.controller

import com.back.domain.adoption.dto.request.AdoptionCareStatusUpdateRequestDto
import com.back.domain.adoption.dto.request.AdoptionRequestDto
import com.back.domain.adoption.dto.response.AdoptionResponseDto
import com.back.domain.adoption.dto.response.ApplicationResponseDto
import com.back.domain.adoption.dto.response.ApplicationSimpleListResponseDto
import com.back.domain.adoption.service.AdoptionService
import com.back.global.common.ApiResponse
import io.swagger.v3.oas.annotations.Operation
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/applies")
class AdoptionController(
    private val adoptionService: AdoptionService
) {

    @PostMapping("/adoption")
    @Operation(summary = "입양 신청", description = "입양 신청을 처리합니다.")
    fun applyAdoption(
        @RequestBody adoptionRequestDto: @Valid AdoptionRequestDto,
        @AuthenticationPrincipal userDetails: UserDetails
    ): ResponseEntity<ApiResponse<AdoptionResponseDto>> {
        val adoptionResponseDto = adoptionService.applyAdoption(adoptionRequestDto, userDetails.username)
        // 알람은 추후 구현
        return ResponseEntity.ok(ApiResponse.success(adoptionResponseDto))
    }

    @GetMapping
    @Operation(summary = "회원 입양/돌봄 신청 목록 조회", description = "회원의 입양 및 돌봄 신청 목록을 조회합니다.")
    fun getAdoptionAndCareList(
        @AuthenticationPrincipal userDetails: UserDetails
    ): ResponseEntity<ApiResponse<List<ApplicationSimpleListResponseDto>>> {
        val simpleApplications = adoptionService.getMemberApplications(userDetails.username)
        return ResponseEntity.ok(ApiResponse.success(simpleApplications))
    }

    @GetMapping("/detail")
    @Operation(summary = "회원 입양/돌봄 신청 내역 상세 조회", description = "회원의 입양 및 돌봄 신청 내역을 상세 조회합니다.")
    fun getAdoptionAndCareDetail(
        @RequestParam typeId: Long, @RequestParam type: String,
        @AuthenticationPrincipal userDetails: UserDetails
    ): ResponseEntity<ApiResponse<ApplicationResponseDto>> {
        val applicationDetails = adoptionService.getApplicationDetails(typeId, type, userDetails.username)
        return ResponseEntity.ok(ApiResponse.success(applicationDetails))
    }

    @DeleteMapping
    @Operation(summary = "회원 입양/돌봄 신청 내역 단건 취소(삭제)", description = "회원의 입양 및 돌봄 신청 내역 하나를 취소합니다.")
    fun deleteAdoptionAndCare(
        @RequestParam typeId: Long, @RequestParam type: String,
        @AuthenticationPrincipal userDetails: UserDetails
    ): ResponseEntity<ApiResponse<Void>> {
        adoptionService.deleteSingleHistory(typeId, type, userDetails.username)
        return ResponseEntity.ok(ApiResponse.success(null))
    }

    @DeleteMapping("/all")
    @Operation(summary = "회원 입양/돌봄 신청 내역 전체 취소(삭제)", description = "회원의 입양 및 돌봄 신청 내역 전체를 취소합니다.")
    fun deleteAdoptionAndCareAll(
        @AuthenticationPrincipal userDetails: UserDetails
    ): ResponseEntity<ApiResponse<Void>> {
        adoptionService.deleteAllHistory(userDetails.username)
        return ResponseEntity.ok(ApiResponse.success(null))
    }

    @GetMapping("/received")
    @Operation(summary = "보호자가 받은 입양/돌봄 신청 내역 리스트 조회", description = "보호자가 받은 입양 및 돌봄 신청 내역 리스트를 조회합니다.")
    fun getReceivedApplications(
        @AuthenticationPrincipal userDetails: UserDetails
    ): ResponseEntity<ApiResponse<List<ApplicationSimpleListResponseDto>>> {
        val receivedApplications = adoptionService.getReceivedApplications(userDetails.username)
        return ResponseEntity.ok(ApiResponse.success(receivedApplications))
    }

    @GetMapping("/received/detail")
    @Operation(summary = "보호자가 받은 입양/돌봄 신청 내역 상세 조회", description = "보호자가 받은 입양 및 돌봄 신청 내역을 상세 조회합니다.")
    fun getReceivedApplicationDetail(
        @RequestParam typeId: Long, @RequestParam type: String,
        @AuthenticationPrincipal userDetails: UserDetails
    ): ResponseEntity<ApiResponse<ApplicationResponseDto>> {
        val applicationDetails = adoptionService.getReceivedApplicationDetails(typeId, type, userDetails.username)
        return ResponseEntity.ok(ApiResponse.success(applicationDetails))
    }

    @PutMapping("/received")
    @Operation(
        summary = "보호자가 받은 입양/돌봄 신청 내역 상태 변경 - 수락/거절(ACCEPTED/REJECTED)",
        description = "보호자가 받은 입양 및 돌봄 신청 내역의 상태를 변경(수락/거절)합니다."
    )
    fun updateReceivedApplicationStatus(
        @RequestBody requestDto: @Valid AdoptionCareStatusUpdateRequestDto,
        @AuthenticationPrincipal userDetails: UserDetails
    ): ResponseEntity<ApiResponse<Void>> {
        adoptionService.updateReceivedApplicationStatus(requestDto, userDetails.username)
        return ResponseEntity.ok(ApiResponse.success(null))
    }

    @DeleteMapping("/received")
    @Operation(summary = "보호자가 받은 입양/돌봄 등록 내역 단건 취소(삭제)", description = "보호자가 받은 입양 및 돌봄 등록 내역 하나를 취소합니다.")
    fun deleteReceivedApplication(
        @RequestParam typeId: Long, @RequestParam type: String,
        @AuthenticationPrincipal userDetails: UserDetails
    ): ResponseEntity<ApiResponse<Void>> {
        adoptionService.deleteReceivedSingleHistory(typeId, type, userDetails.username)
        return ResponseEntity.ok(ApiResponse.success(null))
    }

    @DeleteMapping("/received/all")
    @Operation(summary = "보호자가 받은 입양/돌봄 등록 내역 전체 취소(삭제)", description = "보호자가 받은 입양 및 돌봄 등록 내역 전체를 취소합니다.")
    fun deleteReceivedApplicationsAll(
        @AuthenticationPrincipal userDetails: UserDetails
    ): ResponseEntity<ApiResponse<Void>> {
        adoptionService.deleteOwnerAllHistory(userDetails.username)
        return ResponseEntity.ok(ApiResponse.success(null))
    }
}
