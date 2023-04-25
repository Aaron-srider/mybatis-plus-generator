package fit.wenchao.db

import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.util.*
import java.util.stream.Collectors

class JavaPackage {
   var absolutePackageString: String? = null
   var relativePackageString: String? = null


    override fun toString(): String {
        return "JavaPackage{" +
                "packageString='" + absolutePackageString + '\'' +
                '}'
    }

    fun listSourceFileNames(): List<String?> {
        val file = File(absolutePackageString)
        val list = file.list()
        var sourceFileNames: List<String?> = ArrayList()
        if (list != null) {
            sourceFileNames = Arrays.stream(list).collect(Collectors.toList())
        }
        return sourceFileNames
    }

    val file: File
        get() = File(absolutePackageString)

    val dotSplitName: String
        get() = relativePackageString!!.replace("/", ".")


}

fun from(relativePackageString: String): JavaPackage {
    val javaPackage = JavaPackage()
    val file = File("")
    val projectPath = file.absolutePath
    javaPackage.relativePackageString = relativePackageString
    javaPackage.absolutePackageString = "$projectPath/src/main/java/$relativePackageString"
    if (!File(javaPackage.absolutePackageString).exists()) {
        try {
            Files.createDirectories(File(javaPackage.absolutePackageString).toPath())
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }
    return javaPackage
}
