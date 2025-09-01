package com.back.domain.member.controller

import com.back.domain.member.dto.response.MemberResponseDto
import com.back.domain.member.service.AdminService
import com.back.global.common.ApiResponse
import io.swagger.v3.oas.annotations.Operation
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasAuthority('ROLE_ADMIN')") // *** 클래스 레벨에 권한 설정을 하여, 이 컨트롤러의 모든 API는 ADMIN 권한이 필요함을 명시합니다.
class AdminController(
    private val adminService: AdminService
) {

    @Operation(summary = "전체 회원 목록 조회")
    @GetMapping("/members")
    fun getAllMembers(): ResponseEntity<ApiResponse<List<MemberResponseDto>>> {
        val members = adminService.getAllMembers()
        return ResponseEntity.ok(ApiResponse.success(members))
    }

    @Operation(summary = "특정 회원 정보 조회")
    @GetMapping("/members/{memberId}")
    fun getMember(@PathVariable memberId: Long): ResponseEntity<ApiResponse<MemberResponseDto>> {
        val member = adminService.getMember(memberId)
        return ResponseEntity.ok(ApiResponse.success(member))
    }

    @Operation(summary = "특정 회원 삭제 및 강제 탈퇴")
    @DeleteMapping("/members/{memberId}")
    fun deleteMember(@PathVariable memberId: Long): ResponseEntity<ApiResponse<Void?>> {
        adminService.deleteMember(memberId)
        return ResponseEntity.ok(ApiResponse.success("해당 회원을 삭제했습니다.", null))
    }
}