package fit.wenchao.db

import com.google.common.base.CaseFormat

object VarCaseConvertUtils {
    fun lowerCamel2LowerUnderScore(name: String?): String {
        return CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, name!!)
    }

    fun lowerUnderScore2LowerCamel(name: String?): String {
        return CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, name!!)
    }

    fun lowerHyphen2LowerUnderCase(name: String?): String {
        return CaseFormat.LOWER_HYPHEN.to(CaseFormat.LOWER_UNDERSCORE, name!!)
    }
}
