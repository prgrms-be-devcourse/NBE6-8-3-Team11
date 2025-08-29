package com.back.domain.member.controller

import com.back.domain.member.dto.request.MemberUpdateRequestDto
import com.back.domain.member.dto.response.MemberResponseDto
import com.back.domain.member.service.MemberService
import com.back.global.common.ApiResponse
import io.swagger.v3.oas.annotations.Operation
import jakarta.validation.Valid // *** @Valid를 사용하기 위해 import 합니다.
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/members")
class MemberController(
    private val memberService: MemberService
) {

    @GetMapping("/{memberId}")
    @Operation(summary = "회원 정보 조회")
    fun getMemberInfo(@PathVariable memberId: Long): ResponseEntity<ApiResponse<MemberResponseDto>> {
        return ResponseEntity.ok(ApiResponse.success(memberService.getMemberInfo(memberId)))
    }

    @PutMapping("/{memberId}")
    @Operation(summary = "회원 정보 수정")
    fun updateMemberInfo(
        @PathVariable memberId: Long,
        @Valid @RequestBody requestDto: MemberUpdateRequestDto, // *** DTO에 설정된 유효성 검사를 적용하기 위해 @Valid를 추가합니다.
        @AuthenticationPrincipal userDetails: UserDetails
    ): ResponseEntity<ApiResponse<MemberResponseDto>> {
        val updatedMember = memberService.updateMemberInfo(memberId, requestDto, userDetails)
        return ResponseEntity.ok(ApiResponse.success("회원 정보가 수정되었습니다.", updatedMember))
    }
}