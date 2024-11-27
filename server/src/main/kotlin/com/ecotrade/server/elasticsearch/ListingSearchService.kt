package com.ecotrade.server.elasticsearch

import com.ecotrade.server.listing.model.Listing

class ListingSearchService(private val listingSearchRepository: ListingSearchRepository) {

    fun indexListing(listing: Listing) {
        val document = ListingDocument(
            listing.id,
            listing.title,
            listing.description,
            listing.price,
            listing.category,
            listing.sustainabilityScore
        )

        listingSearchRepository.save(document)
    }

    fun deleteListing(id: Long) {
        listingSearchRepository.deleteById(id)
    }
}