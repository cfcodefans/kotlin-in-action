package ch07

import info
import org.junit.Assert
import org.junit.Test
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.lang.IndexOutOfBoundsException
import java.math.BigDecimal
import kotlin.math.sqrt

class Ch07Tests {
    companion object {
        val log: Logger = LoggerFactory.getLogger(Ch07Tests::class.java)

        data class Complex(val real: Double, val imaginary: Double)

        operator fun Complex.plus(other: Complex): Complex = Complex(real + other.real, imaginary + other.imaginary)
    }

    @Test
    fun testOperator() {
        data class Point(val x: Int, val y: Int) {
            operator fun plus(other: Point): Point {
                return Point(x + other.x, y + other.y)
            }

            operator fun minus(other: Point): Point {
                return Point(x - other.x, y - other.y)
            }

            override fun equals(other: Any?): Boolean {
                return other === this || (other is Point && other.x == x && other.y == y)
            }

            open fun mod(): Float = sqrt((x * x + y * y).toDouble()).toFloat()
        }

        run {
            val p1 = Point(10, 20)
            val p2 = Point(30, 40)
            log.info("$p1 + $p2 = ${p1 + p2}")
            log.info("$p1 - $p2 = ${p1 - p2}")
            var p = Point(1, 2)
            p += Point(3, 4)
            log.info(p)
        }

        run {
            val c1 = Complex(0.5, 0.9)
            val c2 = Complex(0.3, 2.4)
            log.info(c1 + c2)
        }

        operator fun Point.times(scale: Double): Point {
            return Point((x * scale).toInt(), (y * scale).toInt())
        }

        val p = Point(6, 7)
        log.info(p * 1.5)

        operator fun Char.times(count: Int): String = toString().repeat(count)
        log.info('4' * 7)

        run {
            val numbers = ArrayList<Int>()
            numbers += 42
            log.info(numbers)
            numbers += listOf<Int>(4, 2)
            log.info(numbers)

            val newList = numbers + listOf(4, 5)
            log.info(newList)
//            val anotherList = newList += listOf(6, 7)
        }

        run {
            operator fun Point.unaryMinus(): Point {
                return Point(-x, -y)
            }

            val p = Point(10, 20)
            log.info(p, -p, -(-p))
        }

        run {
            operator fun BigDecimal.inc() = this + BigDecimal.ONE
            var bd = BigDecimal.ZERO
            log.info(bd++, ++bd)
        }

        run {
            val p = Point(10, 20)
            Assert.assertTrue(p == p)
            Assert.assertTrue(p === p)
            Assert.assertTrue(p !== p.copy())
            Assert.assertTrue(p == p.copy())
        }

        run {
            val p1 = Point(10, 20)
            val p2 = Point(10, 30)
            val p3 = Point(20, 20)

            operator fun Point.compareTo(other: Point): Int {
                return this.mod().compareTo(other.mod())
            }

            Assert.assertTrue(p1 < p2)
        }

        run {
            operator fun Point.get(index: Int): Int {
                return when (index) {
                    0 -> x
                    1 -> y
                    else -> throw IndexOutOfBoundsException("Invalid coordinate: $index")
                }
            }

            val p = Point(10, 20)
            Assert.assertEquals(10, p[0])
            Assert.assertEquals(20, p[1])
        }
    }

    @Test
    fun testBinaryOperator() {
        log.info(0x0F and 0xF0)
//        log.info(0x0F & 0xF0)
        log.info(0x0F or 0xF0)
        log.info(0x1 shl 4)
    }

}