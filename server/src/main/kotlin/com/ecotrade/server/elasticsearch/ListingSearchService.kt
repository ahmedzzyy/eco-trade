package com.ecotrade.server.elasticsearch

import com.ecotrade.server.listing.model.Listing
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.elasticsearch.client.elc.NativeQuery
import org.springframework.data.elasticsearch.core.ElasticsearchOperations
import org.springframework.data.elasticsearch.core.SearchHitSupport
import org.springframework.data.elasticsearch.core.SearchHits
import org.springframework.data.elasticsearch.core.SearchPage
import org.springframework.data.elasticsearch.core.search
import org.springframework.stereotype.Service

@Service
class ListingSearchService(
    private val listingSearchRepository: ListingSearchRepository,
    private val operations: ElasticsearchOperations
) {

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

    fun updateListing(listing: Listing) {
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

    fun searchListingsByFilters(
        keywords: String?,
        minPrice: Double?,
        maxPrice: Double?,
        category: String?,
        minSustainabilityScore: Int?,
        pageable: Pageable
    ): Page<ListingDocument> {
        val queryBuilder = NativeQuery.builder()
            .apply {
                if (keywords != null) {
                    withQuery { q ->
                        q.match { m -> m.field("title").query(keywords) }
                    }
                }
                if (category != null) {
                    withQuery { q ->
                        q.term { t ->
                            t.field("category").value(category)
                        }
                    }
                }
                if (maxPrice != null || minPrice != null) {
                    withQuery { q ->
                        q.range { r ->
                            r.number { n ->
                                n.field("price")
                                    .gte(minPrice)
                                    .lte(maxPrice)
                            }
                        }
                    }
                }
                if (minSustainabilityScore != null) {
                    withQuery { q ->
                        q.range { r ->
                            r.number { n ->
                                n.field("sustainabilityScore")
                                    .gte(minSustainabilityScore.toDouble())
                            }
                        }
                    }
                }
            }
            .withPageable(pageable)
            .build()

        val searchHits: SearchHits<ListingDocument> = operations.search(queryBuilder)
        val searchPage: SearchPage<ListingDocument> = SearchHitSupport.searchPageFor(searchHits, pageable)

        return searchPage.map { it.content }
    }
}