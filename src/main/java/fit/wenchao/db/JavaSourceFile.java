package fit.wenchao.db;

import lombok.var;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class JavaSourceFile {
    JavaClassName javaClassName;
    String srcFileName;
    String srcCode;

    String packageName;

    public static JavaSourceFile ofMysqlModel(Table table, JavaPackage javaPackage) {
        JavaSourceFile javaSourceFile = new JavaSourceFile();
        StringBuilder codeBuilder = new StringBuilder();

        JavaClassName javaClassName = JavaClassName.fromLowerUnderScore(javaPackage, table.getName(), "PO");
        javaSourceFile.srcFileName = javaClassName.toJavaSrcFileName();
        javaSourceFile.javaClassName = javaClassName;
        javaSourceFile.packageName = javaPackage.getDotSplitName();
        //JavaClassName javaClassName = JavaClassName.fromLowerUnderScore("user_action");

        JavaCode javaCode = JavaCode
                .writing()
                .packagel(javaPackage.getDotSplitName())
                .importl("lombok.Data")
                .atl("Data")
                .publicl().classl(javaClassName.getName())
                .blockl(jc -> {
                    for (TableAttr tableAttr : table) {
                        if (tableAttr.getType().equalsIgnoreCase("varchar")) {
                            jc.write("String ");
                            //codeBuilder.append("String ");
                        }
                        if (tableAttr.getType().equalsIgnoreCase("int")) {
                            jc.write("int ");
                            //codeBuilder.append("int ");
                        }
                        JavaVarName javaVarName = JavaVarName.fromUnderScore(tableAttr.getName());
                        jc.write(javaVarName.getName() + ";\n");
                        //codeBuilder.append(javaVarName.getName() + ";\n");
                    }
                });

        javaSourceFile.srcCode = javaCode.toString();
        return javaSourceFile;
    }

    public static JavaSourceFile ofMybatisMapper(Table table, JavaPackage javaPackage, JavaSourceFile poSourceFile) {
        JavaSourceFile javaSourceFile = new JavaSourceFile();
        StringBuilder codeBuilder = new StringBuilder();

        JavaClassName poClassName = poSourceFile.getJavaClassName();

        JavaClassName mapperClassName = JavaClassName.fromLowerUnderScore(javaPackage, table.getName(), "Mapper");
        //poSourceFile.
        javaSourceFile.srcFileName = mapperClassName.toJavaSrcFileName();
        javaSourceFile.javaClassName = mapperClassName;
        javaSourceFile.packageName = javaPackage.getDotSplitName();
        //JavaClassName javaClassName = JavaClassName.fromLowerUnderScore("user_action");


        JavaCode javaCode =
                JavaCode.writing()
                        .packagel(javaPackage.getDotSplitName())
                        .importl(poClassName.getFullName())
                        .importl("org.apache.ibatis.annotations.Mapper")
                        .importl("com.baomidou.mybatisplus.core.mapper.BaseMapper")
                        .atl("Mapper")
                        .publicl().interfacel(mapperClassName.getName()).extendsl("BaseMapper<" + poClassName.getName() + ">")
                        .blockl();

        javaSourceFile.srcCode = javaCode.toString();
        return javaSourceFile;
    }

    public static JavaSourceFile ofServiceImpl(Table table, JavaPackage javaPackage, JavaSourceFile serviceSourceFile) {
        JavaSourceFile javaSourceFile = new JavaSourceFile();
        StringBuilder codeBuilder = new StringBuilder();

        JavaClassName serviceClassName = serviceSourceFile.getJavaClassName();

        JavaClassName serviceImplClassName = JavaClassName.fromLowerUnderScore(javaPackage, table.getName(), "ServiceImpl");
        //poSourceFile.
        javaSourceFile.srcFileName = serviceImplClassName.toJavaSrcFileName();
        javaSourceFile.javaClassName = serviceImplClassName;
        javaSourceFile.packageName = javaPackage.getDotSplitName();
        //JavaClassName javaClassName = JavaClassName.fromLowerUnderScore("user_action");
        //TODO
        JavaCode javaCode =
                JavaCode.writing()
                        .packagel(javaPackage.getDotSplitName())
                        .importl(serviceClassName.getFullName())
                        .importl("org.apache.ibatis.annotations.Mapper")
                        .importl("com.baomidou.mybatisplus.core.mapper.BaseMapper")
                        .atl("Mapper")
                        .publicl().interfacel(serviceImplClassName.getName()).extendsl("BaseMapper<" + serviceClassName.getName() + ">")
                        .blockl();

        javaSourceFile.srcCode = javaCode.toString();
        return javaSourceFile;
        return null;
    }


    private JavaClassName getJavaClassName() {
        return this.javaClassName;
    }

    public static JavaSourceFile ofDao(Table table, JavaPackage javaPackage) {
        JavaSourceFile javaSourceFile = new JavaSourceFile();
        StringBuilder codeBuilder = new StringBuilder();

        JavaClassName daoClassName = JavaClassName.fromLowerUnderScore(javaPackage, table.getName(), "Dao");
        javaSourceFile.srcFileName = daoClassName.toJavaSrcFileName();
        javaSourceFile.javaClassName = daoClassName;
        javaSourceFile.packageName = javaPackage.getDotSplitName();
        //JavaClassName javaClassName = JavaClassName.fromLowerUnderScore("user_action");

        JavaCode javaCode = JavaCode
                .writing()
                .packagel(javaPackage.getDotSplitName())
                .publicl().interfacel(daoClassName.getName())
                .blockl();

        javaSourceFile.srcCode = javaCode.toString();
        return javaSourceFile;
    }

    public static JavaSourceFile ofDaoImpl(Table table, JavaPackage javaPackage, JavaSourceFile daoSourceFile) {
        JavaSourceFile javaSourceFile = new JavaSourceFile();
        StringBuilder codeBuilder = new StringBuilder();

        JavaClassName daoImplClassName = JavaClassName.fromLowerUnderScore(javaPackage, table.getName(), "DaoImpl");
        javaSourceFile.srcFileName = daoImplClassName.toJavaSrcFileName();
        javaSourceFile.javaClassName = daoImplClassName;
        javaSourceFile.packageName = javaPackage.getDotSplitName();
        //JavaClassName javaClassName = JavaClassName.fromLowerUnderScore("user_action");

        JavaCode javaCode =
                JavaCode.writing()
                        .packagel(javaPackage.getDotSplitName())
                        .importl("org.springframework.stereotype.Repository")
                        .importl(daoSourceFile.getJavaClassName().getFullName())
                        .atl("Repository")
                        .publicl().classl(daoImplClassName.getName()).implementsl(daoSourceFile.getJavaClassName().getName())
                        .blockl();

        javaSourceFile.srcCode = javaCode.toString();
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
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public static JavaSourceFile ofService(Table table, JavaPackage javaPackage) {
        JavaSourceFile javaSourceFile = new JavaSourceFile();


        JavaClassName serviceClassName = JavaClassName.fromLowerUnderScore(javaPackage, table.getName(), "Service");
        //poSourceFile.
        javaSourceFile.srcFileName = serviceClassName.toJavaSrcFileName();
        javaSourceFile.javaClassName = serviceClassName;
        javaSourceFile.packageName = javaPackage.getDotSplitName();
        //JavaClassName javaClassName = JavaClassName.fromLowerUnderScore("user_action");

        JavaCode javaCode =
                JavaCode.writing()
                        .packagel(javaPackage.getDotSplitName())
                        .publicl().interfacel(serviceClassName.getName())
                        .blockl();

        javaSourceFile.srcCode = javaCode.toString();
        return javaSourceFile;
    }

    public boolean existsIn(JavaPackage javaPackage) {
        var srcNames = javaPackage.listSourceFileNames();
        return srcNames.contains(this.srcFileName);
    }
}
