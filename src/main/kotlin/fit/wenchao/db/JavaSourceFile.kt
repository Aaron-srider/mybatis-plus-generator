package fit.wenchao.db

import fit.wenchao.db.JavaClassName.Companion.fromLowerUnderScore
import fit.wenchao.db.codeWriter.JavaCodeWriter
import fit.wenchao.db.codeWriter.KotlinCodeWriter
import fit.wenchao.db.codeWriter.javaWriter
import fit.wenchao.db.codeWriter.kotlinWriter
import fit.wenchao.db.constants.Lang
import java.io.ByteArrayInputStream
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*

class JavaSourceFile {
    var javaClassName: JavaClassName? = null
    var srcFileName: String? = null
    var srcCode: String? = null
    var packageName: String? = null
    fun put2Package(javaPackage: JavaPackage) {
        val javaPackageFile = javaPackage.file
        val javaSourceFilePath = javaPackageFile.toPath().resolve(Paths.get(srcFileName))

        srcCode?.let {
            try {
                Files.newOutputStream(javaSourceFilePath).use { out ->
                    ByteArrayInputStream(it.toByteArray(StandardCharsets.UTF_8)).use { `in` ->
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


    }

    fun notExistsIn(javaPackage: JavaPackage): Boolean {
        val srcNames = javaPackage.listSourceFileNames()
        return !srcNames.contains(srcFileName)
    }

}

fun ofMysqlModel(table: Table, javaPackage: JavaPackage, lang: Lang): JavaSourceFile {
    val javaSourceFile = JavaSourceFile()
    val javaClassName = fromLowerUnderScore(javaPackage, table.name, "PO")
    javaSourceFile.srcFileName = javaClassName.toSrcFileName(lang)
    javaSourceFile.javaClassName = javaClassName
    javaSourceFile.packageName = javaPackage.dotSplitName

    // if kotlin
    var code = if (lang == Lang.KOTLIN) {
        kotlinWriter()
            .packagel(javaPackage.dotSplitName)
            .importl("com.baomidou.mybatisplus.annotation.TableName")
            .importl("com.baomidou.mybatisplus.annotation.TableId")
            .importl("com.baomidou.mybatisplus.annotation.IdType")
            .importl("java.io.Serializable")
            .atl("TableName(\"`" + table.name + "`\")")
            .datal().classl(javaClassName.name).primaryConstructor { jc: KotlinCodeWriter ->
                val size = table.attrs.size
                var count = 0
                for (tableAttr in table) {
                    count++
                    if (tableAttr.isPri()) {
                        //@TableId(value = "id", type = IdType.AUTO)
                        jc.atl("TableId(value=\"" + tableAttr.getName() + "\", type=IdType.AUTO)")
                    }
                    jc.write("var ")
                    val javaVarName = fromUnderScore(tableAttr.getName())
                    jc.write(javaVarName.name + ": ")
                    if (tableAttr.getType().equals("int", ignoreCase = true)) {
                        jc.write("Int ")
                        // codeBuilder.append("int ");
                    }
                    if (tableAttr.getType().equals("bigint", ignoreCase = true)) {
                        jc.write("Long ")
                        // codeBuilder.append("int ");
                    }
                    if (tableAttr.getType().lowercase(Locale.getDefault()).contains("text")
                        || tableAttr.getType().lowercase(Locale.getDefault()).contains("json")
                        || tableAttr.getType().equals("varchar", ignoreCase = true)
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
            .packagel(javaPackage.dotSplitName)
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
            .publicl().classl(javaClassName.name).implementsl("Serializable")
            .blockl { jc: JavaCodeWriter ->
                for (tableAttr in table) {
                    if (tableAttr.isPri()) {
                        //@TableId(value = "id", type = IdType.AUTO)
                        jc.atl("TableId(value=\"" + tableAttr.getName() + "\", type=IdType.AUTO)")
                    }
                    if (tableAttr.getType().equals("varchar", ignoreCase = true)) {
                        jc.write("String ")
                        // codeBuilder.append("String ");
                    }
                    if (tableAttr.getType().equals("int", ignoreCase = true)) {
                        jc.write("int ")
                        // codeBuilder.append("int ");
                    }
                    val javaVarName = fromUnderScore(tableAttr.getName())
                    jc.write(javaVarName.name + ";\n")
                    // codeBuilder.append(javaVarName.getName() + ";\n");
                }
            }.toString()
    }
    javaSourceFile.srcCode = code
    return javaSourceFile
}

fun ofMybatisMapper(
    table: Table,
    javaPackage: JavaPackage,
    poSourceFile: JavaSourceFile,
    lang: Lang
): JavaSourceFile {
    val javaSourceFile = JavaSourceFile()
    val codeBuilder = StringBuilder()
    val poClassName = poSourceFile.javaClassName
    val mapperClassName = fromLowerUnderScore(javaPackage, table.name, "Mapper")
    javaSourceFile.srcFileName = mapperClassName.toSrcFileName(lang)
    javaSourceFile.javaClassName = mapperClassName
    javaSourceFile.packageName = javaPackage.dotSplitName
    var code: String = if (lang == Lang.KOTLIN) {
        val javaCode = kotlinWriter()
            .packagel(javaPackage.dotSplitName)
            .importl(poClassName!!.getFullName())
            .importl("org.apache.ibatis.annotations.Mapper")
            .importl("com.baomidou.mybatisplus.core.mapper.BaseMapper")
            .atl("Mapper")
            .interfacel(mapperClassName.name).extendsFirstl("BaseMapper<" + poClassName.name + ">")
            .blockl()
        javaCode.toString()
    } else {
        val javaCode = javaWriter()
            .packagel(javaPackage.dotSplitName)
            .importl(poClassName!!.getFullName())
            .importl("org.apache.ibatis.annotations.Mapper")
            .importl("com.baomidou.mybatisplus.core.mapper.BaseMapper")
            .atl("Mapper")
            .publicl().interfacel(mapperClassName.name).extendsl("BaseMapper<" + poClassName.name + ">")
            .blockl()
        javaCode.toString()
    }
    javaSourceFile.srcCode = code
    return javaSourceFile
}

fun ofServiceImpl(
    table: Table,
    javaPackage: JavaPackage,
    serviceSourceFile: JavaSourceFile,
    lang: Lang
): JavaSourceFile {
    val javaSourceFile = JavaSourceFile()
    val serviceClassName = serviceSourceFile.javaClassName
    val serviceImplClassName = fromLowerUnderScore(
        javaPackage,
        table.name,
        "ServiceImpl"
    )
    javaSourceFile.srcFileName = serviceImplClassName.toSrcFileName(lang)
    javaSourceFile.javaClassName = serviceImplClassName
    javaSourceFile.packageName = javaPackage.dotSplitName
    var code: String = if (lang == Lang.KOTLIN) {
        val javaCode = kotlinWriter()
            .packagel(javaPackage.dotSplitName)
            .importl(serviceClassName!!.getFullName())
            .importl("org.springframework.stereotype.Service")
            .atl("Service")
            .classl(serviceImplClassName.name).extendsFirstl(serviceSourceFile.javaClassName!!.name)
            .blockl()
        javaCode.toString()
    } else {
        val javaCode = javaWriter()
            .packagel(javaPackage.dotSplitName)
            .importl(serviceClassName!!.getFullName())
            .importl("lombok.extern.slf4j.Slf4j")
            .importl("org.springframework.stereotype.Service")
            .atl("Service")
            .atl("Slf4j")
            .publicl().classl(serviceImplClassName.name).implementsl(serviceSourceFile.javaClassName!!.name)
            .blockl()
        javaCode.toString()
    }
    javaSourceFile.srcCode = code
    return javaSourceFile
}

fun ofDao(
    table: Table,
    javaPackage: JavaPackage,
    poJavaSourceFile: JavaSourceFile,
    lang: Lang
): JavaSourceFile {
    val javaSourceFile = JavaSourceFile()
    val daoClassName = fromLowerUnderScore(javaPackage, table.name, "Dao")
    javaSourceFile.srcFileName = daoClassName.toSrcFileName(lang)
    javaSourceFile.javaClassName = daoClassName
    javaSourceFile.packageName = javaPackage.dotSplitName
    var code: String = if (lang == Lang.KOTLIN) {
        val javaCode = kotlinWriter()
            .packagel(javaPackage.dotSplitName)
            .importl(poJavaSourceFile.javaClassName!!.getFullName())
            .importl("com.baomidou.mybatisplus.extension.service.IService")
            .interfacel(daoClassName.name)
            .extendsFirstl("IService<" + poJavaSourceFile.javaClassName!!.name + ">")
            .blockl()
        javaCode.toString()
    } else {
        val javaCode = javaWriter()
            .packagel(javaPackage.dotSplitName)
            .importl(poJavaSourceFile.javaClassName!!.getFullName())
            .importl("com.baomidou.mybatisplus.extension.service.IService")
            .publicl().interfacel(daoClassName.name)
            .extendsl("IService<" + poJavaSourceFile.javaClassName!!.name + ">")
            .blockl()
        javaCode.toString()
    }
    javaSourceFile.srcCode = code
    return javaSourceFile
}

fun ofDaoImpl(
    table: Table,
    javaPackage: JavaPackage,
    mapperSourceFile: JavaSourceFile,
    poSourceFile: JavaSourceFile,
    daoSourceFile: JavaSourceFile,
    lang: Lang
): JavaSourceFile {
    val javaSourceFile = JavaSourceFile()
    val daoImplClassName = fromLowerUnderScore(javaPackage, table.name, "DaoImpl")
    javaSourceFile.srcFileName = daoImplClassName.toSrcFileName(lang)
    javaSourceFile.javaClassName = daoImplClassName
    javaSourceFile.packageName = javaPackage.dotSplitName
    var code: String = if (lang == Lang.KOTLIN) {
        val javaCode = kotlinWriter()
            .packagel(javaPackage.dotSplitName)
            .importl("org.springframework.stereotype.Repository")
            .importl("com.baomidou.mybatisplus.extension.service.impl.ServiceImpl")
            .importl(poSourceFile.javaClassName!!.getFullName())
            .importl(mapperSourceFile.javaClassName!!.getFullName())
            .importl(daoSourceFile.javaClassName!!.getFullName())
            .atl("Repository")
            .classl(daoImplClassName.name)
            .extendsFirstl("ServiceImpl<" + mapperSourceFile.javaClassName!!.name + "," + poSourceFile.javaClassName!!.name + ">()")
            .extendsThenl(daoSourceFile.javaClassName!!.name)
            .blockl()
        javaCode.toString()
    } else {
        val javaCode = javaWriter()
            .packagel(javaPackage.dotSplitName)
            .importl("org.springframework.stereotype.Repository")
            .importl("com.baomidou.mybatisplus.extension.service.impl.ServiceImpl")
            .importl(poSourceFile.javaClassName!!.getFullName())
            .importl(mapperSourceFile.javaClassName!!.getFullName())
            .importl(daoSourceFile.javaClassName!!.getFullName())
            .atl("Repository")
            .publicl().classl(daoImplClassName.name)
            .extendsl("ServiceImpl<" + mapperSourceFile.javaClassName!!.name + "," + poSourceFile.javaClassName!!.name + ">")
            .implementsl(
                daoSourceFile.javaClassName!!.name
            )
            .blockl()
        javaCode.toString()
    }
    javaSourceFile.srcCode = code
    return javaSourceFile
}

fun ofService(table: Table, javaPackage: JavaPackage, lang: Lang): JavaSourceFile {
    val javaSourceFile = JavaSourceFile()
    val serviceClassName = fromLowerUnderScore(javaPackage, table.name, "Service")
    javaSourceFile.srcFileName = serviceClassName.toSrcFileName(lang)
    javaSourceFile.javaClassName = serviceClassName
    javaSourceFile.packageName = javaPackage.dotSplitName
    var code: String = if (lang == Lang.KOTLIN) {
        val javaCode = kotlinWriter()
            .packagel(javaPackage.dotSplitName)
            .interfacel(serviceClassName.name)
            .blockl()
        javaCode.toString()
    } else {
        val javaCode = javaWriter()
            .packagel(javaPackage.dotSplitName)
            .publicl().interfacel(serviceClassName.name)
            .blockl()
        javaCode.toString()
    }
    javaSourceFile.srcCode = code
    return javaSourceFile
}
