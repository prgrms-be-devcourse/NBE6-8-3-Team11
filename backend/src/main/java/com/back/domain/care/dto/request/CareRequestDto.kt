import com.back.domain.applicant.dto.request.ApplicantRequestDto
import com.fasterxml.jackson.annotation.JsonFormat
import jakarta.validation.constraints.NotNull
import java.time.LocalDateTime

data class CareRequestDto(
    @field:NotNull
    val petId: Long = 0L,

    @field:NotNull
    val title: String = "",

    val applicantInfo: ApplicantRequestDto = ApplicantRequestDto(),

    val anotherPets: String? = null,

    val experience: String? = null,

    @field:NotNull
    val message: String = "",

    @field:NotNull
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    val desiredStartDate: LocalDateTime = LocalDateTime.now(),

    /**
     * 요청자가 원하는 돌봄 종료 날짜 - 무기한이 가능하므로 비어있을 수 있음
     */
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    val desiredEndDate: LocalDateTime? = null
)
