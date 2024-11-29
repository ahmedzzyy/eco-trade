package com.ecotrade.server.config

import org.apache.http.conn.ssl.TrustAllStrategy
import org.apache.http.ssl.SSLContextBuilder
import org.springframework.context.annotation.Configuration
import org.springframework.data.elasticsearch.client.ClientConfiguration
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration
import javax.net.ssl.SSLContext

@Configuration
class ElasticConfig: ElasticsearchConfiguration() {

    override fun clientConfiguration(): ClientConfiguration {
        return ClientConfiguration.builder()
            .connectedToLocalhost() // Currently Localhost
            .usingSsl(buildSSLContext())
            .withBasicAuth(
                System.getenv("ELASTIC_USERNAME"),
                System.getenv("ELASTIC_PASSWORD")
            ).build()
    }

    private fun buildSSLContext(): SSLContext {

        try {
            return SSLContextBuilder().loadTrustMaterial(null,TrustAllStrategy.INSTANCE)
                .build()
        } catch (e: Exception) { throw RuntimeException(e) }
    }
}