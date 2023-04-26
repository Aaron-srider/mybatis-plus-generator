package fit.wenchao.db

import com.fasterxml.jackson.databind.RuntimeJsonMappingException
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.util.*
import java.util.stream.Collectors

class JavaPackage {

    companion object {

        lateinit var projectToSrc: String

        init {
            // read projectToSrc from config.properties
            val props = Properties()
            val inputStream = JavaPackage::class.java.classLoader.getResourceAsStream("config.properties")
            inputStream?.let {
                props.load(inputStream)
            } ?: run {
                throw RuntimeException("config.properties not found")
            }
            val projectToSrc: String? = props.getProperty("projectToSrc")
            projectToSrc?.let {
                this.projectToSrc = projectToSrc
            } ?: run {
                throw RuntimeException("projectToSrc not found in config.properties")
            }
        }

        fun from(relativePackageString: String): JavaPackage {
            val javaPackage = JavaPackage()
            // get project root file
            val currentProjectRootFile = File("")
            val projectPath = currentProjectRootFile.absolutePath
            javaPackage.relativePackageString = relativePackageString
            javaPackage.absolutePackageString = "$projectPath/${projectToSrc}/$relativePackageString"
            if (!File(javaPackage.absolutePackageString).exists()) {
                try {
                    Files.createDirectories(File(javaPackage.absolutePackageString).toPath())
                } catch (e: IOException) {
                    throw RuntimeException(e)
                }
            }
            return javaPackage
        }
    }


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

