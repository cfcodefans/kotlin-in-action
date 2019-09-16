package ch06

import info
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import probe
import java.io.BufferedReader
import java.io.StringReader
import java.util.*
import kotlin.collections.ArrayList

class Ch06Tests {
    companion object {
        val log: Logger = LoggerFactory.getLogger(Ch06Tests::class.java)

        data class Employee(val name: String, val manager: Employee?)

        data class Address(val streetAddress: String,
                           val zipCode: Int,
                           val city: String,
                           val country: String)

        data class Company(val name: String, val address: Address?)
        data class Person(val name: String?, val company: Company?)

        fun Person.countryName(): String {
            val country = this.company?.address?.country
            return if (country != null) country else "Unknowns"
        }
    }

    @Test
    fun testNullableTypes() {
        fun strLenSafe(s: String?): Int = if (s != null) s.length else 0
        val x: String? = null
        log.info(strLenSafe(x))
        log.info(strLenSafe("abc"))
    }

    @Test
    fun testSafeCall() {
        fun printAllCaps(s: String?) = log.info(s?.toUpperCase())
        printAllCaps("abc")
        printAllCaps(null)

        fun managerName(employee: Employee): String? = employee.manager?.name
        val ceo = Employee("Big Boss", null)
        val developer = Employee("Bob Smith", ceo)
        log.info(managerName(developer))
        log.info(managerName(ceo))

        val person = Person("Dmitry", null)
        log.info(person.countryName())
    }

    @Test
    fun testElvisOperator() {
        fun strLenSafe(s: String?): Int = s?.length ?: 0
        log.info(strLenSafe(null))
        log.info(strLenSafe("abc"))

        fun printShippingLabel(person: Person) {
            val address = person.company?.address ?: throw IllegalArgumentException("No address")
            with(address) {
                log.info("$streetAddress \n $zipCode $city, $country")
            }
        }

        val address = Address("Elsestr. 47", 80678, "Munich", "Germany")
        val jetbrains = Company("JetBrains", address)
        val person = Person("Dmitry", jetbrains)
        printShippingLabel(person)
        printShippingLabel(Person("Alexey", null))
    }

    @Test
    fun testSafeCastAs() {
        data class Point(val x: Int, val y: Int) {
            override fun equals(other: Any?): Boolean {
                val p = other as? Point ?: return false
                return p.x == x && p.y == y
            }

            override fun hashCode(): Int = Objects.hash(x, y)
        }

        val p1 = Point(4, 5)
        val p2 = p1.copy()
        log.info(p1 == p2)
        log.info(p1.equals(42))
    }

    @Test
    fun testNotNullAssertion() {
        fun ignoreNull(s: String?) {
            val sNotNull: String = s!!
            log.info(sNotNull.length)
        }
        ignoreNull(null)
    }

    @Test
    fun testLetFunction() {
        fun sendEmailTo(email: String) = log.info("Sending email to $email")
        var email: String? = "yole@example.com"
        email?.let { sendEmailTo(it) }
        email = null
        email?.let { sendEmailTo(it) }
    }

    @Test
    fun testExtensionForNullableType() {
        fun verifyUserInput(input: String?) {
            if (input.isNullOrBlank()) {
                log.error("Please fill in the required fields")
            }
        }
        verifyUserInput(" ")
        verifyUserInput(null)
    }

    @Test
    fun testNullabiltyOfTypeParam() {
        fun <T> printHashCode(t: T) {
            log.info(t?.hashCode())
        }
        printHashCode(null)
    }

    @Test
    fun testPlatformTypes() {
        fun yellAtSafe(p: Person) {
            log.info((p.name ?: "Anyone").toUpperCase() + "!!!")
        }
        yellAtSafe(Person(null, null))
    }

    @Test
    fun testPrimitiveTypes() {
        fun showProgress(progress: Int) {
            log.info("We're ${progress.coerceIn(0, 100)} done!")
        }
        showProgress(34)
        showProgress(143)
    }

    @Test
    fun testNullPrimitiveTypes() {
        data class Man(val name: String, val age: Int? = null) {
            fun isOlderThan(other: Man): Boolean? {
                if (age == null || other.age == null) return null
                return age > other.age
            }
        }
        log.info(Man("Sam", 35).isOlderThan(Man("Amy", 42)))
        log.info(Man("Sam", 35).isOlderThan(Man("Jane")))
    }

    @Test
    fun testNumberConversions() {
        val x = 1
        log.info(x.toLong() in listOf(1L, 2L, 3L))

        fun foo(l: Long) = l.toString(2)
        val b: Byte = 15
        val l = b + 0L
        log.info(foo(l))

//        log.info("  42 ".toInt())
        log.info("  42 ".trim().toInt())
    }

    @Test
    fun testNothingType() {
        fun fail(message: String): Nothing {
            throw IllegalStateException(message)
        }
        fail("Error occurred")
    }

    @Test
    fun testValidateNumbers() {
        val reader = BufferedReader(StringReader("1\nabc\n42"))
        val numbers: List<Int?> = reader.useLines {
            val result = ArrayList<Int?>()
            for (line in it) {
                result.add(try {
                    line.toInt()
                } catch (e: NumberFormatException) {
                    null
                })
            }
            result
        }

        log.info("Sum of valid numbers: ${numbers.filterNotNull().sum()}")
        log.info("invalid numbers: ${numbers.count { it == null }}")
    }

    @Test
    fun testReadonlyAndMutableCollections() {
        val src: Collection<Int> = arrayListOf(3, 5, 7)
        val target: MutableCollection<Int> = arrayListOf(1)
        src.forEach { target.add(it) }
        log.info(target)
    }

    @Test
    fun testArrays() {
        run {
            val array: Array<Int> = (1..10).toList().toTypedArray()
            for (i in array.indices) {
                log.info("element $i is: ${array[i]}")
            }
        }
        run {
            val letters: Array<String> = Array<String>(26) { i -> log.probe(4);('a' + i).toString() }
            log.info(letters)
        }
    }
}

class Ch06Tests_LateinitialzedProps {
    companion object {
        class SomeService {
            fun performAction(): String = "foo"
        }
    }

    private var someService: SomeService? = null
    private lateinit var anotherService: SomeService
    @Before
    fun setUp() {
        someService = SomeService()
        anotherService = SomeService()
    }

    @Test
    fun testAction() {
        Assert.assertEquals("foo", someService?.performAction())
        Assert.assertEquals("foo", anotherService.performAction())
    }
}