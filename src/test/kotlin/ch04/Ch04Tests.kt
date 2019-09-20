package ch04

import info
import inspect
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import probe
import java.io.Serializable
import java.util.*
import java.util.concurrent.Executors
import kotlin.Comparator
import kotlin.collections.HashSet


class Ch04Tests {
    companion object {
        val log: Logger = LoggerFactory.getLogger(Ch04Tests::class.java)

        interface Clickable {
            fun click() {
                log.probe(4)
                log.info("dummy")
                lastClick = System.currentTimeMillis()
            }

            var lastClick: Long // = -1
        }
    }

    @Test
    fun testInterface() {
        class Button(override var lastClick: Long = -1) : Clickable {
            override fun click() {
                super.click()
                log.info("Button.click()")
            }
        }
        Button().click()
    }

    @Test
    fun testProp() {
        class Person(val name: String, age: Int, salary: Int)
        log.inspect(Person::class.java, true)
    }

    @Test
    fun testClzConstructor() {
        class User(val nickname: String,
                   val isSubscribed: Boolean = true)

        val alice = User("Alice")
        log.info(alice.isSubscribed)
        val bob = User("Bob", false)
        log.info(bob.isSubscribed)
        val carol = User("Carol", isSubscribed = false)
        log.info(carol.isSubscribed)
    }

    @Test
    fun testToString() {
        class Client(val name: String, val postalCode: Int) {
            override fun toString(): String = "Client(name=$name, postalCode=$postalCode"
        }

        val client1 = Client("Alice", 342562)
        log.info(client1)
    }

    @Test
    fun testEquality() {
        class Client(val name: String, val postalCode: Int)

        val client1 = Client("Alice", 342562)
        val client2 = Client("Alice", 342562)
        Assert.assertNotEquals(client1, client2)

        class Customer(val name: String, val postalCode: Int) {
            override fun equals(other: Any?): Boolean =
                    other != null
                            && other is Customer
                            && Objects.equals(this.name, other.name)
                            && Objects.equals(this.postalCode, other.postalCode)
        }

        val customer1 = Customer("Alice", 342562)
        val customer2 = Customer("Alice", 342562)
        Assert.assertEquals(customer1, customer2)
    }
}

class Ch04Tests_1 {
    companion object {
        val log: Logger = LoggerFactory.getLogger(Ch04Tests_1::class.java)

        interface Clickable {
            fun click()
            fun showOff() = log.info("I'm clickable")
        }

        interface Focusable {
            fun setFocus(b: Boolean) = log.info("I ${if (b) "got" else "lost"} focus.")
            fun showOff() = log.info("I'm focusable!")
        }

        class Button : Clickable, Focusable {
            override fun click() = log.info("I was clicked")
            override fun showOff() {
                super<Clickable>.showOff()
                super<Focusable>.showOff()
            }
        }
    }

    @Test
    fun testDiamondInheritance() {
        val button = Button()
        button.showOff()
        button.setFocus(true)
        button.click()
    }

    @Test
    fun testInspect() {
        log.inspect(Button::class.java)
    }
}

class Ch04Tests_2 {
    companion object {
        val log: Logger = LoggerFactory.getLogger(Ch04Tests_2::class.java)

        interface Clickable {
            fun click()
            fun showoff() = log.info("I'am clickable")
        }

        open class RichButton : Clickable {
            override fun click() {}
            open fun animate() {}
            fun disable() {}
        }

        open class Button : Clickable {
            final override fun click() {}
        }
    }

}

class Ch04Tests_3 {
    companion object {
        val log: Logger = LoggerFactory.getLogger(Ch04Tests_3::class.java)

        interface State : Serializable
        interface View {
            fun getCurrentState(): State
            fun restoreState(state: State) {}
        }

        class Button : View {
            class ButtonState : State {}

            override fun getCurrentState(): State = ButtonState()
            override fun restoreState(state: State) {}
        }

        //inner class Inner {}
    }

    inner class Inner {
        fun getOuterRef(): Ch04Tests_3 = this@Ch04Tests_3
    }

    @Test
    fun testInnerCls() {
        Assert.assertTrue(Inner().getOuterRef() == this)
    }
}

class Ch04Tests_sealedClz {
    companion object {
        val log: Logger = LoggerFactory.getLogger(Ch04Tests_sealedClz::class.java)

        interface IExpr
        class Num(val value: Int) : IExpr
        class Sum(val left: IExpr, val right: IExpr) : IExpr

        fun eval(e: IExpr): Int =
                when (e) {
                    is Num -> e.value
                    is Sum -> eval(e.right) + eval(e.left)
                    else -> throw IllegalArgumentException("Unknown expression")
                }

        sealed class Expr {
            class Num(val value: Int) : Expr()
            class Sum(val left: Expr, val right: Expr) : Expr()
        }

        fun eval(e: Expr): Int =
                when (e) {
                    is Expr.Num -> e.value
                    is Expr.Sum -> eval(e.right) + eval(e.left)
                }
    }

