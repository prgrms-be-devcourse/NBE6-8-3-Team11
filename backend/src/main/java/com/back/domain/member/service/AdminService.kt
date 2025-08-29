package com.back.domain.member.service

import com.back.domain.member.dto.response.MemberResponseDto
import com.back.domain.member.entity.toDto
import com.back.domain.member.exception.MemberErrorCode
import com.back.domain.member.exception.MemberException
import com.back.domain.member.repository.MemberRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class AdminService(
    private val memberRepository: MemberRepository
) {

    fun getAllMembers(): List<MemberResponseDto> {
        return memberRepository.findAll().map { it.toDto() }
    }

    fun getMember(memberId: Long): MemberResponseDto {
        val member = memberRepository.findById(memberId)
            .orElseThrow { MemberException(MemberErrorCode.MEMBER_NOT_FOUND) }

        return member.toDto()
    }

    @Transactional
    fun deleteMember(memberId: Long) {
        if (!memberRepository.existsById(memberId)) {
            throw MemberException(MemberErrorCode.MEMBER_NOT_FOUND)
        }
        memberRepository.deleteById(memberId)
    }
}