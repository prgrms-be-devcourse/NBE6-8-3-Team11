package com.back.domain.notification.repository;

import com.back.domain.member.entity.Member;
import com.back.domain.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

    List<Notification> findByMemberOrderByCreatedAtDesc(Member member);

    Optional<Notification> findByIdAndMember(Long id, Member member);
}
