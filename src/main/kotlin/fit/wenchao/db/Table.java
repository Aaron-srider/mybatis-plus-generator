package fit.wenchao.db;

import fit.wenchao.db.constants.Lang;

import java.util.Iterator;
import java.util.List;

public class Table implements Iterable<TableAttr> {

    public String name;

    public List<TableAttr> attrs;

    @Override
    public Iterator<TableAttr> iterator() {
        return attrs.iterator();
    }

    public void setAttrs(List<TableAttr> attrs) {
        this.attrs = attrs;
    }

    public JavaSourceFile generateJavaModel(JavaPackage javaPackage) {
        JavaSourceFile javaSourceFile = JavaSourceFileKt.ofMysqlModel(this, javaPackage, Lang.KOTLIN);
        // System.out.println(javaSourceFile);
        if (javaSourceFile.notExistsIn(javaPackage)) {
            javaSourceFile.put2Package(javaPackage);
        }
        return javaSourceFile;
    }

    public JavaSourceFile generateMybatisMapper(JavaPackage javaPackage, JavaSourceFile poSourceFile) {
        JavaSourceFile javaSourceFile = JavaSourceFileKt.ofMybatisMapper(this, javaPackage, poSourceFile, Lang.KOTLIN);
        // System.out.println(javaSourceFile);
        if (javaSourceFile.notExistsIn(javaPackage)) {
            javaSourceFile.put2Package(javaPackage);
        }

        return javaSourceFile;
    }

    public JavaSourceFile generateDao(JavaPackage javaPackage, JavaSourceFile poJavaSourceFile) {
        JavaSourceFile javaSourceFile = JavaSourceFileKt.ofDao(this, javaPackage, poJavaSourceFile, Lang.KOTLIN);
        // System.out.println(javaSourceFile);
        if (javaSourceFile.notExistsIn(javaPackage)) {
            javaSourceFile.put2Package(javaPackage);
        }
        return javaSourceFile;
    }

    public void generateDaoImpl(JavaPackage javaPackage, JavaSourceFile mapperSourceFile, JavaSourceFile poSourceFile, JavaSourceFile daoSourceFile) {
        JavaSourceFile javaSourceFile = JavaSourceFileKt.ofDaoImpl(this, javaPackage, mapperSourceFile, poSourceFile, daoSourceFile,Lang.KOTLIN );
        // System.out.println(javaSourceFile);
        if (javaSourceFile.notExistsIn(javaPackage)) {
            javaSourceFile.put2Package(javaPackage);
        }
    }

    public JavaSourceFile generateService(JavaPackage javaPackage) {
        JavaSourceFile javaSourceFile = JavaSourceFileKt.ofService(this, javaPackage,Lang.KOTLIN );
        if (javaSourceFile.notExistsIn(javaPackage)) {
            javaSourceFile.put2Package(javaPackage);
        }

        return javaSourceFile;
    }

    public void generateServiceImpl(JavaPackage javaPackage, JavaSourceFile serviceSourceFile) {
        JavaSourceFile javaSourceFile = JavaSourceFileKt.ofServiceImpl(this, javaPackage, serviceSourceFile,Lang.KOTLIN );
        // System.out.println(javaSourceFile);
        if (javaSourceFile.notExistsIn(javaPackage)) {
            javaSourceFile.put2Package(javaPackage);
        }
    }
}
