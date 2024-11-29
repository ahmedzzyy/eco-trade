package com.ecotrade.server.listing.repository

import com.ecotrade.server.listing.model.Listing
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ListingRepository: JpaRepository<Listing, Long>
