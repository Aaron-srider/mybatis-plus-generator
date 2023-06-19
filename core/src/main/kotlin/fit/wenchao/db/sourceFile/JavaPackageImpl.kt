package fit.wenchao.db.sourceFile

import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.util.*
import java.util.stream.Collectors


interface JavaPackage {

    fun toFile(): File
    fun dotSplitName(): String

    fun splashSplitName(): String

    fun absolutePath(): String
    fun listSourceFileNames(): List<String?>
}

class JavaPackageImpl : JavaPackage {

    var absolutePackagePathString: String? = null
    var packagePathString: String? = null

    companion object {

        fun from(packagePathString: String, srcPath: String): JavaPackageImpl {
            val javaPackage = JavaPackageImpl()

            // get project root file
            val currentProjectRootFile = File("")
            val projectPath = currentProjectRootFile.absolutePath

            // java package path
            javaPackage.packagePathString = packagePathString

            // absolute java package path
            javaPackage.absolutePackagePathString = "$projectPath/${srcPath}/$packagePathString"

            // create the java package folder
            if (!File(javaPackage.absolutePackagePathString).exists()) {
                try {
                    Files.createDirectories(File(javaPackage.absolutePackagePathString).toPath())
                } catch (e: IOException) {
                    throw RuntimeException(e)
                }
            }
            return javaPackage
        }
    }

    override fun toFile(): File {
        return file
    }

    override fun dotSplitName(): String {
        return dotSplitName
    }

    override fun splashSplitName(): String {
        return packagePathString!!
    }

    override fun absolutePath(): String {
        return absolutePackagePathString!!
    }

    override fun toString(): String {
        return "JavaPackage{" +
                "packageString='" + absolutePackagePathString + '\'' +
                '}'
    }

    override fun listSourceFileNames(): List<String?> {
        val file = File(absolutePackagePathString)
        val list = file.list()
        var sourceFileNames: List<String?> = ArrayList()
        if (list != null) {
            sourceFileNames = Arrays.stream(list).collect(Collectors.toList())
        }
        return sourceFileNames
    }


    private val file: File
        get() = File(absolutePackagePathString)

    private val dotSplitName: String
        get() = packagePathString!!.replace("/", ".")


}

