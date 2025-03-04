package com.ecotrade.server.listing.model

import com.ecotrade.server.user.model.User
import jakarta.persistence.*

@Entity
@Table(name = "listings")
data class Listing(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false)
    val title: String,

    @Column(columnDefinition = "TEXT", nullable = false)
    val description: String,

    @Column(nullable = false)
    val price: Double,

    @Column(nullable = false)
    val category: String,

    val sustainabilityScore: Int = 0,

    @ElementCollection
    val images: List<String> = listOf(),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User
)