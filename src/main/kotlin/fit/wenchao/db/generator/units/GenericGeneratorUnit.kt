package fit.wenchao.db.generator.units;

import fit.wenchao.db.JavaClassName
import fit.wenchao.db.JavaPackage
import fit.wenchao.db.JavaSourceFile
import fit.wenchao.db.Table
import fit.wenchao.db.constants.Lang
import fit.wenchao.db.generator.GeneratorContext
import fit.wenchao.db.generator.GeneratorUnit
import fit.wenchao.db.generator.GlobalContextKey
import mu.KotlinLogging

abstract class GenericGeneratorUnit(generatorContext: GeneratorContext) : GeneratorUnit(generatorContext) {
    private val log = KotlinLogging.logger {}

    fun getTables(): MutableList<Table> {
        return getInfo(GlobalContextKey.TABLES) as MutableList<Table>
    }

    fun getInfo(key: GlobalContextKey): Any {
        return getInfo(key.name)
    }

    fun getInfo(key: String): Any {
        val modelPackageString = generatorContext.get(key)
            ?: throw RuntimeException("${key} not found in global context")
        return modelPackageString
    }

    fun getPackage(key: GlobalContextKey): JavaPackage {
        val packageString = getInfo(key) as String

        val baseDir = getInfo(GlobalContextKey.BASE_DIR) as String

        // we need to know where to put the generated src files
        return JavaPackage.from("${baseDir}/${packageString}")
    }

    fun getLanguage(): Lang {
        return generatorContext.get(GlobalContextKey.LANGUAGE.name) as Lang? ?: Lang.KOTLIN
    }

    open fun writeFile(clazzName: JavaClassName, language: Lang, code: String): JavaSourceFile {
        val srcFile = JavaSourceFile(clazzName, language, code)
        if (srcFile.notExists()) {
            srcFile.put2Package()
            log.info { "write file ${srcFile.srcFileName} to ${clazzName.javaPackage.relativePackageString}" }
        }
        return srcFile
    }

}
