package fit.wenchao.db;

import lombok.Data;
import lombok.var;

@Data
public class JavaVarName {

    String name;

    public static JavaVarName fromUnderScore(String name) {
        var javaVarName = new JavaVarName();
        String underScore = VarCaseConvertUtils.lowerUnderScore2LowerCamel(name);
        javaVarName.setName(underScore);
        return javaVarName;
    }

    private void setName(String name) {
        this.name = name;
    }
}
