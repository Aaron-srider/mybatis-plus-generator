package fit.wenchao.db.codeWriter

import java.util.function.Consumer


class KotlinCodeWriter {

    private var codeBuilder: StringBuilder = StringBuilder()

    fun writing(): KotlinCodeWriter {
        return KotlinCodeWriter()
    }

    fun empty(): KotlinCodeWriter {
        return KotlinCodeWriter()
    }

    fun packagel(packName: String): KotlinCodeWriter {
        codeBuilder.append("package ").append(packName).append("\n")
        return this
    }

    fun importl(classname: String): KotlinCodeWriter {
        codeBuilder.append("import ").append(classname).append("\n")
        return this
    }

    fun atl(annotationName: String): KotlinCodeWriter {
        codeBuilder.append("@").append(annotationName).append("\n")
        return this
    }

    fun publicl(): KotlinCodeWriter {
        codeBuilder.append("public ")
        return this
    }

    fun classl(classname: String): KotlinCodeWriter {
        codeBuilder.append("class ").append("$classname ")
        return this
    }

    fun blockl(blockCode: KotlinCodeWriter): KotlinCodeWriter {
        codeBuilder.append("{\n")
        codeBuilder.append(blockCode.toString())
        codeBuilder.append("}\n")
        return this
    }

    fun blockl(function: Consumer<KotlinCodeWriter>): KotlinCodeWriter {
        codeBuilder.append("{\n")
        function.accept(this)
        codeBuilder.append("}\n")
        return this
    }


    fun blockl(): KotlinCodeWriter {
        codeBuilder.append("{\n")
        codeBuilder.append("}\n")
        return this
    }


    fun interfacel(interfaceName: String): KotlinCodeWriter {
        codeBuilder.append("interface ").append("$interfaceName ")
        return this
    }

    fun extendsFirstl(classname: String): KotlinCodeWriter {
        codeBuilder.append(": ").append("$classname ")
        return this
    }


    fun extendsThenl(classname: String): KotlinCodeWriter {
        codeBuilder.append(", ").append("$classname ")
        return this
    }

    fun write(str: String): KotlinCodeWriter {
        codeBuilder.append(str)
        return this
    }

    override fun toString(): String {
        return codeBuilder.toString()
    }

    fun writeln(str: String): KotlinCodeWriter {
        codeBuilder.append(str).append("\n")
        return this
    }

    fun datal(): KotlinCodeWriter{
        codeBuilder.append("data ")
        return this
    }

    fun primaryConstructor(getPrimaryConstructor: (KotlinCodeWriter) -> Unit): KotlinCodeWriter {
        codeBuilder.append("(\n")
        getPrimaryConstructor(this)
        codeBuilder.append(")\n")
        return this
    }
}

fun main() {
    var writer = kotlinWriter()

    val toString = writer.packagel("test.package")
        .importl("java.lang.String")
        .datal().classl("KotlinClass")
        .primaryConstructor{ kw ->

            kw.writeln("var name: String?")

        }
        .extendsFirstl("Super1").extendsThenl("Super2")
        .blockl { kw ->
            kw.writeln("var name1: String = \"\"")
        }.toString()



    println(toString)
}