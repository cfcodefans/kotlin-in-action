package ch07

import info
import org.junit.Assert
import org.junit.Test
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.beans.PropertyChangeListener
import java.beans.PropertyChangeSupport
import java.math.BigDecimal
import java.time.LocalDate
import kotlin.math.sqrt
import kotlin.properties.Delegates
import kotlin.reflect.KProperty

class Ch07Tests {
    companion object {
        val log: Logger = LoggerFactory.getLogger(Ch07Tests::class.java)

        data class Complex(val real: Double, val imaginary: Double)

        operator fun Complex.plus(other: Complex): Complex = Complex(real + other.real, imaginary + other.imaginary)
    }

    @Test
    fun testOperator() {
        data class Point(var x: Int, var y: Int) {
            operator fun plus(other: Point): Point {
                return Point(x + other.x, y + other.y)
            }

            inline operator fun minus(other: Point): Point {
                return Point(x - other.x, y - other.y)
            }

            override fun equals(other: Any?): Boolean {
                return other === this || (other is Point && other.x == x && other.y == y)
            }

            open fun mod(): Float = sqrt((x * x + y * y).toDouble()).toFloat()
        }

        data class Rectangle(var upperLeft: Point, var lowerRight: Point) {
            inline operator fun contains(p: Point): Boolean {
                return p.x in upperLeft.x until lowerRight.x
                        && p.y in upperLeft.y until lowerRight.y
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

            operator fun Point.set(index: Int, v: Int) {
                when (index) {
                    0 -> x = v
                    1 -> y = v
                    else -> throw IndexOutOfBoundsException("Invalid coordinate: $index")
                }
            }

            val p = Point(10, 20)
            Assert.assertEquals(10, p[0])
            Assert.assertEquals(20, p[1])
            p[1] = 42
            Assert.assertEquals(p.y, 42)
        }
    }

    @Test
    fun testBinaryOperator() {
        log.info(0x0F and 0xF0)
//        log.info(0x0F & 0xF0)
        log.info(0x0F or 0xF0)
        log.info(0x1 shl 4)
    }

    @Test
    fun testRangePriority() {
        val n = 9
        log.info(0..(n + 1))
        (0..n).forEach { log.info(it) }
    }

    @Test
    fun testIteratorConvention() {
        //        operator fun ClosedRange<LocalDate>.iterator(): Iterator<LocalDate> = object : Iterator<LocalDate> {
        operator fun ClosedRange<LocalDate>.iterator(): Iterator<LocalDate> = object : Iterator<LocalDate> {
            var current = start

            init {
                log.info("initiating Iterator on ${start} to ${endInclusive}")
            }

            override fun hasNext(): Boolean = current <= endInclusive
            override fun next(): LocalDate = current.apply { current = current.plusDays(1) }
        }

        val newYear = LocalDate.ofYearDay(2017, 1)
        val daysOff = newYear.minusDays(1)..newYear
        for (dayOff in daysOff) log.info(dayOff)

        val it1 = daysOff.iterator()
        val it2 = daysOff.iterator()
        Assert.assertFalse(it1 === it2)
    }

    @Test
    fun testDestructingDeclaration() {
        fun printEntries(map: Map<String, String>) {
            for ((k, v) in map) {
                log.info("$k -> $v")
            }
        }

        val map = mapOf("Oracle" to "Java", "JetBrains" to "Kotlin")
        printEntries(map)

        val (x, y) = Complex(10.toDouble(), 20.toDouble())
        log.info(x, y)

        run {
            data class NameComponents(val name: String, val ext: String)

            fun splitFilename(fullName: String): NameComponents {
                val result = fullName.split('.', limit = 2)
                return NameComponents(result[0], result[1])
            }

            val (name: String, ext: String) = splitFilename("example.kt")
            log.info(name, ext)
        }
    }

    @Test
    fun testLazyEmails() {
        class Email

        class Person(val name: String) {
            fun loadEmails(person: Person): List<Email> {
                log.info("Load emails for ${person.name}")
                return listOf()
            }

            private var _emails: List<Email>? = null
            val emails: List<Email>
                get() = _emails ?: loadEmails(this).apply { _emails = this }

            val lazyEmails by lazy { loadEmails(this) }
        }

        val p = Person("Alice")
        p.emails
        p.emails

        val p1 = Person("Alice")
        p.lazyEmails
        p.lazyEmails
    }
}

class Ch07_Tests_ImplementingDelegatedProperties {
    companion object {
        val log: Logger = LoggerFactory.getLogger(Ch07_Tests_ImplementingDelegatedProperties::class.java)

        open class PropertyChangeAware {
            protected val changeSupport = PropertyChangeSupport(this)

            fun addPropertyChangeListener(listener: PropertyChangeListener) {
                changeSupport.addPropertyChangeListener(listener)
            }

            fun removePropertyChangeListener(listener: PropertyChangeListener) {
                changeSupport.removePropertyChangeListener(listener)
            }
        }

        open class KObservableProperty<B, T>(val bean: B, var propValue: T, val changeSupport: PropertyChangeSupport) {
            operator fun getValue(b: B, prop: KProperty<*>) = propValue
            operator fun setValue(b: B, prop: KProperty<*>, newValue: T) {
                val old = propValue
                propValue = newValue
                changeSupport.firePropertyChange(prop.name, old, newValue)
            }
        }

        class Person3(val name: String, age: Int, salary: Int) : PropertyChangeAware() {
            companion object {
                class PersonProperty<T>(p: Person3, t: T,
                                        changeSupport: PropertyChangeSupport) : KObservableProperty<Person3, T>(p, t, changeSupport)
            }

            var age: Int by PersonProperty<Int>(this, age, changeSupport)
            var salary: Int by PersonProperty<Int>(this, salary, changeSupport)
        }
    }

