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

class RepoGeneratorUnit(generatorContext: GeneratorContext)  : GenericGeneratorUnit(generatorContext) {

    override fun doGenerate(partialMap: HashMap<String, Any>, newInfoRegistor: NewInfoRegistor) {

        // all tables to process
        var tables: MutableList<Table> = getTables()

        var repoPackage: JavaPackage =  getPackage(GlobalContextKey.REPO_PACKAGE)

        // what language to generate
        var language: Lang = getLanguage()

        tables.forEach { table ->

            var modelSourceFile =  getInfo("${table.name}_${GlobalContextKey.ModelSource}") as JavaSourceFile
            val modelClazzName = modelSourceFile.javaClassName ?: throw RuntimeException("model information of table ${table.name} can not be found in global context")
            val repoClazzName = JavaClassName.fromLowerUnderScore(repoPackage, table.name, "Dao")

            var code: String = if (language == Lang.KOTLIN) {
                val javaCode = kotlinWriter()
                    .packagel(repoPackage.dotSplitName)
                    .importl(modelClazzName.getFullName())
                    .importl("com.baomidou.mybatisplus.extension.service.IService")
                    .interfacel(repoClazzName.name)
                    .extendsFirstl("IService<" + modelClazzName.name + ">")
                    .blockl()
                javaCode.toString()
            } else {
                val javaCode = javaWriter()
                    .packagel(repoPackage.dotSplitName)
                    .importl(modelClazzName.getFullName())
                    .importl("com.baomidou.mybatisplus.extension.service.IService")
                    .publicl().interfacel(repoClazzName.name)
                    .extendsl("IService<" + modelClazzName.name + ">")
                    .blockl()
                javaCode.toString()
            }


            val repoSrcFile = writeFile(repoClazzName, language, code)
            newInfoRegistor.addInfo("${table.name}_${GlobalContextKey.RepoSource}", repoSrcFile)
        }
    }
}