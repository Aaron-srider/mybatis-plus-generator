package fit.wenchao.db;

import java.util.function.Consumer;

public class JavaCode {

    StringBuilder codeBuilder;

    JavaCode() {
        this.codeBuilder = new StringBuilder();
    }

    public static JavaCode writing() {
        return new JavaCode();
    }

    public static JavaCode empty() {
        return new JavaCode();
    }

    public JavaCode packagel(String packName) {
        this.codeBuilder.append("package ").append(packName).append(";\n");
        return this;
    }

    public JavaCode importl(String classname) {
        this.codeBuilder.append("import ").append(classname).append(";\n");
        return this;
    }

    public JavaCode atl(String annotationName) {
        this.codeBuilder.append("@").append(annotationName).append("\n");
        return this;
    }

    public JavaCode publicl() {
        this.codeBuilder.append("public ");
        return this;
    }

    public JavaCode classl(String classname) {
        this.codeBuilder.append("class ").append(classname + " ");
        return this;
    }

    public JavaCode implementsl(String implClassName) {
        this.codeBuilder.append("implements ").append(implClassName + " ");
        return this;
    }

    public JavaCode blockl(JavaCode blockCode) {
        this.codeBuilder.append("{\n");

        this.codeBuilder.append(blockCode.toString());

        this.codeBuilder.append("}\n");
        return this;
    }

    public JavaCode blockl(Consumer<JavaCode> function) {
        this.codeBuilder.append("{\n");
        function.accept(this);
        this.codeBuilder.append("}\n");
        return this;
    }


    public JavaCode blockl() {
        this.codeBuilder.append("{\n");
        this.codeBuilder.append("}\n");
        return this;
    }

    public String toString() {
        return this.codeBuilder.toString();
    }


    public JavaCode interfacel(String interfaceName) {
        this.codeBuilder.append("interface ").append(interfaceName + " ");
        return this;
    }

    public JavaCode extendsl(String classname) {
        this.codeBuilder.append("extends ").append(classname + " ");
        return this;
    }

    public JavaCode write(String str) {
        this.codeBuilder.append(str);
        return this;
    }
}
