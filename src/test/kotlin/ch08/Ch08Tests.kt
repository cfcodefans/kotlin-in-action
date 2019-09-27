package ch08

import info
import org.junit.Test
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class Ch08Tests {
    companion object {
        val log: Logger = LoggerFactory.getLogger(Ch08Tests::class.java)
    }

    @Test
    fun testFunctionInvocation() {
        run {
            fun twoAndThree(oper: (Int, Int) -> Int) {
                log.info("The result is ${oper(2, 3)}")
            }
            twoAndThree { a, b -> a + b }
            twoAndThree { a, b -> a * b }
        }

        run {
            fun String.filter(predicate: (Char) -> Boolean): String {
                val sb: StringBuilder = StringBuilder()
                for (index in 0 until length) {
                    this[index].let { if (predicate(it)) sb.append(it) }
                }
                return sb.toString()
            }
            log.info("ab1c".filter { it in 'a'..'z' })
        }
    }

    @Test
    fun testJoinToStringDefault() {
        fun <T> Collection<T>.joinToString(
                separator: String = ", ",
                prefix: String = "",
                postfix: String = "",
                transform: (T) -> String = { it.toString() }
        ): String {
            val result: java.lang.StringBuilder = StringBuilder(prefix)
            for ((index, element) in this.withIndex()) {
                if (index > 0) result.append(separator)
                result.append(transform(element))
            }
            result.append(postfix)
            return result.toString()
        }

        val letters = listOf("Alpha", "Beta")
        log.info(letters.joinToString())
        log.info(letters.joinToString { it.toLowerCase() })
        log.info(letters.joinToString(separator = "! ", postfix = "! ", transform = { it.toUpperCase() }))
    }

    @Test
    fun testNullFunctionType() {
        fun <T> Collection<T>.joinToString(
                separator: String = ", ",
                prefix: String = "",
                postfix: String = "",
                transform: ((T) -> String)?
        ): String {
            val result: java.lang.StringBuilder = StringBuilder(prefix)
            for ((index, element) in this.withIndex()) {
                if (index > 0) result.append(separator)
                result.append(transform?.invoke(element) ?: element.toString())
            }
            result.append(postfix)
            return result.toString()
        }

        val letters = listOf("Alpha", "Beta")
        log.info(letters.joinToString())
        log.info(letters.joinToString { it.toLowerCase() })
        log.info(letters.joinToString(separator = "! ", postfix = "! ", transform = { it.toUpperCase() }))
    }

}

class Ch08Tests_HighOrderFunction {

    companion object {
        val log: Logger = LoggerFactory.getLogger(Ch08Tests_HighOrderFunction::class.java)

        enum class Delivery { STANDARD, EXPEDITED }

        class Order(val itemCount: Int)
    }

    @Test
    fun testHighOrderFunction() {
        run {
            fun getShippingCostCalculator(delivery: Delivery): (Order) -> Double = when (delivery) {
                Delivery.EXPEDITED -> { order -> 6 + 2.1 * order.itemCount }
                else -> { order -> 1.2 * order.itemCount }
            }
            log.info("Shipping costs ${getShippingCostCalculator(Delivery.EXPEDITED)(Order(3))}")
        }

        run {
            data class Person(val firstName: String, val lastName: String, val phoneNumber: String?)
            class ContactListFilters {
                var prefix: String = ""
                var onlyWithPhoneNumber: Boolean = false
                fun getPredicate(): (Person) -> Boolean {
                    val startsWithPrefix = { p: Person ->
                        p.firstName.startsWith(prefix) || p.lastName.startsWith(prefix)
                    }
                    if (!onlyWithPhoneNumber) return startsWithPrefix
                    return { startsWithPrefix(it) && it.phoneNumber != null }
                }
            }

            val contacts = listOf(
                    Person("Dmitry", "Jemerov", "123-4567"),
                    Person("Svetlana", "Istkova", null)
            )
            val contactListFilters = ContactListFilters()
            with(contactListFilters) {
                prefix = "Dm"
                onlyWithPhoneNumber = true
            }
            log.info(contacts.filter(contactListFilters.getPredicate()))
        }
    }
}

class Ch08Tests_Lambda {

    companion object {
        val log: Logger = LoggerFactory.getLogger(Ch08Tests_Lambda::class.java)

        enum class OS { WINDOWS, LINUX, MAC, IOS, ANDROID }

        data class SiteVisit(val path: String, val duration: Double, val os: OS)
    }

    @Test
    fun testLambdaAndCollection() {
        val logs = listOf(
                SiteVisit("/", 34.0, OS.WINDOWS),
                SiteVisit("/", 22.0, OS.MAC),
                SiteVisit("/login", 12.0, OS.WINDOWS),
                SiteVisit("/signup", 8.0, OS.IOS),
                SiteVisit("/", 16.3, OS.ANDROID))

        val averageWindowsDuration = logs.filter { it.os == OS.WINDOWS }
                .map(SiteVisit::duration)
                .average()

        log.info("averageWindowsDuration:\t{}", averageWindowsDuration)

        val mobileOSs = setOf(OS.IOS, OS.ANDROID)
        val averageMobileDuration = logs.filter { it.os in mobileOSs }
                .map(SiteVisit::duration)
                .average()

        log.info(averageMobileDuration)
    }

    @Test
    fun testExtensionAndLambda() {
        val logs = listOf(
                SiteVisit("/", 34.0, OS.WINDOWS),
                SiteVisit("/", 22.0, OS.MAC),
                SiteVisit("/login", 12.0, OS.WINDOWS),
                SiteVisit("/signup", 8.0, OS.IOS),
                SiteVisit("/", 16.3, OS.ANDROID))

        fun List<SiteVisit>.averageDurationFor(os: OS) =
                filter { it.os == os }.map(SiteVisit::duration).average()

        fun List<SiteVisit>.averageDurationFor(predicate: (SiteVisit) -> Boolean) =
                filter(predicate).map(SiteVisit::duration).average()

        log.info(logs.averageDurationFor(OS.WINDOWS))
        log.info(logs.averageDurationFor(OS.MAC))

        log.info(logs.averageDurationFor { it.os in setOf(OS.ANDROID, OS.IOS) })
        log.info(logs.averageDurationFor { it.os == OS.IOS && it.path == "/signup" })
    }

}