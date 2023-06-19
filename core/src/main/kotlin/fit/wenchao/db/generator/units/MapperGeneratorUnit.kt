package fit.wenchao.db.generator.units

import fit.wenchao.db.Table
import fit.wenchao.db.codeWriter.javaWriter
import fit.wenchao.db.codeWriter.kotlinWriter
import fit.wenchao.db.constants.Lang
import fit.wenchao.db.generator.GeneratorContext
import fit.wenchao.db.generator.GlobalContextKey
import fit.wenchao.db.generator.NewInfoRegistor
import fit.wenchao.db.sourceFile.JavaClassNameImpl
import fit.wenchao.db.sourceFile.JavaSourceFile

class MapperGeneratorUnit(generatorContext: GeneratorContext) : GenericGeneratorUnit(generatorContext) {

    override fun doGenerate(partialMap: HashMap<String, Any>, newInfoRegistor: NewInfoRegistor) {

        // all tables to process
        var tables: MutableList<Table> = getTables()

        var mapperPackage = getPackage(GlobalContextKey.MAPPER_PACKAGE)

        // what language to generate
        var language = getLanguage()

        tables.forEach { table ->

            // mapper needs model class information
            val modelSourceFile = getInfoFromContext("${table.name}_${GlobalContextKey.ModelSource}") as JavaSourceFile
            val modelClazzName = modelSourceFile.javaClassName
            val mapperClassName = JavaClassNameImpl.fromLowerUnderScore(mapperPackage, table.name, "Mapper")

            var code: String = if (language == Lang.KOTLIN) {
                val javaCode = kotlinWriter()
                    .packagel(mapperPackage.dotSplitName())
                    .importl(modelClazzName!!.getFullClazzName())
                    .importl("org.apache.ibatis.annotations.Mapper")
                    .importl("com.baomidou.mybatisplus.core.mapper.BaseMapper")
                    .atl("Mapper")
                    .interfacel(mapperClassName.getClazzName()).extendsFirstl("BaseMapper<" + modelClazzName.getClazzName() + ">")
                    .blockl()
                javaCode.toString()
            } else {
                val javaCode = javaWriter()
                    .packagel(mapperPackage.dotSplitName())
                    .importl(modelClazzName!!.getFullClazzName())
                    .importl("org.apache.ibatis.annotations.Mapper")
                    .importl("com.baomidou.mybatisplus.core.mapper.BaseMapper")
                    .atl("Mapper")
                    .publicl().interfacel(mapperClassName.getClazzName()).extendsl("BaseMapper<" + modelClazzName.getClazzName() + ">")
                    .blockl()
                javaCode.toString()
            }


            val mapperSrcFile = writeFile(mapperClassName, language, code)

            newInfoRegistor.addInfo("${table.name}_${GlobalContextKey.MapperSource}", mapperSrcFile)

        }
    }

}