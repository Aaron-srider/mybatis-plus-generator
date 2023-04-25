package fit.wenchao.db;

import com.sun.org.apache.bcel.internal.classfile.Code;
import fit.wenchao.db.codeWriter.CodeWriter;
import fit.wenchao.db.codeWriter.CodeWriterKt;
import fit.wenchao.db.codeWriter.JavaCodeWriter;
import fit.wenchao.db.codeWriter.KotlinCodeWriter;
import lombok.var;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static fit.wenchao.db.codeWriter.CodeWriterKt.kotlinWriter;

public class JavaSourceFile {

    JavaClassName javaClassName;

    String srcFileName;

    String srcCode;

    String packageName;

    public static JavaSourceFile ofMysqlModel(Table table, JavaPackage javaPackage, String lang) {
        lang = determineLang(lang);

        JavaSourceFile javaSourceFile = new JavaSourceFile();
        StringBuilder codeBuilder = new StringBuilder();

        JavaClassName javaClassName = JavaClassName.fromLowerUnderScore(javaPackage, table.getName(), "PO");

        javaSourceFile.srcFileName = javaClassName.toSrcFileName(lang);
        javaSourceFile.javaClassName = javaClassName;
        javaSourceFile.packageName = javaPackage.getDotSplitName();

        String code = "";

        // if kotlin
        if (lang.equals("kotlin")) {
            KotlinCodeWriter kotlinCodeWriter = kotlinWriter();

            code = kotlinCodeWriter.writing()
                    .packagel(javaPackage.getDotSplitName())

                    .importl("com.baomidou.mybatisplus.annotation.TableName")
                    .importl("com.baomidou.mybatisplus.annotation.TableId")
                    .importl("com.baomidou.mybatisplus.annotation.IdType")
                    .importl("java.io.Serializable")
                    .atl("TableName(\"`" + table.getName() + "`\")")
                    .datal().classl(javaClassName.getName()).primaryConstructor((jc) -> {
                        int size = table.attrs.size();
                        int count = 0;
                        for (TableAttr tableAttr : table) {
                            count++;
                            if (tableAttr.isPri()) {
                                //@TableId(value = "id", type = IdType.AUTO)
                                jc.atl("TableId(value=\"" + tableAttr.getName() + "\", type=IdType.AUTO)");
                            }
                            jc.write("var ");
                            JavaVarName javaVarName = JavaVarName.fromUnderScore(tableAttr.getName());
                            jc.write(javaVarName.name + ": ");

                            if (tableAttr.getType().equalsIgnoreCase("int")) {
                                jc.write("Int ");
                                // codeBuilder.append("int ");
                            }

                            if (tableAttr.getType().equalsIgnoreCase("bigint")) {
                                jc.write("Long ");
                                // codeBuilder.append("int ");
                            }
                            if (tableAttr.getType().toLowerCase().contains("text")
                                    || tableAttr.getType().toLowerCase().contains("json")
                                    || tableAttr.getType().equalsIgnoreCase("varchar")) {
                                jc.write("String ");
                            }

                            if (count == size
                            ) {
                                jc.write("?");

                            } else {
                                jc.write("?,");
                            }

                            jc.write("\n");
                        }

                        return null;
                    }).extendsFirstl("Serializable")
                    .toString();

        } else {
            code = JavaCodeWriter
                    .writing()
                    .packagel(javaPackage.getDotSplitName())

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
                    .atl("TableName(\"`" + table.getName() + "`\")")
                    .publicl().classl(javaClassName.getName()).implementsl("Serializable")
                    .blockl(jc -> {
                        for (TableAttr tableAttr : table) {
                            if (tableAttr.isPri()) {
                                //@TableId(value = "id", type = IdType.AUTO)
                                jc.atl("TableId(value=\"" + tableAttr.getName() + "\", type=IdType.AUTO)");
                            }
                            if (tableAttr.getType().equalsIgnoreCase("varchar")) {
                                jc.write("String ");
                                // codeBuilder.append("String ");
                            }
                            if (tableAttr.getType().equalsIgnoreCase("int")) {
                                jc.write("int ");
                                // codeBuilder.append("int ");
                            }
                            JavaVarName javaVarName = JavaVarName.fromUnderScore(tableAttr.getName());
                            jc.write(javaVarName.getName() + ";\n");
                            // codeBuilder.append(javaVarName.getName() + ";\n");
                        }
                    }).toString();
        }

        javaSourceFile.srcCode = code;
        return javaSourceFile;
    }

    private static String determineLang(String lang) {
        if (lang == null || (!lang.equals("java") && !lang.equals("kotlin"))) {
            // default
            lang = "java";
        }
        return lang;
    }

