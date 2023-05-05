package fit.wenchao.db

import fit.wenchao.db.constants.Lang

class JavaClassName(var name: String, var javaPackage: JavaPackage) {

    companion object {
        fun fromLowerUnderScore(javaPackage: JavaPackage, name: String, prefix: String): JavaClassName {
            val underScore = VarCaseConvertUtils.lowerUnderScore2LowerCamel(name)
            val capitalized = underScore.replaceFirstChar { it.uppercase() }
            return JavaClassName(capitalized + prefix, javaPackage)
        }
    }

    fun getFullName(): String {
        return "${javaPackage.dotSplitName}.$name"
    }

    fun toJavaSrcFileName(): String {
        return "$name.java"
    }

    fun toKotlinSrcFileName(): String {
        return "$name.kt"
    }

    fun toSrcFileName(lang: Lang): String {
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
