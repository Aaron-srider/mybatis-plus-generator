package fit.wenchao

import fit.wenchao.db.VarCaseConvertUtils
import org.junit.jupiter.api.Test

class VarCaseConvertUtilsTest {

    @Test
    fun test() {
        var testString = "test_string"
        println(VarCaseConvertUtils.lowerUnderScore2UpperCamel(testString))
    }
}