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

class ServiceImplGeneratorUnit(generatorContext: GeneratorContext)  : GenericGeneratorUnit(generatorContext) {


    override fun doGenerate(partialMap: HashMap<String, Any>, newInfoRegistor: NewInfoRegistor) {

        // all tables to process
        var tables: MutableList<Table> = getTables()

        var serviceImplPackage: JavaPackageImpl =  getPackage(GlobalContextKey.SERVICEIMPL_PACKAGE)

        // what language to generate
        var language: Lang = getLanguage()

        tables.forEach { table ->

            val serviceImplClazzName = JavaClassNameImpl.fromLowerUnderScore(serviceImplPackage, table.name, "ServiceImpl")

            var serviceSourceFile =  getInfoFromContext("${table.name}_${GlobalContextKey.ServiceSource}") as JavaSourceFile
            val serviceClazzName = serviceSourceFile.javaClassName

            var code: String = if (language == Lang.KOTLIN) {
                val javaCode = kotlinWriter()
                    .packagel(serviceImplPackage.dotSplitName())
                    .importl(serviceClazzName.getFullClazzName())
                    .importl("org.springframework.stereotype.Service")
                    .atl("Service")
                    .classl(serviceImplClazzName.getClazzName()).extendsFirstl(serviceClazzName.getClazzName())
                    .blockl()
                javaCode.toString()
            } else {
                val javaCode = javaWriter()
                    .packagel(serviceImplPackage.dotSplitName())
                    .importl(serviceClazzName.getFullClazzName())
                    .importl("lombok.extern.slf4j.Slf4j")
                    .importl("org.springframework.stereotype.Service")
                    .atl("Service")
                    .atl("Slf4j")
                    .publicl().classl(serviceImplClazzName.getClazzName()).implementsl(serviceClazzName.getClazzName())
                    .blockl()
                javaCode.toString()
            }
            val srcFile = writeFile(serviceImplClazzName, language, code)
            newInfoRegistor.addInfo("${table.name}_${GlobalContextKey.ServiceImplSource}", srcFile)
        }
    }
}