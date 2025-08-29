package com.back.domain.pet.controller

import com.back.domain.pet.dto.request.PetCreateRequestDto
import com.back.domain.pet.dto.request.PetUpdateRequestDto
import com.back.domain.pet.dto.response.PetInfoResponseDto
import com.back.domain.pet.service.PetService
import com.back.global.common.ApiResponse
import io.swagger.v3.oas.annotations.Operation
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/pets")
class PetController(
    private val petService: PetService
) {

    //반려동물 생성
    @PostMapping
    @Operation(summary = "동물 생성")
    fun createPet(
        @RequestBody @Valid dto: PetCreateRequestDto,
        @AuthenticationPrincipal userDetails: UserDetails
    ): ResponseEntity<ApiResponse<PetInfoResponseDto>> {
        val userEmail = userDetails.username
        val createdPet = petService.createPet(dto, userEmail)
        return ResponseEntity.status(HttpStatus.OK)
            .body(ApiResponse.success(createdPet))
    }

    //반려동물 삭제
    @DeleteMapping("/{petId}")
    @Operation(summary = "동물 삭제")
    fun deletePet(
        @PathVariable petId: Long,
        @AuthenticationPrincipal userDetails: UserDetails
    ): ResponseEntity<ApiResponse<Void>> {
        val userEmail = userDetails.username
        petService.deletePet(petId, userEmail)
        return ResponseEntity.status(HttpStatus.OK)
            .body(ApiResponse.success("펫 삭제 성공", null))
    }

    // 반려동물 수정
    @PutMapping("/{petId}")
    @Operation(summary = "동물 수정")
    fun updatePet(
        @PathVariable petId: Long,
        @RequestBody @Valid requestDto: PetUpdateRequestDto,
        @AuthenticationPrincipal userDetails: UserDetails
    ): ResponseEntity<ApiResponse<PetInfoResponseDto>> {
        val userEmail = userDetails.username
        val updated = petService.updatePet(petId, userEmail, requestDto)
        return ResponseEntity.status(HttpStatus.OK)
            .body(ApiResponse.success(updated))
    }

    // 단일 반려동물 조회
    @GetMapping("/{petId}")
    @Operation(summary = "동물 단건 조회")
    fun getPet(@PathVariable petId: Long): ResponseEntity<ApiResponse<PetInfoResponseDto>> {
        val pet = petService.getPetById(petId)
        return ResponseEntity.status(HttpStatus.OK)
            .body(ApiResponse.success(pet))
    }

    // 전체 반려동물 조회
    @GetMapping
    @Operation(summary = "동물 전체 조회")
    fun getAllPets(): ResponseEntity<ApiResponse<List<PetInfoResponseDto>>> {
        val pets = petService.getAllPets()
        return ResponseEntity.status(HttpStatus.OK)
            .body(ApiResponse.success(pets))
    }
}