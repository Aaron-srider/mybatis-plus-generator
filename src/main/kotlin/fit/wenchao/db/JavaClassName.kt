package fit.wenchao.db

import fit.wenchao.db.constants.Lang

class JavaClassName {

    lateinit var name: String;
    lateinit var javaPackage: JavaPackage;

    companion object {
        @JvmStatic
        fun fromLowerUnderScore(javaPackage: JavaPackage, name: String, prefix: String): JavaClassName {
            val kotlinClassName = JavaClassName()
            kotlinClassName.javaPackage = javaPackage
            val underScore = VarCaseConvertUtils.lowerUnderScore2LowerCamel(name)
            val capitalized = underScore.replaceFirstChar { it.uppercase() }
            kotlinClassName.name = capitalized + prefix
            return kotlinClassName
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
