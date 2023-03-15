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

    public void generateMybatisMapper(JavaPackage javaPackage, JavaSourceFile poSourceFile) {
        JavaSourceFile javaSourceFile = JavaSourceFile.ofMybatisMapper(this, javaPackage, poSourceFile);
        //System.out.println(javaSourceFile);
        if (!javaSourceFile.existsIn(javaPackage)) {
            javaSourceFile.put2Package(javaPackage);
        }
    }

    public JavaSourceFile generateDao(JavaPackage javaPackage) {
        JavaSourceFile javaSourceFile = JavaSourceFile.ofDao(this, javaPackage);
        //System.out.println(javaSourceFile);
        if (!javaSourceFile.existsIn(javaPackage)) {
            javaSourceFile.put2Package(javaPackage);
        }
        return javaSourceFile;
    }

    public void generateDaoImpl(JavaPackage javaPackage, JavaSourceFile daoSourceFile) {
        JavaSourceFile javaSourceFile = JavaSourceFile.ofDaoImpl(this, javaPackage, daoSourceFile);
        //System.out.println(javaSourceFile);
        if (!javaSourceFile.existsIn(javaPackage)) {
            javaSourceFile.put2Package(javaPackage);
        }
    }

    public void generateService(JavaPackage javaPackage) {
        JavaSourceFile javaSourceFile = JavaSourceFile.ofService(this, javaPackage);
        if (!javaSourceFile.existsIn(javaPackage)) {
            javaSourceFile.put2Package(javaPackage);
        }
    }

    public void generateServiceImpl(JavaPackage javaPackage, JavaSourceFile serviceSourceFile) {
        JavaSourceFile javaSourceFile = JavaSourceFile.ofServiceImpl(this, javaPackage, serviceSourceFile);
        //System.out.println(javaSourceFile);
        if (!javaSourceFile.existsIn(javaPackage)) {
            javaSourceFile.put2Package(javaPackage);
        }
    }
}
