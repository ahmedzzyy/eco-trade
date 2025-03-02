package com.ecotrade.server.config

import org.apache.http.conn.ssl.TrustAllStrategy
import org.apache.http.ssl.SSLContextBuilder
import org.springframework.context.annotation.Configuration
import org.springframework.data.elasticsearch.client.ClientConfiguration
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration
import java.io.File
import javax.net.ssl.SSLContext

@Configuration
class ElasticConfig: ElasticsearchConfiguration() {

    override fun clientConfiguration(): ClientConfiguration {
        // DEV: ENV variables
        val elasticUsername = System.getenv("ELASTIC_USERNAME")
        val elasticPassword = System.getenv("ELASTIC_PASSWORD")

        // PROD: Implement `fetchFromSecretsManager`
//        val elasticUsername = fetchFromSecretsManager("ELASTIC_USERNAME")
//        val elasticPassword = fetchFromSecretsManager("ELASTIC_PASSWORD")

        return ClientConfiguration.builder()
            // DEV
            .connectedToLocalhost()
            // PROD
//            .connectedTo("<elasticsearch-host-change_me>:9200")
            .usingSsl(buildSSLContext())
            .withBasicAuth(elasticUsername, elasticPassword).build()
    }

    private fun buildSSLContext(): SSLContext {
        try {
            return SSLContextBuilder()
                // DEV
                .loadTrustMaterial(null,TrustAllStrategy.INSTANCE)
                // PROD
//                .loadTrustMaterial(
//                    File("path/to/change_me/truststore.jks"),
//                    "truststore-password".toCharArray()
//                )
                .build()
        } catch (e: Exception) { throw RuntimeException(e) }
    }
}