package com.back.domain.adoption.service

import com.back.domain.adoption.dto.request.AdoptionCareStatusUpdateRequestDto
import com.back.domain.adoption.dto.request.AdoptionRequestDto
import com.back.domain.adoption.dto.response.AdoptionResponseDto
import com.back.domain.adoption.dto.response.ApplicationResponseDto
import com.back.domain.adoption.dto.response.ApplicationSimpleListResponseDto
import com.back.domain.adoption.entity.Adoption
import com.back.domain.adoption.enums.RequestStatus
import com.back.domain.adoption.repository.AdoptionRepository
import com.back.domain.applicant.dto.request.ApplicantRequestDto
import com.back.domain.care.entity.Care
import com.back.domain.care.repository.CareRepository
import com.back.domain.member.entity.Member
import com.back.domain.member.exception.MemberErrorCode
import com.back.domain.member.exception.MemberException
import com.back.domain.member.repository.MemberRepository
import com.back.domain.notification.service.NotificationService
import com.back.domain.pet.entity.Pet
import com.back.domain.pet.entity.PetStatus
import com.back.domain.pet.enums.PetStatusType
import com.back.domain.pet.exception.PetErrorCode
import com.back.domain.pet.exception.PetException
import com.back.domain.pet.repository.PetRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class AdoptionService(
    private val memberRepository: MemberRepository,
    private val petRepository: PetRepository,
    private val adoptionRepository: AdoptionRepository,
    private val careRepository: CareRepository,
    private val notificationService: NotificationService
) {

    fun applyAdoption(adoptionRequestDto: AdoptionRequestDto, memberEmail: String): AdoptionResponseDto {
        val member = getMemberByEmail(memberEmail)

        val pet = petRepository.findById(adoptionRequestDto.petId)
            .orElseThrow{ PetException(PetErrorCode.PET_NOT_FOUND)}

        val isAvailableForAdoption = pet.petStatuses.any { status ->
            status.status == PetStatusType.AVAILABLE_FOR_ADOPTION ||
                    status.status == PetStatusType.AVAILABLE_BOTH
        }

        if (!isAvailableForAdoption) {
            throw PetException(PetErrorCode.PET_NOT_AVAILABLE_FOR_CARE)
        }

        val applicant = ApplicantRequestDto.of(adoptionRequestDto.applicantInfo)
        
        val adoption = Adoption.create(
            title = adoptionRequestDto.title,
            anotherPets = adoptionRequestDto.anotherPets,
            experience = adoptionRequestDto.experience,
            message = adoptionRequestDto.message,
            member = member,
            pet = pet,
            applicant = applicant,
            status = RequestStatus.PENDING
        )
        
        adoptionRepository.save(adoption)
        notificationService.sendAdoptionRequestNotification(member.id, "입양을 신청하였습니다.", member.name)

        notificationService.sendAdoptionRequestNotification(pet.member.id, "입양 신청이 도착하였습니다.", member.name)

        return AdoptionResponseDto.from(adoption)!!
    }

    @Transactional(readOnly = true)
    fun getMemberApplications(memberEmail: String): List<ApplicationSimpleListResponseDto> {
        val member = getMemberByEmail(memberEmail)

        val adoptions = adoptionRepository.findByMemberOrderByCreatedAtDesc(member)
        val cares = careRepository.findByMemberOrderByCreatedAtDesc(member)

        val applications = adoptions.map { adoption ->
            ApplicationSimpleListResponseDto.fromAdoption(adoption)
        } + cares.map { care ->
            ApplicationSimpleListResponseDto.fromCare(care)
        }
        
        // createdAt 기준으로 내림차순 정렬 (최신순) - null은 마지막에 배치
        return applications.sortedByDescending { it.createdAt }
    }

    @Transactional(readOnly = true)
    fun getApplicationDetails(typeId: Long, type: String, memberEmail: String): ApplicationResponseDto {
        val member = getMemberByEmail(memberEmail)
        val entity = getApplicationEntity(type, typeId, member)

        return when (entity) {
            is Adoption -> ApplicationResponseDto.fromAdoption(entity)!!
            is Care -> ApplicationResponseDto.fromCare(entity)!!
            else -> throw IllegalArgumentException("Invalid application type: $type")
        }
    }

    fun deleteSingleHistory(typeId: Long, type: String, memberEmail: String) {
        val member = getMemberByEmail(memberEmail)
        val entity = getApplicationEntity(type, typeId, member)

        when (entity) {
            is Adoption -> adoptionRepository.delete(entity)
            is Care -> careRepository.delete(entity)
        }
    }

    fun deleteAllHistory(memberEmail: String) {
        val member = getMemberByEmail(memberEmail)

        val adoptions = adoptionRepository.findByMember(member)
        val cares = careRepository.findByMember(member)

        adoptionRepository.deleteAll(adoptions)
        careRepository.deleteAll(cares)
    }

    @Transactional(readOnly = true)
    fun getReceivedApplications(memberEmail: String): List<ApplicationSimpleListResponseDto> {
        val member = getMemberByEmail(memberEmail)

        val adoptions = adoptionRepository.findByPet_MemberOrderByCreatedAtDesc(member)
        val cares = careRepository.findByPet_MemberOrderByCreatedAtDesc(member)

        val applications = adoptions.map { adoption ->
            ApplicationSimpleListResponseDto.fromAdoption(adoption)
        } + cares.map { care ->
            ApplicationSimpleListResponseDto.fromCare(care)
        }
        
        // createdAt 기준으로 내림차순 정렬 (최신순) - null은 마지막에 배치
        return applications.sortedByDescending { it.createdAt }
    }

    @Transactional(readOnly = true)
    fun getReceivedApplicationDetails(
        typeId: Long, type: String, memberEmail: String
    ): ApplicationResponseDto {
        val member = getMemberByEmail(memberEmail)
        val entity = getReceivedApplicationEntity(type, typeId, member)

        return when (entity) {
            is Adoption -> ApplicationResponseDto.fromAdoption(entity)!!
            is Care -> ApplicationResponseDto.fromCare(entity)!!
            else -> throw IllegalArgumentException("Invalid application type: $type")
        }
    }

    fun updateReceivedApplicationStatus(requestDto: AdoptionCareStatusUpdateRequestDto, memberEmail: String) {
        val member = getMemberByEmail(memberEmail)
        val entity = getReceivedApplicationEntity(requestDto.type, requestDto.id, member)

        val pet = petRepository.findById(requestDto.id)
            .orElseThrow { PetException(PetErrorCode.PET_NOT_FOUND)}

        val newStatus = RequestStatus.valueOf(requestDto.status)

        if (newStatus == RequestStatus.REJECTED) {
            updateApplicationStatus(entity, newStatus)
            notificationService.sendResponseNotification(member.id, "신청을 거절되었습니다.", requestDto.type, false)
            notificationService.sendResponseNotification(pet.member.id, "신청이 거절되었습니다.", requestDto.type, false)
            return
        }

        // ACCEPTED인 경우 PetStatus도 함께 업데이트
        updateApplicationStatusWithPetStatus(entity, newStatus)
        notificationService.sendResponseNotification(member.id, "신청을 승인하셨습니다.", requestDto.type, true)
        notificationService.sendResponseNotification(pet.member.id, "신청이 승인되셨습니다.", requestDto.type, true)
    }

    fun deleteReceivedSingleHistory(typeId: Long, type: String, memberEmail: String) {
        val member = getMemberByEmail(memberEmail)
        val entity = getReceivedApplicationEntity(type, typeId, member)

        when (entity) {
            is Adoption -> adoptionRepository.delete(entity)
            is Care -> careRepository.delete(entity)
        }
    }

    fun deleteOwnerAllHistory(memberEmail: String) {
        val member = getMemberByEmail(memberEmail)

        val adoptions = adoptionRepository.findByPet_MemberOrderByCreatedAtDesc(member)
        val cares = careRepository.findByPet_MemberOrderByCreatedAtDesc(member)

        adoptionRepository.deleteAll(adoptions)
        careRepository.deleteAll(cares)
    }

    @Transactional(readOnly = true)
    private fun getMemberByEmail(memberEmail: String): Member {
        return memberRepository.findByEmail(memberEmail)
            .orElseThrow { MemberException(MemberErrorCode.MEMBER_NOT_FOUND)}
    }

    @Transactional(readOnly = true)
    private fun getApplicationEntity(type: String, id: Long, member: Member): Any {
        return when (type) {
            "ADOPTION" -> adoptionRepository.findByIdAndMember(id, member)
                ?: throw PetException(PetErrorCode.PET_NOT_FOUND)
            "CARE" -> careRepository.findByIdAndMember(id, member)
                ?: throw PetException(PetErrorCode.PET_NOT_FOUND)
            else -> throw IllegalArgumentException("Invalid application type: $type")
        }
    }

    @Transactional(readOnly = true)
    private fun getReceivedApplicationEntity(type: String, id: Long, member: Member): Any {
        return when (type) {
            "ADOPTION" -> adoptionRepository.findByIdAndPet_Member(id, member)
                ?: throw PetException(PetErrorCode.PET_NOT_FOUND)
            "CARE" -> careRepository.findByIdAndPet_Member(id, member)
                ?: throw PetException(PetErrorCode.PET_NOT_FOUND)
            else -> throw IllegalArgumentException("Invalid application type: $type")
        }
    }

    private fun updateApplicationStatus(entity: Any, status: RequestStatus) {
        when (entity) {
            is Adoption -> {
                entity.updateStatus(status)
                adoptionRepository.save(entity)
            }
            is Care -> {
                entity.updateStatus(status)
                careRepository.save(entity)
            }
            else -> throw IllegalArgumentException("Invalid application type")
        }
    }

    private fun updateApplicationStatusWithPetStatus(entity: Any, status: RequestStatus) {
        when (entity) {
            is Adoption -> {
                entity.updateStatus(status)
                updatePetStatus(entity.pet, PetStatusType.ADOPTED)
                adoptionRepository.save(entity)
            }
            is Care -> {
                entity.updateStatus(status)
                updatePetStatus(entity.pet, PetStatusType.CARE_COMPLETED)
                careRepository.save(entity)
            }
            else -> throw IllegalArgumentException("Invalid application type")
        }
    }

    private fun updatePetStatus(pet: Pet, newStatus: PetStatusType) {
        pet.petStatuses.clear()
        // 다른 도메인 마이그레이션으로 인한 에러 임시 주석 처리
        pet.petStatuses.add(
            PetStatus.create(
                status = newStatus,
                pet = pet
            )
        )
    }
}
