package com.ecotrade.server

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class EcoTradeApplication

fun main(args: Array<String>) {
	runApplication<EcoTradeApplication>(*args)
}
