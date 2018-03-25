package com.zzzmode.server.tools.charles

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
class CharlesApplication

fun main(args: Array<String>) {

    runApplication<CharlesApplication>(*args)
}
