package ch02

import org.junit.Test
import org.slf4j.Logger
import org.slf4j.LoggerFactory

fun max(a: Int, b: Int): Int {
    Ch02Tests.log.info(Thread.currentThread().stackTrace.joinToString("\n\t"))
    return if (a > b) a else b
}

class Ch02Tests {
    companion object {
        val log: Logger = LoggerFactory.getLogger(Ch02Tests::class.java)
        inline fun inlineMax(a: Int, b: Int): Int {
            log.info(Thread.currentThread().stackTrace.joinToString("\n\t"))
            return if (a > b) a else b
        }

        enum class Color {
            RED, ORANGE, YELLOW, GREEN, BLUE, INDIGO, VIOLET
        }
    }

    @Test
    fun testFunction1() {
        log.info("max(2,4) = ${max(2, 4)}")
    }

    @Test
    fun testFunction2() {
        fun max(a: Int, b: Int): Int {
            log.info(Thread.currentThread().stackTrace.joinToString("\n\t"))
            return if (a > b) a else b
        }
        log.info("max(2,4) = ${max(2, 4)}")
    }

    @Test
    fun testFunction3() {
        log.info("max(2,4) = ${inlineMax(2, 4)}")
    }

    @Test
    fun testStringTemplates() {
        val name = "what"
        log.info("what is your name? $name")
        log.info("What is your name initial? ${name[0]}")
        log.info("Is your name started with C? ${if (name[0] == 'c') "Yes" else "No"}")
    }

    @Test
    fun testProperties() {
        //        class Person(name: String, isMarried: Boolean)
//        val bob = Person("Bob", true)
//        log.info(bob.name)
//        log.info(bob.isMarried)
        run {
            class Person(val name: String, var isMarried: Boolean)

            val bob = Person("Bob", true)
            log.info(bob.name)
            log.info(bob.isMarried.toString())
        }
//        log.info(bob.isMarried.toString())
        run {
            class Person(_name: String, _isMarried: Boolean) {
                //                private val name: String = _name
                val name: String = _name
                var isMarried: Boolean = _isMarried
            }

            val bob = Person("Bob", true)
            log.info(bob.name)
            log.info(bob.isMarried.toString())
        }
    }

    @Test
    fun testCustomAccessors() {
        class Rectangle(val height: Int, val width: Int) {
            //            val height:Int = 0
            val isSquare: Boolean get() = height == width
        }

        val rect = Rectangle(41, 43)
        log.info(rect.isSquare.toString())
    }

    @Test
    fun testRun() {
        run {
            log.info(Thread.currentThread().stackTrace.joinToString("\n\t"))
        }
    }

    @Test
    fun testEnumColor1() {
//        enum class Color {
//            RED, ORANGE, YELLOW, GREEN, BLUE, INDIGO, VIOLET
//        }
        log.info(Color.values().joinToString("\n\t"))
        log.info(Color.values().map { Pair(it.ordinal, it) }.joinToString("\n\t"))
    }
}