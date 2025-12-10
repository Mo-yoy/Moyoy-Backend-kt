package com.moyoy

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class MoyoyApplication

fun main(args: Array<String>) {
	runApplication<MoyoyApplication>(*args)
}
