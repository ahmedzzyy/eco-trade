package com.ecotrade.server.listing.controller

import com.ecotrade.server.elasticsearch.ListingDocument
import com.ecotrade.server.exception.UserNotFoundException
import com.ecotrade.server.listing.dto.ListingRequest
import com.ecotrade.server.listing.dto.ListingResponse
import com.ecotrade.server.listing.service.ListingService
import com.ecotrade.server.user.service.UserService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import jakarta.validation.Valid
import org.springdoc.core.annotations.ParameterObject
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

    @Operation(
        summary = "Create a new listing",
        security = [SecurityRequirement(name = "BearerAuth")]
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "204", description = "Listing created successfully"),
            ApiResponse(responseCode = "401", description = "Unauthorized - User must be logged in", content = [Content()])
        ]
    )
    @PostMapping
    fun createListing(@RequestBody @Valid listingRequest: ListingRequest): ResponseEntity<Void> {
        val currentUserEmail = SecurityContextHolder.getContext().authentication.principal as String
        val currentUser = userService.findByEmail(currentUserEmail)
            .orElseThrow { UserNotFoundException("User not found! Please log in.") }

        listingService.createListing(listingRequest.toEntity(currentUser))

        return ResponseEntity.noContent().build()
    }

    @Operation(
        summary = "Retrieve all listings with pagination",
        security = [SecurityRequirement(name = "BearerAuth")]
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "List of listings retrieved successfully", content = [
                Content(mediaType = "application/json", schema = Schema(implementation = ListingResponse::class))
            ])
        ]
    )
    @GetMapping
    fun getAllListings(
        @ParameterObject @PageableDefault(size = 10) pageable: Pageable
    ): ResponseEntity<Page<ListingResponse>> {
        val listings = listingService.getAllListings(pageable)
            .map { ListingResponse.fromEntity(it) }
        return ResponseEntity.ok(listings)
    }

    @Operation(
        summary = "Get a specific listing by its ID",
        security = [SecurityRequirement(name = "BearerAuth")]
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Listing retrieved successfully", content = [
                Content(mediaType = "application/json", schema = Schema(implementation = ListingResponse::class))
            ]),
            ApiResponse(responseCode = "404", description = "Listing not found", content = [Content()])
        ]
    )
    @GetMapping("/{id}")
    fun getListingById(@PathVariable id: Long): ResponseEntity<ListingResponse> {
        val listing = listingService.getListingById(id)
        return ResponseEntity.ok(ListingResponse.fromEntity(listing))
    }

    @Operation(
        summary = "Search for listings with filters and pagination",
        security = [SecurityRequirement(name = "BearerAuth")]
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Filtered listings retrieved successfully", content = [
                Content(mediaType = "application/json", schema = Schema(implementation = ListingDocument::class))
            ])
        ]
    )
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

    @Operation(
        summary = "Update an existing listing",
        security = [SecurityRequirement(name = "BearerAuth")]
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "204", description = "Listing updated successfully"),
            ApiResponse(responseCode = "401", description = "Unauthorized - User must be logged in", content = [Content()]),
            ApiResponse(responseCode = "403", description = "Forbidden - User is not the owner of the listing", content = [Content()])
        ]
    )
    @PutMapping("/{id}")
    fun updateListingById(
        @PathVariable id: Long,
        @RequestBody @Valid listingRequest: ListingRequest
    ): ResponseEntity<Void> {
        val currentUserEmail = SecurityContextHolder.getContext().authentication.principal as String

        listingService.updateListing(id, listingRequest, currentUserEmail)
        return ResponseEntity.noContent().build()
    }

    @Operation(
        summary = "Delete a listing",
        security = [SecurityRequirement(name = "BearerAuth")]
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "204", description = "Listing deleted successfully"),
            ApiResponse(responseCode = "401", description = "Unauthorized - User must be logged in", content = [Content()]),
            ApiResponse(responseCode = "403", description = "Forbidden - User is not the owner of the listing", content = [Content()])
        ]
    )
    @DeleteMapping("/{id}")
    fun deleteListingById(@PathVariable id: Long): ResponseEntity<Void> {
        val currentUserEmail = SecurityContextHolder.getContext().authentication.principal as String

        listingService.deleteListing(id, currentUserEmail)
        return ResponseEntity.noContent().build()
    }
}