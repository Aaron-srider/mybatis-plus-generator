package fit.wenchao.db.codeWriter

import java.util.function.Consumer

class JavaCodeWriter {
    var codeBuilder: StringBuilder = StringBuilder()

    fun packagel(packName: String?): JavaCodeWriter {
        codeBuilder.append("package ").append(packName).append(";\n")
        return this
    }

    fun importl(classname: String?): JavaCodeWriter {
        codeBuilder.append("import ").append(classname).append(";\n")
        return this
    }

    fun atl(annotationName: String?): JavaCodeWriter {
        codeBuilder.append("@").append(annotationName).append("\n")
        return this
    }

    fun publicl(): JavaCodeWriter {
        codeBuilder.append("public ")
        return this
    }

    fun classl(classname: String): JavaCodeWriter {
        codeBuilder.append("class ").append("$classname ")
        return this
    }

    fun implementsl(implClassName: String): JavaCodeWriter {
        codeBuilder.append("implements ").append("$implClassName ")
        return this
    }

    fun blockl(blockCode: JavaCodeWriter): JavaCodeWriter {
        codeBuilder.append("{\n")
        codeBuilder.append(blockCode.toString())
        codeBuilder.append("}\n")
        return this
    }

    fun blockl(function: Consumer<JavaCodeWriter>): JavaCodeWriter {
        codeBuilder.append("{\n")
        function.accept(this)
        codeBuilder.append("}\n")
        return this
    }

    fun blockl(): JavaCodeWriter {
        codeBuilder.append("{\n")
        codeBuilder.append("}\n")
        return this
    }

    override fun toString(): String {
        return codeBuilder.toString()
    }

    fun interfacel(interfaceName: String): JavaCodeWriter {
        codeBuilder.append("interface ").append("$interfaceName ")
        return this
    }

    fun extendsl(classname: String): JavaCodeWriter {
        codeBuilder.append("extends ").append("$classname ")
        return this
    }

    fun write(str: String?): JavaCodeWriter {
        codeBuilder.append(str)
        return this
    }

    companion object {
        fun writing(): JavaCodeWriter {
            return JavaCodeWriter()
        }

        fun empty(): JavaCodeWriter {
            return JavaCodeWriter()
        }
    }
}

