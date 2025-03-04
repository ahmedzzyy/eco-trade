package com.ecotrade.server.listing.controller

import com.ecotrade.server.elasticsearch.ListingDocument
import com.ecotrade.server.exception.UserNotFoundException
import com.ecotrade.server.listing.dto.ListingRequest
import com.ecotrade.server.listing.dto.ListingResponse
import com.ecotrade.server.listing.service.ListingService
import com.ecotrade.server.user.service.UserService
import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/listings")
class ListingController(
    private val listingService: ListingService,
    private val userService: UserService
) {

    @PostMapping
    fun createListing(@RequestBody @Valid listingRequest: ListingRequest): ResponseEntity<Void> {
        val currentUserEmail = SecurityContextHolder.getContext().authentication.principal as String
        val currentUser = userService.findByEmail(currentUserEmail)
            .orElseThrow { UserNotFoundException("User not found! Please log in.") }

        listingService.createListing(listingRequest.toEntity(currentUser))

        return ResponseEntity.noContent().build()
    }

    @GetMapping
    fun getAllListings(@PageableDefault(size = 10) pageable: Pageable): ResponseEntity<Page<ListingResponse>> {
        val listings = listingService.getAllListings(pageable)
            .map { ListingResponse.fromEntity(it) }
        return ResponseEntity.ok(listings)
    }

    @GetMapping("/{id}")
    fun getListingById(@PathVariable id: Long): ResponseEntity<ListingResponse> {
        val listing = listingService.getListingById(id)
        return ResponseEntity.ok(ListingResponse.fromEntity(listing))
    }

    @GetMapping("/search")
    fun searchListings(
        @RequestParam(required = false) keywords: String?,
        @RequestParam(required = false) minPrice: Double?,
        @RequestParam(required = false) maxPrice: Double?,
        @RequestParam(required = false) category: String?,
        @RequestParam(required = false) minSustainabilityScore: Int?,
        @PageableDefault pageable: Pageable
    ): ResponseEntity<Page<ListingDocument>> {
        val searchResults = listingService.searchListingsByFilters(
            keywords,
            minPrice,
            maxPrice,
            category,
            minSustainabilityScore,
            pageable
        )

        return ResponseEntity.ok(searchResults)
    }

    @PutMapping("/{id}")
    fun updateListingById(
        @PathVariable id: Long,
        @RequestBody @Valid listingRequest: ListingRequest
    ): ResponseEntity<Void> {
        val currentUserEmail = SecurityContextHolder.getContext().authentication.principal as String

        listingService.updateListing(id, listingRequest, currentUserEmail)
        return ResponseEntity.noContent().build()
    }

    @DeleteMapping("/{id}")
    fun deleteListingById(@PathVariable id: Long): ResponseEntity<Void> {
        val currentUserEmail = SecurityContextHolder.getContext().authentication.principal as String

        listingService.deleteListing(id, currentUserEmail)
        return ResponseEntity.noContent().build()
    }
}