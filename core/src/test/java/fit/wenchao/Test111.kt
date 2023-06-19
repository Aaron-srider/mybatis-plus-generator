package fit.wenchao

import org.junit.jupiter.api.Test


class Test111 {
    @Test
    fun test() {
        val underScore = "hello_world"
        underScore.replaceFirstChar { it.uppercase() }.let { println(it) }
    }
}