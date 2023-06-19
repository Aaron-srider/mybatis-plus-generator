package fit.wenchao.db.codeWriter

open class CodeWriter {

}

fun javaWriter(): JavaCodeWriter {
    return JavaCodeWriter()
}

fun kotlinWriter(): KotlinCodeWriter {
    return KotlinCodeWriter()
}

