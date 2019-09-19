package ch07

import info
import org.junit.Test
import org.slf4j.Logger
import org.slf4j.LoggerFactory

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
    }

    @Test
    fun testBinaryOperator() {
        log.info(0x0F and 0xF0)
        log.info(0x0F or 0xF0)
        log.info(0x1 shl 4)
    }
}