package com.back.domain.member.service

import com.back.domain.member.dto.request.MemberUpdateRequestDto
import com.back.domain.member.dto.response.MemberResponseDto
import com.back.domain.member.entity.toDto // *** Member.kt에 만든 확장 함수를 import 합니다.
import com.back.domain.member.exception.MemberErrorCode
import com.back.domain.member.exception.MemberException
import com.back.domain.member.repository.MemberRepository
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional


@Service
@Transactional(readOnly = true)
class MemberService(
    private val memberRepository: MemberRepository,
    private val passwordEncoder: PasswordEncoder
) {
    fun getMemberInfo(memberId: Long): MemberResponseDto {
        val member = memberRepository.findById(memberId)
            .orElseThrow { MemberException(MemberErrorCode.MEMBER_NOT_FOUND) }
        return member.toDto()
    }

    @Transactional
    fun updateMemberInfo(
        memberId: Long,
        requestDto: MemberUpdateRequestDto,
        userDetails: UserDetails
    ): MemberResponseDto {
        val memberToUpdate = memberRepository.findById(memberId)
            .orElseThrow { MemberException(MemberErrorCode.MEMBER_NOT_FOUND) }

        if (memberToUpdate.email != userDetails.username) {
            throw MemberException(MemberErrorCode.FORBIDDEN_ACCESS)
        }

        memberToUpdate.updateInfo(
            name = requestDto.name ?: memberToUpdate.name,
            phone = requestDto.phone ?: memberToUpdate.phone,
            address = requestDto.address ?: memberToUpdate.address,
            bio = requestDto.bio ?: memberToUpdate.bio
        )
//        if (requestDto.newPassword() != null && !requestDto.newPassword().isBlank()) {
//            if (!passwordEncoder.matches(requestDto.currentPassword(), memberToUpdate.getPassword())) {
//                throw new MemberException(MemberErrorCode.AUTH_LOGIN_FAILED);
//            }
//
//            memberToUpdate.updatePassword(passwordEncoder.encode(requestDto.newPassword()));
//        }

        return memberToUpdate.toDto()
    }
}