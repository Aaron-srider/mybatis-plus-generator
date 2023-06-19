package fit.wenchao


class TestDriven {

}

fun main() {
    var generator = fit.wenchao.db.generator.Generator()
    generator.start(TestDriven::class.java)
}