package ch04

import org.junit.Test
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import probe

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
}