    @Test
    fun test() {
        log.info("{}", eval(Sum(Sum(Num(1), Num(2)), Num(4))))
        log.info("{}", eval(Expr.Sum(Expr.Sum(Expr.Num(1), Expr.Num(2)), Expr.Num(4))))
    }
}

class Ch04Tests_ImplementingPropertiesDeclaredInInterfaces {
    companion object {
        val log: Logger = LoggerFactory.getLogger(Ch04Tests_ImplementingPropertiesDeclaredInInterfaces::class.java)

        fun getFacebookName(accountId: Int) = "fb:$accountId"
        interface IUser {
            val nickname: String
        }

        class PrivateUser(override val nickname: String) : IUser
        class SubscribingUser(val email: String) : IUser {
            override val nickname: String
                get() = email.substringBefore('@')
        }

        class FacebookUser(val accountId: Int) : IUser {
            override val nickname = getFacebookName(accountId)
        }

        interface ISubscribingUser : IUser {
            val email: String
            override val nickname: String
                get() = email.substringBefore('@')
        }

        class RealUser(val name: String) {
            var address: String = "unspecified"
                set(value: String) {
                    log.info("""
                Address was changed for $name:
                "$field" -> "$value".""".trimIndent())
                    field = value
                    log.probe(5)
                }
        }

        class LenCounter {
            var counter: Int = 0
                private set(value: Int) {
                    log.probe(3)
                    field = value
                }

            constructor() {
                counter = 0
            }

            fun addWord(word: String) {
                counter += word.length
            }
        }
    }

    @Test
    fun testBackingField() {
        val user = RealUser("Alice")
        user.address = "Elsenheimerstrasse 47, 80687 Muenchen"
        log.inspect(RealUser::class.java)
    }

    @Test
    fun testHiddenSetter() {
        val lenCounter = LenCounter()
//        lenCounter.counter = 0
        lenCounter.addWord("Hi!")
        log.info(lenCounter.counter)
        log.inspect(LenCounter::class.java)
    }

    @Test
    fun test() {
        log.info(PrivateUser("test@kotlinlang.org").nickname)
        log.info(SubscribingUser("test@kotlinlang.org").nickname)
    }
}

class Ch04Tests_DataClass {
    companion object {
        val log: Logger = LoggerFactory.getLogger(Ch04Tests_DataClass::class.java)

        data class Client(val name: String, val postalCode: Int)
        data class Customer(var name: String, var postalCode: Int)
    }

    @Test
    fun checkDataClass() {
        log.inspect(Client::class.java)
        log.inspect(Customer::class.java)
    }

    @Test
    fun testEquality() {
        val bob = Client("Bob", 973293)
        Assert.assertFalse(bob === bob.copy())
        Assert.assertTrue(bob == bob.copy())
    }
}

class Ch04Tests_Object {
    object SomeObj {
        init {
            log.info("Inner Object")
        }

        val someVal: String = "SomeObj.someVal"
        var someVar: String = "SomeObj.someVar"
    }

    @Test
    fun testInnerObject() {
        log.inspect(SomeObj::class.java)
    }

    companion object {
        val log: Logger = LoggerFactory.getLogger(Ch04Tests_Object::class.java)

        object CaseInsensitiveCmp : Comparator<String> {
            //            constructor() {}
            init {
                log.probe()
            }

            override fun compare(s1: String?, s2: String?): Int = Comparator.naturalOrder<String>().compare(s1?.toLowerCase(), s2?.toLowerCase())
            override fun toString(): String {
                return super.toString()
            }
        }

        object SomeSingleton {
            var count: Int = 0

            init {
                log.info("${this} init")
            }
        }
    }

    @Test
    fun testObjectClass() {
        log.inspect(CaseInsensitiveCmp::class.java)
        log.info(CaseInsensitiveCmp)
    }

    @Test
    fun testObjectThreadSafety() {
        log.inspect(SomeSingleton::class.java)
        runBlocking {
            repeat(10000) {
                async {
                    //                    delay(1000)
                    SomeSingleton.count += 1 - it % 2 * 2
                }
            }
        }
        log.info("final ${SomeSingleton.count}")
        val exs = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors())
        repeat(10000) {
            exs.submit() { SomeSingleton.count += 1 - it % 2 * 2 }
        }

        log.info("final ${SomeSingleton.count}")
    }
}

class Ch04_Tests_By {
    companion object {
        val log: Logger = LoggerFactory.getLogger(Ch04_Tests_By::class.java)

        class CountingSet<T>(val innerSet: MutableSet<T> = HashSet<T>()) : MutableCollection<T> by innerSet {
            var objectsAdded = 0
            override fun add(element: T): Boolean {
                objectsAdded++
                return innerSet.add(element)
            }

            override fun addAll(c: Collection<T>): Boolean {
                objectsAdded += c.size
                return innerSet.addAll(c)
            }
        }
    }

    @Test
    fun testByDelegation() {
        log.inspect(CountingSet::class.java)
    }
}