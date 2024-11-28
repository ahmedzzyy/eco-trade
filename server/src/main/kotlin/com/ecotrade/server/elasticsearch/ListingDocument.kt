package com.ecotrade.server.elasticsearch

import org.springframework.data.annotation.Id
import org.springframework.data.elasticsearch.annotations.Document
import org.springframework.data.elasticsearch.annotations.Field
import org.springframework.data.elasticsearch.annotations.FieldType

@Document(indexName = "listings")
data class ListingDocument(

    @Id
    val id: Long,

    @Field(type = FieldType.Text, analyzer = "standard")
    val title: String,

    @Field(type = FieldType.Text, analyzer = "standard")
    val description: String,

    @Field(type = FieldType.Double)
    val price: Double,

    @Field(type = FieldType.Keyword)
    val category: String,

    @Field(type = FieldType.Integer)
    val sustainabilityScore: Int = 0

)
