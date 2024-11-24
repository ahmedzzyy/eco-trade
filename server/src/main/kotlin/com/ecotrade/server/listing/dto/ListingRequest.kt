package com.ecotrade.server.listing.dto

import com.ecotrade.server.listing.model.Listing
import com.ecotrade.server.user.model.User
import jakarta.validation.constraints.*

data class ListingRequest(

    @field:NotBlank(message = "Title is mandatory")
    val title: String,

    @field:NotBlank(message = "Description is mandatory")
    @field:Size(max = 500, message = "Description cannot exceed 500 characters")
    val description: String,

    @field:Min(value = 0, message = "Price must be at least 0")
    val price: Double,

    @field:NotBlank(message = "Category is mandatory")
    val category: String,

    @field:Min(value = 0, message = "Sustainability score must be at least 0 or higher")
    val sustainabilityScore: Int = 0,

    val images: List<String> = listOf()
) {
    fun toEntity(user: User): Listing {
        return Listing(
            title = title,
            description = description,
            price = price,
            category = category,
            sustainabilityScore = sustainabilityScore,
            images = images,
            user = user
        )
    }
}