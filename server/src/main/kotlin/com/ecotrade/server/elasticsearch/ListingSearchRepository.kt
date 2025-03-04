package com.ecotrade.server.elasticsearch

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository
import org.springframework.stereotype.Repository

@Repository
interface ListingSearchRepository: ElasticsearchRepository<ListingDocument, Long>