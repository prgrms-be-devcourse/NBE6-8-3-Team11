package com.back.domain.applicant.repository

import com.back.domain.applicant.entity.Applicant
import org.springframework.data.jpa.repository.JpaRepository

interface ApplicantRepository : JpaRepository<Applicant, Long>