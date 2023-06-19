package fit.wenchao.db.sourceFile

import fit.wenchao.db.Table
import fit.wenchao.db.codeWriter.JavaCodeWriter
import fit.wenchao.db.codeWriter.KotlinCodeWriter
import fit.wenchao.db.codeWriter.javaWriter
import fit.wenchao.db.codeWriter.kotlinWriter
import fit.wenchao.db.constants.Lang
import fit.wenchao.db.logger.Log
import fit.wenchao.db.sourceFile.JavaClassNameImpl.Companion.fromLowerUnderScore
import java.io.ByteArrayInputStream
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*

class JavaSourceFile: Log {

    var javaPackage: JavaPackage
    var javaClassName: JavaClassName
    var srcFileName: String
    var srcCode: String
    var packageName: String

    constructor(clazzName: JavaClassName, language: Lang, srcCode:String)  {
        this.srcFileName = clazzName.toSrcFileName(language)
        this.javaClassName = clazzName
        this.javaPackage = clazzName.getPackagePart()
        this.packageName = clazzName.getPackagePart().dotSplitName()
        this.srcCode = srcCode
    }

    fun put2Package(javaPackage: JavaPackageImpl) {
        val javaPackageFile = javaPackage.toFile()
        val javaSourceFilePath = javaPackageFile.toPath().resolve(Paths.get(srcFileName))

        try {
            Files.newOutputStream(javaSourceFilePath).use { out ->
                ByteArrayInputStream(srcCode.toByteArray(StandardCharsets.UTF_8)).use { `in` ->
                    val buffer = ByteArray(1024 * 1024)
                    var len: Int
                    while (`in`.read(buffer).also { len = it } != -1) {
                        out.write(buffer, 0, len)
                    }
                    out.flush()
                }
            }
        } catch (e: IOException) {
            throw RuntimeException(e)
        }

    }


