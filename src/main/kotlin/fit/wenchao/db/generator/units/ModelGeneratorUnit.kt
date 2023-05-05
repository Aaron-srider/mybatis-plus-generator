package fit.wenchao.db.generator.units

import fit.wenchao.db.JavaClassName
import fit.wenchao.db.Table
import fit.wenchao.db.codeWriter.JavaCodeWriter
import fit.wenchao.db.codeWriter.KotlinCodeWriter
import fit.wenchao.db.codeWriter.javaWriter
import fit.wenchao.db.codeWriter.kotlinWriter
import fit.wenchao.db.constants.Lang
import fit.wenchao.db.fromUnderScore
import fit.wenchao.db.generator.GeneratorContext
import fit.wenchao.db.generator.GlobalContextKey
import fit.wenchao.db.generator.NewInfoRegistor
import java.util.*


class ModelGeneratorUnit(generatorContext: GeneratorContext) : GenericGeneratorUnit(generatorContext) {

    override fun doGenerate(partialMap: HashMap<String, Any>, newInfoRegistor: NewInfoRegistor) {

        // all tables to process
        var tables: MutableList<Table> = getTables()

        var modelPackage = getPackage(GlobalContextKey.MODEL_PACKAGE)

        // what language to generate
        var language = getLanguage()

        tables.forEach { table ->
            val modelClazzName = JavaClassName.fromLowerUnderScore(modelPackage, table.name, "PO")

            // if kotlin
            var code = if (language == Lang.KOTLIN) {
                kotlinWriter()
                    .packagel(modelPackage.dotSplitName)
                    .importl("com.baomidou.mybatisplus.annotation.TableName")
                    .importl("com.baomidou.mybatisplus.annotation.TableId")
                    .importl("com.baomidou.mybatisplus.annotation.IdType")
                    .importl("java.io.Serializable")
                    .atl("TableName(\"`" + table.name + "`\")")
                    .datal().classl(modelClazzName.name).primaryConstructor { jc: KotlinCodeWriter ->
                        val size = table.attrs.size
                        var count = 0
                        for (tableAttr in table) {
                            count++
                            if (tableAttr.isPri) {
                                //@TableId(value = "id", type = IdType.AUTO)
                                jc.atl("TableId(value=\"" + tableAttr.name + "\", type=IdType.AUTO)")
                            }
                            jc.write("var ")
                            val javaVarName = fromUnderScore(tableAttr.name)
                            jc.write(javaVarName.name + ": ")
                            if (tableAttr.type.equals("int", ignoreCase = true)) {
                                jc.write("Int ")
                                // codeBuilder.append("int ");
                            }
                            if (tableAttr.type.equals("bigint", ignoreCase = true)) {
                                jc.write("Long ")
                                // codeBuilder.append("int ");
                            }
                            if (tableAttr.type.lowercase(Locale.getDefault()).contains("text")
                                || tableAttr.type.lowercase(Locale.getDefault()).contains("json")
                                || tableAttr.type.equals("varchar", ignoreCase = true)
                            ) {
                                jc.write("String ")
                            }
                            if (count == size) {
                                jc.write("?")
                            } else {
                                jc.write("?,")
                            }
                            jc.write("\n")
                        }
                        null
                    }.extendsFirstl("Serializable")
                    .toString()
            } else {
                javaWriter()
                    .packagel(modelPackage.dotSplitName)
                    .importl("com.baomidou.mybatisplus.annotation.TableName")
                    .importl("com.baomidou.mybatisplus.annotation.TableId")
                    .importl("com.baomidou.mybatisplus.annotation.IdType")
                    .importl("java.io.Serializable")
                    .importl("lombok.Data")
                    .importl("lombok.AllArgsConstructor")
                    .importl("lombok.Builder")
                    .importl("lombok.Data")
                    .importl("lombok.NoArgsConstructor")
                    .importl("lombok.experimental.Accessors")
                    .atl("Data")
                    .atl("AllArgsConstructor")
                    .atl("NoArgsConstructor")
                    .atl("Builder")
                    .atl("Accessors(chain = true)")
                    .atl("TableName(\"`" + table.name + "`\")")
                    .publicl().classl(modelClazzName.name).implementsl("Serializable")
                    .blockl { jc: JavaCodeWriter ->
                        for (tableAttr in table) {
                            if (tableAttr.isPri) {
                                //@TableId(value = "id", type = IdType.AUTO)
                                jc.atl("TableId(value=\"" + tableAttr.name + "\", type=IdType.AUTO)")
                            }
                            if (tableAttr.type.equals("varchar", ignoreCase = true)) {
                                jc.write("String ")
                                // codeBuilder.append("String ");
                            }
                            if (tableAttr.type.equals("int", ignoreCase = true)) {
                                jc.write("int ")
                                // codeBuilder.append("int ");
                            }
                            val javaVarName = fromUnderScore(tableAttr.name)
                            jc.write(javaVarName.name + ";\n")
                            // codeBuilder.append(javaVarName.name + ";\n");
                        }
                    }.toString()
            }

            val modelSrcFile = writeFile(modelClazzName, language, code)
            newInfoRegistor.addInfo("${table.name}_${GlobalContextKey.ModelSource}", modelSrcFile)
        }
    }


}
