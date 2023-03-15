package fit.wenchao.db;

import java.util.Iterator;
import java.util.List;

public class Table implements Iterable<TableAttr> {
    String name;

    List<TableAttr> attrs;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Iterator<TableAttr> iterator() {
        return attrs.iterator();
    }

    public void setAttrs(List<TableAttr> attrs) {
        this.attrs = attrs;
    }

    public JavaSourceFile generateJavaModel(JavaPackage javaPackage) {
        JavaSourceFile javaSourceFile = JavaSourceFile.ofMysqlModel(this, javaPackage);
        //System.out.println(javaSourceFile);
        if (!javaSourceFile.existsIn(javaPackage)) {
            javaSourceFile.put2Package(javaPackage);
        }
        return javaSourceFile;
    }

    public JavaSourceFile generateMybatisMapper(JavaPackage javaPackage, JavaSourceFile poSourceFile) {
        JavaSourceFile javaSourceFile = JavaSourceFile.ofMybatisMapper(this, javaPackage, poSourceFile);
        //System.out.println(javaSourceFile);
        if (!javaSourceFile.existsIn(javaPackage)) {
            javaSourceFile.put2Package(javaPackage);
        }

        return javaSourceFile;
    }

    public JavaSourceFile generateDao(JavaPackage javaPackage, JavaSourceFile poJavaSourceFile) {
        JavaSourceFile javaSourceFile = JavaSourceFile.ofDao(this, javaPackage, poJavaSourceFile);
        //System.out.println(javaSourceFile);
        if (!javaSourceFile.existsIn(javaPackage)) {
            javaSourceFile.put2Package(javaPackage);
        }
        return javaSourceFile;
    }

    public void generateDaoImpl(JavaPackage javaPackage, JavaSourceFile mapperSourceFile, JavaSourceFile poSourceFile,JavaSourceFile daoSourceFile) {
        JavaSourceFile javaSourceFile = JavaSourceFile.ofDaoImpl(this, javaPackage, mapperSourceFile,poSourceFile,daoSourceFile);
        //System.out.println(javaSourceFile);
        if (!javaSourceFile.existsIn(javaPackage)) {
            javaSourceFile.put2Package(javaPackage);
        }
    }

    public JavaSourceFile generateService(JavaPackage javaPackage) {
        JavaSourceFile javaSourceFile = JavaSourceFile.ofService(this, javaPackage);
        if (!javaSourceFile.existsIn(javaPackage)) {
            javaSourceFile.put2Package(javaPackage);
        }

        return javaSourceFile;
    }

    public void generateServiceImpl(JavaPackage javaPackage, JavaSourceFile serviceSourceFile) {
        JavaSourceFile javaSourceFile = JavaSourceFile.ofServiceImpl(this, javaPackage, serviceSourceFile);
        //System.out.println(javaSourceFile);
        if (!javaSourceFile.existsIn(javaPackage)) {
            javaSourceFile.put2Package(javaPackage);
        }
    }
}
