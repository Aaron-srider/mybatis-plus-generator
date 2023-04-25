package fit.wenchao.db

import fit.wenchao.db.constants.Lang

class Table : Iterable<TableAttr> {

    var name: String = ""

    var attrs: List<TableAttr> = mutableListOf()

    override fun iterator(): Iterator<TableAttr> {
        return attrs.iterator()
    }

    fun generateJavaModel(javaPackage: JavaPackage): JavaSourceFile {
        val javaSourceFile = ofMysqlModel(this, javaPackage, Lang.KOTLIN)
        if (javaSourceFile.notExistsIn(javaPackage)) {
            javaSourceFile.put2Package(javaPackage)
        }
        return javaSourceFile
    }

    fun generateMybatisMapper(javaPackage: JavaPackage, poSourceFile: JavaSourceFile): JavaSourceFile {
        val javaSourceFile = ofMybatisMapper(this, javaPackage, poSourceFile, Lang.KOTLIN)
        if (javaSourceFile.notExistsIn(javaPackage)) {
            javaSourceFile.put2Package(javaPackage)
        }
        return javaSourceFile
    }

    fun generateDao(javaPackage: JavaPackage, poJavaSourceFile: JavaSourceFile): JavaSourceFile {
        val javaSourceFile = ofDao(this, javaPackage, poJavaSourceFile, Lang.KOTLIN)
        // System.out.println(javaSourceFile);
        if (javaSourceFile.notExistsIn(javaPackage)) {
            javaSourceFile.put2Package(javaPackage)
        }
        return javaSourceFile
    }

    fun generateDaoImpl(
        javaPackage: JavaPackage,
        mapperSourceFile: JavaSourceFile,
        poSourceFile: JavaSourceFile,
        daoSourceFile: JavaSourceFile
    ) {
        val javaSourceFile = ofDaoImpl(
            this,
            javaPackage, mapperSourceFile, poSourceFile, daoSourceFile, Lang.KOTLIN
        )
        if (javaSourceFile.notExistsIn(javaPackage)) {
            javaSourceFile.put2Package(javaPackage)
        }
    }

    fun generateService(javaPackage: JavaPackage): JavaSourceFile {
        val javaSourceFile = ofService(this, javaPackage, Lang.KOTLIN)
        if (javaSourceFile.notExistsIn(javaPackage)) {
            javaSourceFile.put2Package(javaPackage)
        }
        return javaSourceFile
    }

    fun generateServiceImpl(javaPackage: JavaPackage, serviceSourceFile: JavaSourceFile) {
        val javaSourceFile = ofServiceImpl(this, javaPackage, serviceSourceFile, Lang.KOTLIN)
        if (javaSourceFile.notExistsIn(javaPackage)) {
            javaSourceFile.put2Package(javaPackage)
        }
    }
}