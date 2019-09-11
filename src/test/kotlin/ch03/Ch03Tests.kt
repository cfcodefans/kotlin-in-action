package ch03

import ch02.Ch02Tests
import ch03.Ch03Tests.Companion.log
import org.junit.Assert
import org.junit.Test
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import probe
import java.lang.StringBuilder
import java.time.DayOfWeek
import java.time.temporal.WeekFields

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

    val String.lastChar: Char
        get() = this[this.length - 1]
    var StringBuilder.lastChar: Char
        get() = this[this.length - 1]
        set(v: Char) {
            this.setCharAt(length - 1, v)
            log.probe()
        }

    @Test
    fun testExtensionProp() {
        log.info("Kotlin".lastChar.toString())
        StringBuilder("Kotlin?").run {
            log.probe()
            this.lastChar = '!'
            log.info(this.toString())
        }
    }

    @Test
    fun testExtensionMethod() {
        fun String.isPalindrome(): Boolean = !(0..this.length / 2).any { it -> this[it] != this[this.length - 1 - it] }

        Assert.assertTrue("ll".isPalindrome())
        Assert.assertTrue("lol".isPalindrome())
        Assert.assertFalse("kotlin".isPalindrome())
    }

    @Test
    fun testVarargs() {
        log.info("{}, {}, {}, {}, {}, {}, {}", DayOfWeek.values())
        log.info("{}, {}, {}, {}, {}, {}, {}", *DayOfWeek.values())
    }

    @Test
    fun testSplittingString() {
        log.info("{}", "12.345-6.A".split("\\.|-"))
        log.info("{}", "12.345-6.A".split("\\.|-".toRegex()))
        log.info("{}", "12.345-6.A".split(".", "-"))
    }

    @Test
    fun testParsePath() {
        val path: String = "/Users/yole/kotlin-book/chapter.adoc"
        val dir = path.substringBeforeLast("/")
        val fullname = path.substringAfterLast("/")
        val filename = path.substringBeforeLast(".")
        val extension = path.substringAfterLast(".")

        log.info("Dir:\t$dir, Name:\t$filename, Ext:\t$extension")
    }

    @Test
    fun testMultilineTriplequote() {
        val kotlinLogo = """ 
                            | //
                           .|//
                           .|/ \"""
        log.info(kotlinLogo.trimIndent())
    }

    @Test
    fun testValidateUser() {
        data class User(val id: Int, val name: String, val address: String)

        fun saveUser(user: User) {
            if (user.name.isEmpty()) throw IllegalArgumentException("Can't save user ${user.id}: empty name")
            if (user.address.isEmpty()) throw IllegalArgumentException("Can't save user ${user.id}: empty Address")
        }
        try {
            saveUser(User(1, "", ""))
            Assert.fail()
        } catch (e: Exception) {
            log.error("expected", e)
        }

        fun validate(user: User, value: String, fieldName: String) {
            if (value.isEmpty()) throw IllegalArgumentException("Can't save user ${user.id}: empty $fieldName")
        }

        fun User.validate() {
            validate(this, this.name, "Name")
            validate(this, this.address, "Address")
        }
        try {
            User(1, "", "").validate()
            Assert.fail()
        } catch (e: Exception) {
            log.error("expected", e)
        }
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

