package ch05

import info
import inspect
import org.junit.Test
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import probe
import java.lang.StringBuilder

class Ch05Tests {
    companion object {
        val log: Logger = LoggerFactory.getLogger(Ch05Tests::class.java)

        data class Person(val name: String, val age: Int)
        data class Book(val title: String, val authors: List<String>)
    }

    @Test
    fun testLambda1() {
        val people = listOf(Person("Alice", 29), Person("Bob", 31))
        fun findTheOldest(people: List<Person>): Person? {
            var maxAge = 0
            var theOldest: Person? = null
            for (person in people) {
                if (person.age > maxAge) {
                    maxAge = person.age
                    theOldest = person
                }
            }
            return theOldest
        }
        log.info(findTheOldest(people))
        log.info(people.maxBy { it.age })
    }

    @Test
    fun testSyntaxForLambdaExpression() {
        val sum = { x: Int, y: Int ->
            log.probe(5)
            x + y
        }
        log.info(sum(1, 2))
        log.inspect(sum.javaClass)

        fun sum1(x: Int, y: Int): Int {
            log.probe(5)
            log.inspect(this.javaClass)
            return x + y
        }
        log.info(sum1(2, 3))

        log.info({ x: Int, y: Int -> x * y }(5, 6))
    }

    @Test
    fun testPerformance() {
        repeat(100) {
            val sum = { x: Int, y: Int -> x + y }
            var startedAt = System.currentTimeMillis()
            for (i in 1 until 2000000) {
                sum(i, 1)
            }
            log.info("lambda time: {}", System.currentTimeMillis() - startedAt)
            fun sum1(x: Int, y: Int) = x + y
            startedAt = System.currentTimeMillis()
            for (i in 1 until 2000000) {
                sum(i, 1)
            }
            log.info("fun time: {}", System.currentTimeMillis() - startedAt)
        }
    }

    @Test
    fun testRunBlock() {
        val re = "this is a String".run {
            log.probe(5)
            log.info(this)
        }
        log.info(re)
    }

    @Test
    fun testLetBlock() {
        val re = "this is a String".let {
            log.probe(5)
            log.info(this)
        }
        log.info(re)
    }

    @Test
    fun testWithBlock() {
        val re = with("this is a String") {
            log.probe(5)
            log.info(this)
        }
        log.info(re)
    }

    @Test
    fun testApplyBlock() {
        val re = "this is a String".apply {
            log.probe(5)
            log.info(this)
        }
        log.info(re)
    }

    @Test
    fun testAlsoBlock() {
        val re = "this is a String".also {
            log.probe(5)
            log.info(this)
        }
        log.info(re)
    }

    @Test
    fun testLambda2() {
        val people = listOf(
                Person("Alice", 29),
                Person("Bob", 31)
        )
        val names = people.joinToString(separator = " ",
                transform = { p: Person -> p.name })
        log.info(names)
    }

    @Test
    fun testLambda3() {
        fun printMsgWithPrefix(msgs: Collection<String>, prefix: String) {
            msgs.forEach { log.info("$prefix $it") }
        }
        printMsgWithPrefix(listOf("403 Forbidden", "404 Not Found"), "Error:")
    }

    @Test
    fun testLambda4() {
        fun printProblemCounts(resps: Collection<String>) {
            var clientErrors = 0
            var serverErrors = 0
            resps.forEach {
                if (it.startsWith("4")) clientErrors++
                else if (it.startsWith("5")) serverErrors++
            }
            log.info("$clientErrors client errors, $serverErrors server errors")
        }
        printProblemCounts(listOf("200 OK", "418 I'am a teapot", "500 Internal Server Error"))
    }

    @Test
    fun testMemberRef() {
        val testLambda4 = this::testLambda4
        testLambda4.invoke()
        run(testLambda4)
        log.inspect(testLambda4::class.java)

        data class Point(val x: Int, val y: Int)

        val pointConstructor = ::Point
        log.inspect(pointConstructor::class.java)
        log.info(pointConstructor)
        log.info(pointConstructor(4, 3))
    }

    @Test
    fun testLambda5() {
        log.info((1 until 5).toList().filter { it % 2 == 0 })
        var people = listOf(
                Person("Alice", 27),
                Person("Bob", 31),
                Person("Carol", 31))
        log.info(people.filter { it.age > 30 })
        log.info((1 until 5).toList().map { it * it })
        log.info(people.map { it.name })
        log.info(mapOf(0 to "zero", 1 to "one").mapValues { it.value.toUpperCase() })

        log.info(people.all { p: Person -> p.age <= 27 })
        log.info(people.any { p: Person -> p.age <= 27 })
        log.info(people.find { p: Person -> p.age <= 27 })

        log.info(people.groupBy { it.age })
        log.info(listOf("a", "ab", "b").groupBy(String::first))

        log.info(listOf("abc", "def").flatMap { it.toList() })

        log.info(listOf(
                Book("Thursday Next", listOf("Jasper Fforde")),
                Book("Mort", listOf("Terry Partchett")),
                Book("Good Omens", listOf("Terry Partchett", "Neil Gaiman"))
        ).flatMap { it.authors }.toSet())
    }

    @Test
    fun testSequence() {
        log.info((1 until 10).asSequence()
                .map { log.info("map($it)"); it * it }
                .filter { log.info("filter($it)"); it % 2 == 0 }.toList())
        log.info((1 until 10).asIterable()
                .map { log.info("map($it)"); it * it }
                .filter { log.info("filter($it)"); it % 2 == 0 }.toList())

        val naturalNumbers = generateSequence(0) { it + 1 }
        val numbersTo100 = naturalNumbers.takeWhile { it <= 100 }
        log.info(naturalNumbers.sum())
    }

    @Test
    fun testSAMConstructor() {
        fun createAllDoneRunnable(): Runnable {
            return Runnable { log.info("All done!") }
        }
        createAllDoneRunnable().run()
    }

    @Test
    fun testAlphabet() {
        run {
            fun alphabet(): String {
                val result: StringBuilder = StringBuilder()
                for (letter in 'A'..'Z') {
                    result.append(letter)
                }
                result.append("\nNow I know the alphabet!")
                return result.toString()
            }
            log.info(alphabet())
        }
        run {
            fun alphabet(): String {
                val result: StringBuilder = StringBuilder()
                return with(result) {
                    for (letter in 'A'..'Z') {
                        append(letter)
                    }
                    append("\nNow I know the alphabet!")
                    toString()
                }
            }
            log.info(alphabet())
        }
        run {
            fun alphabet(): String = with(StringBuilder()) {
                for (letter in 'A'..'Z') {
                    append(letter)
                }
                append("\nNow I know the alphabet!")
                toString()
            }
            log.info(alphabet())
        }
        run {
            fun alphabet(): String = StringBuilder().apply {
                for (letter in 'A'..'Z') {
                    append(letter)
                }
                append("\nNow I know the alphabet!")
            }.toString()
            log.info(alphabet())
        }
        run {
            fun alphabet(): String = buildString {
                for (letter in 'A'..'Z') {
                    append(letter)
                }
                append("\nNow I know the alphabet!")
            }
            log.info(alphabet())
        }
    }
}