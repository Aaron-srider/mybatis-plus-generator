package fit.wenchao.db.generator.units

import fit.wenchao.db.JavaClassName
import fit.wenchao.db.JavaPackage
import fit.wenchao.db.JavaSourceFile
import fit.wenchao.db.Table
import fit.wenchao.db.codeWriter.javaWriter
import fit.wenchao.db.codeWriter.kotlinWriter
import fit.wenchao.db.constants.Lang
import fit.wenchao.db.generator.GeneratorContext
import fit.wenchao.db.generator.GlobalContextKey
import fit.wenchao.db.generator.NewInfoRegistor

class ServiceGeneratorUnit(generatorContext: GeneratorContext)  : GenericGeneratorUnit(generatorContext) {


    override fun doGenerate(partialMap: HashMap<String, Any>, newInfoRegistor: NewInfoRegistor) {

        // all tables to process
        var tables: MutableList<Table> = getTables()

        var servicePackage: JavaPackage =  getPackage(GlobalContextKey.SERVICE_PACKAGE)

        // what language to generate
        var language: Lang = getLanguage()

        tables.forEach { table ->

            val serviceClazzName = JavaClassName.fromLowerUnderScore(servicePackage, table.name, "Service")

            var modelSourceFile =  getInfo("${table.name}_${GlobalContextKey.ModelSource}") as JavaSourceFile
            val modelClazzName = modelSourceFile.javaClassName

            var mapperSourceFile =  getInfo("${table.name}_${GlobalContextKey.MapperSource}") as JavaSourceFile
            val mapperClazzName = mapperSourceFile.javaClassName

            var repoSourceFile =  getInfo("${table.name}_${GlobalContextKey.RepoSource}") as JavaSourceFile
            val repoClazzName = repoSourceFile.javaClassName

            var code: String = if (language == Lang.KOTLIN) {
                val javaCode = kotlinWriter()
                    .packagel(servicePackage.dotSplitName)
                    .interfacel(serviceClazzName.name)
                    .blockl()
                javaCode.toString()
            } else {
                val javaCode = javaWriter()
                    .packagel(servicePackage.dotSplitName)
                    .publicl().interfacel(serviceClazzName.name)
                    .blockl()
                javaCode.toString()
            }

            val srcFile = writeFile(serviceClazzName, language, code)
            newInfoRegistor.addInfo("${table.name}_${GlobalContextKey.ServiceSource}", srcFile)
        }
    }
}