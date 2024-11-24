package com.ecotrade.server.listing.dto

import com.ecotrade.server.listing.model.Listing
import com.ecotrade.server.user.model.User

data class ListingRequest(
    val title: String,
    val description: String,
    val price: Double,
    val category: String,
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