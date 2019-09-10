package ch03

import ch02.Ch02Tests
import ch03.Ch03Tests.Companion.log
import org.junit.Test
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import probe
import java.lang.StringBuilder

class Ch03Tests {
    companion object {
        val log: Logger = LoggerFactory.getLogger(Ch03Tests::class.java)
    }

    @Test
    fun testCreatingCollectionsInKoklin() {
        val set = hashSetOf(1, 7, 53)
        val list = arrayListOf(1, 7, 53)
        val map = hashMapOf(1 to "one", 7 to "seven", 53 to "fifty-three")

        log.info("{}", set.javaClass)
        log.info("{}", list.javaClass)
        log.info("{}", map.javaClass)

        val strings = listOf("first", "second", "fourteenth")
        log.info("{}", strings.javaClass)
    }

    @Test
    fun printCollecions() {
        val set = hashSetOf(1, 7, 53)
        val list = arrayListOf(1, 7, 53)
        val map = hashMapOf(1 to "one", 7 to "seven", 53 to "fifty-three")
        log.info("{}", set)
        log.info("{}", list)
        log.info("{}", map)
    }

    @Test
    fun testCreatingCollectionsInKoklin1() {
        val strings = listOf("first", "second", "fourteenth")
        log.info("$strings.last() = ${strings.last()}")
        log.info("$strings.first() = ${strings.first()}")
        log.info("$strings[1] = ${strings[1]}")
        log.info("$strings.max() = ${strings.max()}")
        val numbers = setOf(1, 14, 2)
        log.info("$numbers.max() = ${numbers.max()}")
    }

    @Test
    fun testJoinToStr() {
        fun <T> joinToStr(col: Collection<T>, separator: String, prefix: String, postfix: String): String {
            val result: StringBuilder = StringBuilder(prefix)
            for ((index, element) in col.withIndex()) {
                if (index > 0) result.append(separator)
                result.append(element)
            }
            result.append(postfix)
            return result.toString()
        }
        log.info(joinToStr(
                Thread.currentThread().stackTrace.toList(),
                "\n\t",
                "{",
                "}"
        ))
    }

    @Test
    fun testJoinToStrFinal() {
        fun <T> Collection<T>.joinToStr(
                separator: String = ", ",
                prefix: String = "",
                postfix: String = ""
        ): String {
            val result: StringBuilder = StringBuilder(prefix)
            for ((index, element) in this.withIndex()) {
                if (index > 0) result.append(separator)
                result.append(element)
            }
            result.append(postfix)
            return result.toString()
        }
        log.info(Thread.currentThread().stackTrace.toList().joinToStr(
                separator = "\n\t",
                postfix = "!"
        ))
    }

    @Test
    fun testNoOverridingForExtensionFunctions() {
        val view: View = Button()
        view.click()
    }

    @Test
    fun testNoOverridingForExtensionFunctions1() {
        fun View.showOff() {
            log.probe(5)
            log.info("I'm a view!")
        }

        fun Button.showOff() = log.info("I'm a button!")
        val view: View = Button()
        view.showOff()
        (view as Button).showOff()
        val button: Button = Button()
        button.showOff()
    }
}

open class View {
    open fun click() = log.info("View clicked")
}

class Button : View() {

    override fun click() {
        log.probe(5)
        log.info("Button clicked")
    }
}