    public static JavaSourceFile ofMybatisMapper(Table table, JavaPackage javaPackage, JavaSourceFile poSourceFile, String lang) {
        lang = determineLang(lang);

        JavaSourceFile javaSourceFile = new JavaSourceFile();
        StringBuilder codeBuilder = new StringBuilder();

        JavaClassName poClassName = poSourceFile.getJavaClassName();

        JavaClassName mapperClassName = JavaClassName.fromLowerUnderScore(javaPackage, table.getName(), "Mapper");

        javaSourceFile.srcFileName = mapperClassName.toSrcFileName(lang);
        javaSourceFile.javaClassName = mapperClassName;
        javaSourceFile.packageName = javaPackage.getDotSplitName();

        String code = null;
        if (lang.equals("kotlin")) {
            KotlinCodeWriter javaCode =
                    CodeWriterKt.kotlinWriter()
                            .packagel(javaPackage.getDotSplitName())
                            .importl(poClassName.getFullName())
                            .importl("org.apache.ibatis.annotations.Mapper")
                            .importl("com.baomidou.mybatisplus.core.mapper.BaseMapper")
                            .atl("Mapper")
                            .interfacel(mapperClassName.getName()).extendsFirstl("BaseMapper<" + poClassName.getName() + ">")
                            .blockl();
            code = javaCode.toString();
        } else {
            JavaCodeWriter javaCode =
                    CodeWriterKt.javaWriter()
                            .packagel(javaPackage.getDotSplitName())
                            .importl(poClassName.getFullName())
                            .importl("org.apache.ibatis.annotations.Mapper")
                            .importl("com.baomidou.mybatisplus.core.mapper.BaseMapper")
                            .atl("Mapper")
                            .publicl().interfacel(mapperClassName.getName()).extendsl("BaseMapper<" + poClassName.getName() + ">")
                            .blockl();
            code = javaCode.toString();
        }


        javaSourceFile.srcCode = code;
        return javaSourceFile;
    }

    public static JavaSourceFile ofServiceImpl(Table table, JavaPackage javaPackage, JavaSourceFile serviceSourceFile, String lang) {
        lang = determineLang(lang);
        JavaSourceFile javaSourceFile = new JavaSourceFile();

        JavaClassName serviceClassName = serviceSourceFile.getJavaClassName();

        JavaClassName serviceImplClassName = JavaClassName.fromLowerUnderScore(javaPackage,
                table.getName(),
                "ServiceImpl");
        javaSourceFile.srcFileName = serviceImplClassName.toSrcFileName(lang);
        javaSourceFile.javaClassName = serviceImplClassName;
        javaSourceFile.packageName = javaPackage.getDotSplitName();

        String code = null;
        if (lang.equals("kotlin")) {
            KotlinCodeWriter javaCode =
                    CodeWriterKt.kotlinWriter()
                            .packagel(javaPackage.getDotSplitName())
                            .importl(serviceClassName.getFullName())
                            .importl("org.springframework.stereotype.Service")
                            .atl("Service")
                            .classl(serviceImplClassName.getName()).extendsFirstl(serviceSourceFile.getJavaClassName().getName())
                            .blockl();
            code = javaCode.toString();
        } else {
            JavaCodeWriter javaCode =
                    CodeWriterKt.javaWriter()
                            .packagel(javaPackage.getDotSplitName())
                            .importl(serviceClassName.getFullName())
                            .importl("lombok.extern.slf4j.Slf4j")
                            .importl("org.springframework.stereotype.Service")
                            .atl("Service")
                            .atl("Slf4j")
                            .publicl().classl(serviceImplClassName.getName()).implementsl(serviceSourceFile.getJavaClassName().getName())
                            .blockl();
            code = javaCode.toString();
        }


        javaSourceFile.srcCode = code;
        return javaSourceFile;
    }


    private JavaClassName getJavaClassName() {
        return this.javaClassName;
    }

    public static JavaSourceFile ofDao(Table table, JavaPackage javaPackage, JavaSourceFile poJavaSourceFile, String lang) {
        lang = determineLang(lang);
        JavaSourceFile javaSourceFile = new JavaSourceFile();

        JavaClassName daoClassName = JavaClassName.fromLowerUnderScore(javaPackage, table.getName(), "Dao");
        javaSourceFile.srcFileName = daoClassName.toSrcFileName(lang);
        javaSourceFile.javaClassName = daoClassName;
        javaSourceFile.packageName = javaPackage.getDotSplitName();

        String code = null;
        if (lang.equals("kotlin")) {
            KotlinCodeWriter javaCode =
                    CodeWriterKt.kotlinWriter()
                            .packagel(javaPackage.getDotSplitName())
                            .importl(poJavaSourceFile.getJavaClassName().getFullName())
                            .importl("com.baomidou.mybatisplus.extension.service.IService")
                            .interfacel(daoClassName.getName()).extendsFirstl("IService<" + poJavaSourceFile.getJavaClassName().getName() + ">")
                            .blockl();
            code = javaCode.toString();
        } else {
            JavaCodeWriter javaCode =
                    CodeWriterKt.javaWriter()
                            .packagel(javaPackage.getDotSplitName())
                            .importl(poJavaSourceFile.getJavaClassName().getFullName())
                            .importl("com.baomidou.mybatisplus.extension.service.IService")
                            .publicl().interfacel(daoClassName.getName()).extendsl("IService<" + poJavaSourceFile.getJavaClassName().getName() + ">")
                            .blockl();
            code = javaCode.toString();
        }

        javaSourceFile.srcCode = code;
        return javaSourceFile;
    }

