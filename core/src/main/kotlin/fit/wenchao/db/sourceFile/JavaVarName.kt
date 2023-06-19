package fit.wenchao.db.sourceFile;

import fit.wenchao.db.VarCaseConvertUtils


class JavaVarName {
    var name: String? = null;
}

fun fromUnderScore(name: String): JavaVarName {
    var javaVarName = JavaVarName()
    var underScore = VarCaseConvertUtils.lowerUnderScore2LowerCamel(name);
    javaVarName.name = underScore;
    return javaVarName;
}
