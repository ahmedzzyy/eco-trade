package com.ecotrade.server.listing.repository

import com.ecotrade.server.listing.model.Listing
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository

interface ListingRepository: JpaRepository<Listing, Long> {
    fun getAll(pageable: Pageable): Page<Listing>
}