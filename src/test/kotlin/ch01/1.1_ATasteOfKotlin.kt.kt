package ch01.ex1_ATasteOfKotlin

import org.junit.Test
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import probe

data class Person(val name: String, val age: Int? = null)

fun main(args: Array<String>) {
    val persons = listOf(Person("Alice"), Person("Bob", age = 29))
    val oldest = persons.maxBy { it.age ?: 0 }
    println("The oldest is $oldest")
}

public class Ch01Tests {
    companion object {
        val log: Logger = LoggerFactory.getLogger(Ch01Tests::class.java)

        fun whatIsCompanionObject() {
            log.probe(5)
            val javaClass = this.javaClass
            log.info("what is this in companion object: {}", javaClass)

        }
    }

    @Test
    fun testCompanion() {
        whatIsCompanionObject()
    }

    @Test
    fun test() {
        val persons = listOf(Person("Alice"), Person("Bob", age = 29))
        val oldest = persons.maxBy { it.age ?: 0 }
        log.info("The oldest is $oldest")
    }
}