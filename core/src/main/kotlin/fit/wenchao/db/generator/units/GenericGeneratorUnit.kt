package fit.wenchao.db.generator.units;

import fit.wenchao.db.Table
import fit.wenchao.db.constants.Lang
import fit.wenchao.db.generator.GeneratorContext
import fit.wenchao.db.generator.GeneratorUnit
import fit.wenchao.db.generator.GlobalContextKey
import fit.wenchao.db.sourceFile.JavaClassName
import fit.wenchao.db.sourceFile.JavaPackageImpl
import fit.wenchao.db.sourceFile.JavaSourceFile
import mu.KotlinLogging

abstract class GenericGeneratorUnit(generatorContext: GeneratorContext) : GeneratorUnit(generatorContext) {
    private val log = KotlinLogging.logger {}

    protected fun getTables(): MutableList<Table> {
        return getInfoFromContext(GlobalContextKey.TABLES) as MutableList<Table>
    }

    private fun getInfoFromContext(key: GlobalContextKey): Any {
        return getInfoFromContext(key.name)
    }

    protected fun getInfoFromContext(key: String): Any {
        val contextInfo = generatorContext.get(key)
            ?: throw RuntimeException("${key} not found in global context")
        return contextInfo
    }

    fun getPackage(key: GlobalContextKey): JavaPackageImpl {
        val specificPackageString = getInfoFromContext(key) as String

        val basePackageString = getInfoFromContext(GlobalContextKey.BASE_PACKAGE) as String
        val srcPath = getInfoFromContext(GlobalContextKey.SRC_PATH) as String

        // we need to know where to put the generated src files
        return JavaPackageImpl.from("${basePackageString}/${specificPackageString}",srcPath)
    }

    fun getLanguage(): Lang {
        return generatorContext.get(GlobalContextKey.LANGUAGE.name) as Lang? ?: Lang.KOTLIN
    }

    open fun writeFile(clazzName: JavaClassName, language: Lang, code: String): JavaSourceFile {
        val srcFile = JavaSourceFile(clazzName, language, code)
        if (srcFile.notExists()) {
            srcFile.put2Package()
            log.info { "write file ${srcFile.srcFileName} to ${clazzName.getPackagePart().splashSplitName()}" }
        }
        return srcFile
    }

}