    fun put2Package() {
        val javaPackageFile = javaPackage.toFile()
        val javaSourceFilePath = javaPackageFile.toPath().resolve(Paths.get(srcFileName))
        try {
            Files.newOutputStream(javaSourceFilePath).use { out ->
                ByteArrayInputStream(srcCode.toByteArray(StandardCharsets.UTF_8)).use { `in` ->
                    val buffer = ByteArray(1024 * 1024)
                    var len: Int
                    while (`in`.read(buffer).also { len = it } != -1) {
                        out.write(buffer, 0, len)
                    }
                    out.flush()
                }
            }
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }

    fun notExistsIn(javaPackage: JavaPackageImpl): Boolean {
        val srcNames = javaPackage.listSourceFileNames()
        return !srcNames.contains(srcFileName)
    }

    fun notExists(): Boolean {
        val srcNames = javaPackage.listSourceFileNames()
        return !srcNames.contains(srcFileName)
    }

}

fun ofMysqlModel(table: Table, javaPackage: JavaPackageImpl, lang: Lang): JavaSourceFile {


    table.name ?: throw RuntimeException()


    val javaClassName = fromLowerUnderScore(javaPackage, table.name!!, "PO")


    // if kotlin
    var code = if (lang == Lang.KOTLIN) {
        kotlinWriter()
            .packagel(javaPackage.dotSplitName())
            .importl("com.baomidou.mybatisplus.annotation.TableName")
            .importl("com.baomidou.mybatisplus.annotation.TableId")
            .importl("com.baomidou.mybatisplus.annotation.IdType")
            .importl("java.io.Serializable")
            .atl("TableName(\"`" + table.name + "`\")")
            .datal().classl(javaClassName.getClazzName()).primaryConstructor { jc: KotlinCodeWriter ->
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
            .packagel(javaPackage.dotSplitName())
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
            .publicl().classl(javaClassName.getClazzName()).implementsl("Serializable")
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
    val javaSourceFile = JavaSourceFile(javaClassName, lang, code)
    return javaSourceFile
}

fun ofMybatisMapper(
    table: Table,
    javaPackage: JavaPackageImpl,
    poSourceFile: JavaSourceFile,
    lang: Lang
): JavaSourceFile {
    val poClassName = poSourceFile.javaClassName
    val mapperClassName = fromLowerUnderScore(javaPackage, table.name, "Mapper")
    var code: String = if (lang == Lang.KOTLIN) {
        val javaCode = kotlinWriter()
            .packagel(javaPackage.dotSplitName())
            .importl(poClassName!!.getFullClazzName())
            .importl("org.apache.ibatis.annotations.Mapper")
            .importl("com.baomidou.mybatisplus.core.mapper.BaseMapper")
            .atl("Mapper")
            .interfacel(mapperClassName.getClazzName()).extendsFirstl("BaseMapper<" + poClassName.getClazzName() + ">")
            .blockl()
        javaCode.toString()
    } else {
        val javaCode = javaWriter()
            .packagel(javaPackage.dotSplitName())
            .importl(poClassName!!.getFullClazzName())
            .importl("org.apache.ibatis.annotations.Mapper")
            .importl("com.baomidou.mybatisplus.core.mapper.BaseMapper")
            .atl("Mapper")
            .publicl().interfacel(mapperClassName.getClazzName()).extendsl("BaseMapper<" + poClassName.getClazzName() + ">")
            .blockl()
        javaCode.toString()
    }
    val javaSourceFile = JavaSourceFile(mapperClassName, lang, code)
    return javaSourceFile
}

fun ofServiceImpl(
    table: Table,
    javaPackage: JavaPackageImpl,
    serviceSourceFile: JavaSourceFile,
    lang: Lang
): JavaSourceFile {
    val serviceClassName = serviceSourceFile.javaClassName
    val serviceImplClassName = fromLowerUnderScore(
        javaPackage,
        table.name,
        "ServiceImpl"
    )
    var code: String = if (lang == Lang.KOTLIN) {
        val javaCode = kotlinWriter()
            .packagel(javaPackage.dotSplitName())
            .importl(serviceClassName!!.getFullClazzName())
            .importl("org.springframework.stereotype.Service")
            .atl("Service")
            .classl(serviceImplClassName.getClazzName()).extendsFirstl(serviceSourceFile.javaClassName!!.getClazzName())
            .blockl()
        javaCode.toString()
    } else {
        val javaCode = javaWriter()
            .packagel(javaPackage.dotSplitName())
            .importl(serviceClassName!!.getFullClazzName())
            .importl("lombok.extern.slf4j.Slf4j")
            .importl("org.springframework.stereotype.Service")
            .atl("Service")
            .atl("Slf4j")
            .publicl().classl(serviceImplClassName.getClazzName()).implementsl(serviceSourceFile.javaClassName!!.getClazzName())
            .blockl()
        javaCode.toString()
    }
    val javaSourceFile = JavaSourceFile(serviceImplClassName, lang, code)
    return javaSourceFile
}

fun ofDao(
    table: Table,
    javaPackage: JavaPackageImpl,
    poJavaSourceFile: JavaSourceFile,
    lang: Lang
): JavaSourceFile {
    val daoClassName = fromLowerUnderScore(javaPackage, table.name, "Dao")
    var code: String = if (lang == Lang.KOTLIN) {
        val javaCode = kotlinWriter()
            .packagel(javaPackage.dotSplitName())
            .importl(poJavaSourceFile.javaClassName!!.getFullClazzName())
            .importl("com.baomidou.mybatisplus.extension.service.IService")
            .interfacel(daoClassName.getClazzName())
            .extendsFirstl("IService<" + poJavaSourceFile.javaClassName!!.getClazzName() + ">")
            .blockl()
        javaCode.toString()
    } else {
        val javaCode = javaWriter()
            .packagel(javaPackage.dotSplitName())
            .importl(poJavaSourceFile.javaClassName!!.getFullClazzName())
            .importl("com.baomidou.mybatisplus.extension.service.IService")
            .publicl().interfacel(daoClassName.getClazzName())
            .extendsl("IService<" + poJavaSourceFile.javaClassName!!.getClazzName() + ">")
            .blockl()
        javaCode.toString()
    }
    val javaSourceFile = JavaSourceFile(daoClassName, lang, code)
    return javaSourceFile
}

fun ofDaoImpl(
    table: Table,
    javaPackage: JavaPackageImpl,
    mapperSourceFile: JavaSourceFile,
    poSourceFile: JavaSourceFile,
    daoSourceFile: JavaSourceFile,
    lang: Lang
): JavaSourceFile {
    val daoImplClassName = fromLowerUnderScore(javaPackage, table.name, "DaoImpl")
    var code: String = if (lang == Lang.KOTLIN) {
        val javaCode = kotlinWriter()
            .packagel(javaPackage.dotSplitName())
            .importl("org.springframework.stereotype.Repository")
            .importl("com.baomidou.mybatisplus.extension.service.impl.ServiceImpl")
            .importl(poSourceFile.javaClassName!!.getFullClazzName())
            .importl(mapperSourceFile.javaClassName!!.getFullClazzName())
            .importl(daoSourceFile.javaClassName!!.getFullClazzName())
            .atl("Repository")
            .classl(daoImplClassName.getClazzName())
            .extendsFirstl("ServiceImpl<" + mapperSourceFile.javaClassName!!.getClazzName() + "," + poSourceFile.javaClassName!!.getClazzName() + ">()")
            .extendsThenl(daoSourceFile.javaClassName!!.getClazzName())
            .blockl()
        javaCode.toString()
    } else {
        val javaCode = javaWriter()
            .packagel(javaPackage.dotSplitName())
            .importl("org.springframework.stereotype.Repository")
            .importl("com.baomidou.mybatisplus.extension.service.impl.ServiceImpl")
            .importl(poSourceFile.javaClassName!!.getFullClazzName())
            .importl(mapperSourceFile.javaClassName!!.getFullClazzName())
            .importl(daoSourceFile.javaClassName!!.getFullClazzName())
            .atl("Repository")
            .publicl().classl(daoImplClassName.getClazzName())
            .extendsl("ServiceImpl<" + mapperSourceFile.javaClassName!!.getClazzName() + "," + poSourceFile.javaClassName!!.getClazzName() + ">")
            .implementsl(
                daoSourceFile.javaClassName!!.getClazzName()
            )
            .blockl()
        javaCode.toString()
    }
    val javaSourceFile = JavaSourceFile(daoImplClassName, lang, code)
    return javaSourceFile
}

fun ofService(table: Table, javaPackage: JavaPackageImpl, lang: Lang): JavaSourceFile {
    val serviceClassName = fromLowerUnderScore(javaPackage, table.name, "Service")
    var code: String = if (lang == Lang.KOTLIN) {
        val javaCode = kotlinWriter()
            .packagel(javaPackage.dotSplitName())
            .interfacel(serviceClassName.getClazzName())
            .blockl()
        javaCode.toString()
    } else {
        val javaCode = javaWriter()
            .packagel(javaPackage.dotSplitName())
            .publicl().interfacel(serviceClassName.getClazzName())
            .blockl()
        javaCode.toString()
    }
    val javaSourceFile = JavaSourceFile(serviceClassName, lang, code)
    return javaSourceFile
}
