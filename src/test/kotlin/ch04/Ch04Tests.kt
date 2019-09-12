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
    fun testDiamondInheirtance() {
        val button = Button()
        button.showOff()
        button.setFocus(true)
        button.click()
    }
}