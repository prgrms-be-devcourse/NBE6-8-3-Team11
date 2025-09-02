package com.back.domain.pet.dto.request

import com.back.domain.pet.enums.Gender

data class PetUpdateRequestDto(

    var name: String? = null,
    var species: String? = null,
    var age: Int? = null,
    var gender: Gender? = null,
    var description: String? = null,

    var imageUrl: String? = null,
    var shelterName: String? = null,
    var statuses: List<String>? = null
)
