package com.ecotrade.server.listing.service

import com.ecotrade.server.listing.dto.ListingRequest
import com.ecotrade.server.listing.model.Listing
import com.ecotrade.server.listing.repository.ListingRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class ListingService(private val listingRepository: ListingRepository) {

    fun createListing(listing: Listing): Listing {
        return listingRepository.save(listing)
    }

    fun getListingById(id: Long): Listing {
        return listingRepository.findById(id)
            .orElseThrow { RuntimeException("Listing not found!") }
    }

    fun getAllListings(pageable: Pageable): Page<Listing> {
        return listingRepository.getAll(pageable)
    }

    fun updateListing(id: Long, listingRequest: ListingRequest, currentUserEmail: String): Listing {
        val listing = listingRepository.findById(id)
            .orElseThrow { RuntimeException("Listing not found!") }

        if (listing.user.email != currentUserEmail) {
            throw RuntimeException("Unauthorized to update the listing.")
        }

        val updatedListing = listing.copy(
            title = listingRequest.title,
            description = listingRequest.description,
            category = listingRequest.category,
            price = listingRequest.price,
            sustainabilityScore = listingRequest.sustainabilityScore,
            images = listingRequest.images
        )

        return listingRepository.save(updatedListing)
    }

    fun deleteListing(id: Long, currentUserEmail: String) {
        val listing = listingRepository.findById(id)
            .orElseThrow { RuntimeException("Listing not found!") }

        if (listing.user.email != currentUserEmail) {
            throw RuntimeException("Unauthorized to update the listing.")
        }

        listingRepository.delete(listing)
    }
}