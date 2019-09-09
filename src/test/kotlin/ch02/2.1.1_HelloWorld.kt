package ch02.ex1_1_HelloWorld

import org.slf4j.Logger
import org.slf4j.LoggerFactory

fun main(args: Array<String>) {
    val log: Logger = LoggerFactory.getLogger(Thread.currentThread().name);
    log.info("Hello, world!")
    Thread.currentThread().stackTrace.reversedArray().forEach { log.info("$it") }
}

