package ch02

import ch02.Ch02Tests.Companion.RGB.*
import inspect
import org.junit.Test
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.BufferedReader
import java.io.StringReader
import java.util.*

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

        enum class RGB(val r: Int, val g: Int, val b: Int) {
            RED(255, 0, 0),
            ORANGE(255, 165, 0),
            YELLOW(255, 255, 0),
            GREEN(0, 255, 0),
            BLUE(255, 0, 255),
            INDIGO(75, 0, 130),
            VIOLET(238, 130, 238);

            fun rgb() = (r * 256 + g) * 256 + b
        }

        interface Expr
        class Num(val value: Int) : Expr
        class Sum(val left: Expr, val right: Expr) : Expr

        fun selfInspect() = log.inspect(this.javaClass)
    }

    @Test
    fun testCompanionObject() {
        selfInspect()
        log.inspect(this.javaClass)
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
            log.inspect(Person::class.java)
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
        log.inspect(Rectangle::class.java)
    }

    @Test
    fun testRun() {
        run {
            log.info(Thread.currentThread().stackTrace.joinToString("\n\t"))
        }
        log.info(Thread.currentThread().stackTrace.joinToString("\n\t"))
    }

    @Test
    fun testEnumColor1() {
//        enum class Color {
//            RED, ORANGE, YELLOW, GREEN, BLUE, INDIGO, VIOLET
//        }
        log.info(Color.values().joinToString("\n\t"))
        log.info(Color.values().map { Pair(it.ordinal, it) }.joinToString("\n\t"))
    }

    @Test
    fun testWhenEnum() {

        fun getMnemonic(rgb: RGB) = when (rgb) {
            RGB.RED -> "Richard"
            RGB.ORANGE -> "of"
            RGB.YELLOW -> "York"
            RGB.GREEN -> "Gave"
            RGB.BLUE -> "Battle"
            RGB.INDIGO -> "In"
            RGB.VIOLET -> "Vain"
        }
        RGB.values().forEach { log.info(getMnemonic(it)) }

        fun getWarmth(rgb: RGB) = when (rgb) {
            RGB.RED, RGB.ORANGE, RGB.YELLOW -> "warm"
            RGB.GREEN -> "neutral"
            RGB.BLUE, RGB.INDIGO, RGB.VIOLET -> "cold"
        }
        RGB.values().forEach { log.info(getWarmth(it)) }

        fun mix(c1: RGB, c2: RGB) = when (setOf<RGB>(c1, c2)) {
            setOf(RGB.RED, RGB.YELLOW) -> RGB.ORANGE
            setOf(RGB.BLUE, RGB.YELLOW) -> RGB.GREEN
            setOf(RGB.BLUE, RGB.VIOLET) -> RGB.INDIGO
            else -> throw Exception("Dirty color")
        }
        log.info("{}", mix(RGB.BLUE, RGB.YELLOW))

        fun mixOptimized(c1: RGB, c2: RGB) = when {
            (c1 == RED && c2 == YELLOW) || (c2 == RED && c1 == YELLOW) -> ORANGE
            (c1 == BLUE && c2 == YELLOW) || (c2 == YELLOW && c1 == BLUE) -> GREEN
            (c1 == VIOLET && c2 == BLUE) || (c2 == BLUE && c1 == VIOLET) -> INDIGO
            else -> throw Exception("Dirty color")
        }
        log.info("{}", mix(RGB.BLUE, RGB.YELLOW))
    }

    @Test
    fun testSmartCast() {
        //        interface Expr
        fun eval(e: Expr): Int {
            if (e is Num) {
                val n = e as Num
                return n.value
            }
            if (e is Sum) {
                return eval(e.right) + eval(e.left)
            }
            throw IllegalArgumentException("Unknown expression")
        }

        log.info(eval(
                Sum(
                        Sum(Num(4), Num(3)),
                        Num(5))
        ).toString())

        fun eval_when(e: Expr): Int = when (e) {
            is Num -> e.value
            is Sum -> eval_when(e.right) + eval_when(e.left)
            else -> throw IllegalArgumentException("Unknown expression")
        }
        log.info(eval_when(
                Sum(
                        Sum(Num(4), Num(3)),
                        Num(5))
        ).toString())

        fun eval_logging(e: Expr): Int = when (e) {
            is Num -> {
                log.info("num: ${e.value}")
                e.value
            }
            is Sum -> {
                val left = eval_logging(e.left)
                val right = eval_logging(e.right)
                log.info("sum: $left + $right")
                left + right
            }
            else -> throw IllegalArgumentException("Unknown expression")
        }
        log.info(eval_logging(
                Sum(
                        Sum(Num(4), Num(3)),
                        Num(5))
        ).toString())
    }

    @Test
    fun testFizzBuzz() {
        fun fizzBuzz(i: Int) = when {
            i % 15 == 0 -> "FizzBuzz"
            i % 3 == 0 -> "Fizz"
            i % 5 == 0 -> "Buzz"
            else -> "$i "
        }
        for (i in 1..100) {
            log.info(fizzBuzz(i))
        }

        for (i in 100 downTo 1 step 2) {
            log.info(fizzBuzz(i))
        }
    }

    @Test
    fun testIteratingOverMaps() {
        val binaryReps: TreeMap<Char, String> = TreeMap()
        for (c in 'A'..'F') {
            binaryReps[c] = Integer.toBinaryString(c.toInt())
        }
        for ((letter, binary) in binaryReps) log.info("$letter = $binary")
    }

    @Test
    fun testAnInCheck() {
        fun isLetter(c: Char) = c in 'a'..'z' || c in 'A'..'Z'
        fun isNoDigit(c: Char) = c !in '0'..'9'

        log.info("isLetter('1')=${isLetter('1')}")
        log.info("isNoDigit('1')=${isNoDigit('1')}")

        fun recoginze(c: Char) = when (c) {
            in '0'..'9' -> "It is digit!"
            in 'a'..'z', in 'A'..'Z' -> "It is a letter!"
            else -> "I don't know..."
        }
        log.info("recoginze(' ')=${recoginze(' ')}")
    }

    @Test
    fun testTryCatchFinally() {
        fun readNumber(reader: BufferedReader): Int? {
            try {
                val line: String = reader.readLine()
                return Integer.parseInt(line)
            } catch (e: NumberFormatException) {
                return null
            } finally {
                reader.close()
            }
        }
        log.info("{}", readNumber(BufferedReader(StringReader("239"))))
    }

    @Test
    fun testTryCloseable() {
        val reader = BufferedReader(StringReader("239"))
        log.info("{}", reader.use {
            try {
                log.info(Thread.currentThread().stackTrace.joinToString("\n\t"))
                Integer.parseInt(it.readLine())
            } catch (e: NumberFormatException) {
                null
            }
        })
    }

    @Test
    fun testTryAsExpression() {
        log.info("try :{}", try {
            Integer.parseInt(" ")
        } catch (e: NumberFormatException) {
            -1
        })
    }

    @Test
    fun testTryAsExpression1() {
        fun readNumber(reader: BufferedReader) {
            val number = try {
                Integer.parseInt(reader.readLine())
            } catch (e: NumberFormatException) {
                return
            }
            println(number)
        }

        val reader = BufferedReader(StringReader("not a number"))
        readNumber(reader)
    }
}

