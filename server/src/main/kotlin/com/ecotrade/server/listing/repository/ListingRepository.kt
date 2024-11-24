package com.ecotrade.server.listing.repository

import com.ecotrade.server.listing.model.Listing
import org.springframework.data.jpa.repository.JpaRepository

interface ListingRepository: JpaRepository<Listing, Long> {
    fun findByCategory(category: String): List<Listing>
}