    public static JavaSourceFile ofDaoImpl(Table table, JavaPackage javaPackage, JavaSourceFile mapperSourceFile, JavaSourceFile poSourceFile, JavaSourceFile daoSourceFile, String lang) {
        lang = determineLang(lang);
        JavaSourceFile javaSourceFile = new JavaSourceFile();

        JavaClassName daoImplClassName = JavaClassName.fromLowerUnderScore(javaPackage, table.getName(), "DaoImpl");
        javaSourceFile.srcFileName = daoImplClassName.toSrcFileName(lang);
        javaSourceFile.javaClassName = daoImplClassName;
        javaSourceFile.packageName = javaPackage.getDotSplitName();

        String code = null;
        if (lang.equals("kotlin")) {
            KotlinCodeWriter javaCode =
                    CodeWriterKt.kotlinWriter()
                            .packagel(javaPackage.getDotSplitName())
                            .importl("org.springframework.stereotype.Repository")
                            .importl("com.baomidou.mybatisplus.extension.service.impl.ServiceImpl")
                            .importl(poSourceFile.getJavaClassName().getFullName())
                            .importl(mapperSourceFile.getJavaClassName().getFullName())
                            .importl(daoSourceFile.getJavaClassName().getFullName())
                            .atl("Repository")
                            .classl(daoImplClassName.getName())
                            .extendsFirstl("ServiceImpl<" + mapperSourceFile.getJavaClassName().getName() + "," + poSourceFile.getJavaClassName().getName() + ">()")
                            .extendsThenl(daoSourceFile.getJavaClassName().getName())
                            .blockl();
            code = javaCode.toString();
        } else {
            JavaCodeWriter javaCode =
                    JavaCodeWriter.writing()
                            .packagel(javaPackage.getDotSplitName())
                            .importl("org.springframework.stereotype.Repository")
                            .importl("com.baomidou.mybatisplus.extension.service.impl.ServiceImpl")
                            .importl(poSourceFile.getJavaClassName().getFullName())
                            .importl(mapperSourceFile.getJavaClassName().getFullName())
                            .importl(daoSourceFile.getJavaClassName().getFullName())
                            .atl("Repository")
                            .publicl().classl(daoImplClassName.getName()).extendsl("ServiceImpl<" + mapperSourceFile.getJavaClassName().getName() + "," + poSourceFile.getJavaClassName().getName() + ">").implementsl(
                                    daoSourceFile.getJavaClassName().getName())
                            .blockl();
            code = javaCode.toString();
        }

        javaSourceFile.srcCode = code;
        return javaSourceFile;
    }

    public void put2Package(JavaPackage javaPackage) {
        File javaPackageFile = javaPackage.getFile();
        Path javaSourceFilePath = javaPackageFile.toPath().resolve(Paths.get(this.srcFileName));
        try (
                OutputStream out = Files.newOutputStream(javaSourceFilePath);
                ByteArrayInputStream in = new ByteArrayInputStream(this.srcCode.getBytes(StandardCharsets.UTF_8));
        ) {
            byte[] buffer = new byte[1024 * 1024];
            int len;
            while ((len = in.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }
            out.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static JavaSourceFile ofService(Table table, JavaPackage javaPackage, String lang) {
        lang = determineLang(lang);
        JavaSourceFile javaSourceFile = new JavaSourceFile();

        JavaClassName serviceClassName = JavaClassName.fromLowerUnderScore(javaPackage, table.getName(), "Service");
        javaSourceFile.srcFileName = serviceClassName.toSrcFileName(lang);
        javaSourceFile.javaClassName = serviceClassName;
        javaSourceFile.packageName = javaPackage.getDotSplitName();


        String code = null;
        if ("kotlin".equals(lang)) {
            KotlinCodeWriter javaCode =
                    CodeWriterKt.kotlinWriter()
                            .packagel(javaPackage.getDotSplitName())
                            .interfacel(serviceClassName.getName())
                            .blockl();
            code = javaCode.toString();
        } else {
            JavaCodeWriter javaCode =
                    JavaCodeWriter.writing()
                            .packagel(javaPackage.getDotSplitName())
                            .publicl().interfacel(serviceClassName.getName())
                            .blockl();
            code = javaCode.toString();
        }


        javaSourceFile.srcCode = code;
        return javaSourceFile;
    }

    public boolean existsIn(JavaPackage javaPackage) {
        var srcNames = javaPackage.listSourceFileNames();
        return srcNames.contains(this.srcFileName);
    }
}
