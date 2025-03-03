package com.ecotrade.server.listing.dto

import com.ecotrade.server.listing.model.Listing

data class ListingResponse (
    val id: Long,
    val title: String,
    val description: String,
    val price: Double,
    val category: String,
    val sustainabilityScore: Int,
    val images: List<String>,
    val userId: Long
) {
    companion object {
        fun fromEntity(listing: Listing): ListingResponse {
            return ListingResponse(
                id = listing.id,
                title = listing.title,
                description = listing.description,
                price = listing.price,
                category = listing.category,
                sustainabilityScore = listing.sustainabilityScore,
                images = listing.images,
                userId = listing.user.id
            )
        }
    }
}