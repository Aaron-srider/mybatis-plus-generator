package fit.wenchao.db;

import lombok.Data;

@Data
public class JavaClassName {

    String name;

    JavaPackage javaPackage;

    public static JavaClassName fromLowerUnderScore(JavaPackage javaPackage, String name, String prefix) {
        JavaClassName javaClassName = new JavaClassName();
        javaClassName.javaPackage = javaPackage;
        String underScore = VarCaseConvertUtils.lowerUnderScore2LowerCamel(name);
        if (underScore.length() >= 1) {
            underScore = ("" + underScore.charAt(0)).toUpperCase() + underScore.substring(1);
        }
        javaClassName.setName(underScore + prefix);
        return javaClassName;
    }

    public String getFullName() {
        return javaPackage.getDotSplitName() + "." + name;
    }

    public String toJavaSrcFileName() {
        return name + ".java";
    }

    public String toString() {
        return name;
    }
}
