package fit.wenchao.db

import fit.wenchao.db.constants.Lang
import fit.wenchao.db.sourceFile.*
import kotlin.reflect.KProperty

class QuotedStringDelegate {
    private var value: String = ""

    operator fun getValue(thisRef: Any?, property: KProperty<*>): String {
        return value
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, newValue: String) {
        value = "`$newValue`"
    }
}

class Table : Iterable<TableAttr> {

    var name: String  = ""

    var attrs: List<TableAttr> = mutableListOf()


    fun getQuotedName():String {
        return "`${name}`"
    }


    override fun iterator(): Iterator<TableAttr> {
        return attrs.iterator()
    }

    fun generateJavaModel(javaPackage: JavaPackageImpl): JavaSourceFile {
        val javaSourceFile = ofMysqlModel(this, javaPackage, Lang.KOTLIN)
        if (javaSourceFile.notExistsIn(javaPackage)) {
            javaSourceFile.put2Package(javaPackage)
        }
        return javaSourceFile
    }

    fun generateMybatisMapper(javaPackage: JavaPackageImpl, poSourceFile: JavaSourceFile): JavaSourceFile {
        val javaSourceFile = ofMybatisMapper(this, javaPackage, poSourceFile, Lang.KOTLIN)
        if (javaSourceFile.notExistsIn(javaPackage)) {
            javaSourceFile.put2Package(javaPackage)
        }
        return javaSourceFile
    }

    fun generateDao(javaPackage: JavaPackageImpl, poJavaSourceFile: JavaSourceFile): JavaSourceFile {
        val javaSourceFile = ofDao(this, javaPackage, poJavaSourceFile, Lang.KOTLIN)
        // System.out.println(javaSourceFile);
        if (javaSourceFile.notExistsIn(javaPackage)) {
            javaSourceFile.put2Package(javaPackage)
        }
        return javaSourceFile
    }

    fun generateDaoImpl(
        javaPackage: JavaPackageImpl,
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

    fun generateService(javaPackage: JavaPackageImpl): JavaSourceFile {
        val javaSourceFile = ofService(this, javaPackage, Lang.KOTLIN)
        if (javaSourceFile.notExistsIn(javaPackage)) {
            javaSourceFile.put2Package(javaPackage)
        }
        return javaSourceFile
    }

    fun generateServiceImpl(javaPackage: JavaPackageImpl, serviceSourceFile: JavaSourceFile) {
        val javaSourceFile = ofServiceImpl(this, javaPackage, serviceSourceFile, Lang.KOTLIN)
        if (javaSourceFile.notExistsIn(javaPackage)) {
            javaSourceFile.put2Package(javaPackage)
        }
    }
}