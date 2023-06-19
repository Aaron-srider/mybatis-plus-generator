package fit.wenchao.db.generator.units

import fit.wenchao.db.Table
import fit.wenchao.db.codeWriter.javaWriter
import fit.wenchao.db.codeWriter.kotlinWriter
import fit.wenchao.db.constants.Lang
import fit.wenchao.db.generator.GeneratorContext
import fit.wenchao.db.generator.GlobalContextKey
import fit.wenchao.db.generator.NewInfoRegistor
import fit.wenchao.db.sourceFile.JavaClassNameImpl
import fit.wenchao.db.sourceFile.JavaPackageImpl
import fit.wenchao.db.sourceFile.JavaSourceFile

class RepoGeneratorUnit(generatorContext: GeneratorContext)  : GenericGeneratorUnit(generatorContext) {

    override fun doGenerate(partialMap: HashMap<String, Any>, newInfoRegistor: NewInfoRegistor) {

        // all tables to process
        var tables: MutableList<Table> = getTables()

        var repoPackage: JavaPackageImpl =  getPackage(GlobalContextKey.REPO_PACKAGE)

        // what language to generate
        var language: Lang = getLanguage()

        tables.forEach { table ->

            var modelSourceFile =  getInfoFromContext("${table.name}_${GlobalContextKey.ModelSource}") as JavaSourceFile
            val modelClazzName = modelSourceFile.javaClassName ?: throw RuntimeException("model information of table ${table.name} can not be found in global context")
            val repoClazzName = JavaClassNameImpl.fromLowerUnderScore(repoPackage, table.name, "Dao")

            var code: String = if (language == Lang.KOTLIN) {
                val javaCode = kotlinWriter()
                    .packagel(repoPackage.dotSplitName())
                    .importl(modelClazzName.getFullClazzName())
                    .importl("com.baomidou.mybatisplus.extension.service.IService")
                    .interfacel(repoClazzName.getClazzName())
                    .extendsFirstl("IService<" + modelClazzName.getClazzName() + ">")
                    .blockl()
                javaCode.toString()
            } else {
                val javaCode = javaWriter()
                    .packagel(repoPackage.dotSplitName())
                    .importl(modelClazzName.getFullClazzName())
                    .importl("com.baomidou.mybatisplus.extension.service.IService")
                    .publicl().interfacel(repoClazzName.getClazzName())
                    .extendsl("IService<" + modelClazzName.getClazzName() + ">")
                    .blockl()
                javaCode.toString()
            }


            val repoSrcFile = writeFile(repoClazzName, language, code)
            newInfoRegistor.addInfo("${table.name}_${GlobalContextKey.RepoSource}", repoSrcFile)
        }
    }
}