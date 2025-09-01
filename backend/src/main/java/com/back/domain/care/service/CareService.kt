package com.back.domain.care.service

import com.back.domain.adoption.enums.RequestStatus
import com.back.domain.applicant.dto.request.ApplicantRequestDto
import com.back.domain.care.dto.request.CareRequestDto
import com.back.domain.care.dto.response.CareResponseDto
import com.back.domain.care.entity.Care
import com.back.domain.care.repository.CareRepository
import com.back.domain.member.exception.MemberErrorCode
import com.back.domain.member.exception.MemberException
import com.back.domain.member.repository.MemberRepository
import com.back.domain.notification.service.NotificationService
import com.back.domain.pet.entity.PetStatus
import com.back.domain.pet.enums.PetStatusType
import com.back.domain.pet.exception.PetErrorCode
import com.back.domain.pet.exception.PetException
import com.back.domain.pet.repository.PetRepository
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class CareService(
    private val memberRepository: MemberRepository,
    private val petRepository: PetRepository,
    private val careRepository: CareRepository,
    private val notificationService: NotificationService,
    private val messagingTemplate: SimpMessagingTemplate
) {

    fun applyCare(careRequestDto: CareRequestDto, memberEmail: String): CareResponseDto {
        val member = memberRepository.findByEmail(memberEmail)
            .orElseThrow { MemberException(MemberErrorCode.MEMBER_NOT_FOUND) }

        val pet = petRepository.findById(careRequestDto.petId)
            .orElseThrow { PetException(PetErrorCode.PET_NOT_FOUND) }

        val isAvailableForCare = pet.petStatuses.any { status ->
            status.status == PetStatusType.AVAILABLE_FOR_CARE ||
                    status.status == PetStatusType.AVAILABLE_BOTH
        }

        if (!isAvailableForCare) {
            throw PetException(PetErrorCode.PET_NOT_AVAILABLE_FOR_CARE)
        }

        val applicant = ApplicantRequestDto.of(careRequestDto.applicantInfo)
        
        val care = Care.create(
            title = careRequestDto.title,
            message = careRequestDto.message,
            desiredStartDate = careRequestDto.desiredStartDate,
            anotherPets = careRequestDto.anotherPets,
            experience = careRequestDto.experience,
            desiredEndDate = careRequestDto.desiredEndDate,
            member = member,
            pet = pet,
            applicant = applicant,
            status = RequestStatus.PENDING
        )
        
        careRepository.save(care)

        notificationService.sendCareRequestNotification(member.id, "동물 돌봄 신청이 접수되었습니다", member.name)

        pet.member?.id?.let { memberId ->
            notificationService.sendCareRequestNotification(
                memberId,
                "동물 돌봄 신청이 도착하였습니다",
                member.name
            )
        }

        return CareResponseDto.from(care)
    }
}