    @Test
    fun testPropertyChange1() {
        class Person(val name: String, age: Int, salary: Int) : PropertyChangeAware() {
            var age: Int = age
                set(newValue) {
                    val oldValue = field
                    field = newValue
                    changeSupport.firePropertyChange("age", oldValue, newValue)
                }

            var salary: Int = salary
                set(newValue) {
                    val oldValue = field
                    field = newValue
                    changeSupport.firePropertyChange("salary", oldValue, newValue)
                }
        }

        val p = Person("Dmitry", 43, 2000)
        p.addPropertyChangeListener(PropertyChangeListener { event ->
            log.info("Property ${event.propertyName} changed from ${event.oldValue} to ${event.newValue}")
        })

        p.age = 35
        p.salary = 2100
    }

    @Test
    fun testPropertyChange2() {
        class ObservableProperty<T>(val propName: String, var propValue: T, val changeSupport: PropertyChangeSupport) {
            fun getValue(): T = propValue
            fun setValue(newValue: T) {
                val old = propValue
                propValue = newValue
                changeSupport.firePropertyChange(propName, old, newValue)
            }
        }

        class Person(val name: String, age: Int, salary: Int) : PropertyChangeAware() {
            val _age: ObservableProperty<Int> = ObservableProperty("age", age, changeSupport)
            var age: Int
                get() = _age.getValue()
                set(value) {
                    _age.setValue(value)
                }
            val _salary: ObservableProperty<Int> = ObservableProperty("age", age, changeSupport)
            var salary: Int
                get() = _salary.getValue()
                set(value) {
                    _salary.setValue(value)
                }
        }

        val p = Person("Dmitry", 43, 2000)
        p.addPropertyChangeListener(PropertyChangeListener { event ->
            log.info("Property ${event.propertyName} changed from ${event.oldValue} to ${event.newValue}")
        })

        p.age = 35
        p.salary = 2100
    }

    @Test
    fun testPropertyChange3() {
        val p = Person3("Dmitry", 43, 2000)
        p.addPropertyChangeListener(PropertyChangeListener { event ->
            log.info("Property ${event.propertyName} changed from ${event.oldValue} to ${event.newValue}")
        })
        p.age = 35
        p.salary = 2100
    }

    @Test
    fun testPropertyDelegate() {
        class Person(val name: String, age: Int, salary: Int) : PropertyChangeAware() {
            private val observer = { prop: KProperty<*>, oldValue: Int, newValue: Int ->
                changeSupport.firePropertyChange(prop.name, oldValue, newValue)
            }
            var age: Int by Delegates.observable(age, observer)
            var salary: Int by Delegates.observable(salary, observer)
        }

        val p = Person3("Dmitry", 43, 2000)
        p.addPropertyChangeListener(PropertyChangeListener { event ->
            log.info("Property ${event.propertyName} changed from ${event.oldValue} to ${event.newValue}")
        })
        p.age = 35
        p.salary = 2100
    }
}

