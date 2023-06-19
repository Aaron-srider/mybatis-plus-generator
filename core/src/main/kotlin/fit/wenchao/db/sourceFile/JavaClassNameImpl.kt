package fit.wenchao.db.sourceFile

import fit.wenchao.db.VarCaseConvertUtils
import fit.wenchao.db.constants.Lang

interface JavaClassName  {
    fun getClazzName(): String
    fun getPackagePart(): JavaPackage
    fun getFullClazzName(): String
    fun toSrcFileName(lang: Lang): String
}
class JavaClassNameImpl(var name: String, var javaPackage: JavaPackageImpl): JavaClassName {

    companion object {
        fun fromLowerUnderScore(javaPackage: JavaPackageImpl, lowerUnderScoreName: String, clazzNameSuffix: String): JavaClassName {
            val upperCamelName = VarCaseConvertUtils.lowerUnderScore2UpperCamel(lowerUnderScoreName)
            return JavaClassNameImpl(upperCamelName + clazzNameSuffix, javaPackage)
        }
    }

    override fun getClazzName(): String {
        return name
    }

    override fun getPackagePart(): JavaPackage {
        return javaPackage
    }

    override fun getFullClazzName(): String {
        return "${javaPackage.dotSplitName()}.$name"
    }

    private fun toJavaSrcFileName(): String {
        return "$name.java"
    }

    private fun toKotlinSrcFileName(): String {
        return "$name.kt"
    }

    override fun toSrcFileName(lang: Lang): String {
        return if (lang == Lang.KOTLIN) {
            toKotlinSrcFileName()
        } else {
            toJavaSrcFileName()
        }
    }

    override fun toString(): String {
        return name
    }
}
