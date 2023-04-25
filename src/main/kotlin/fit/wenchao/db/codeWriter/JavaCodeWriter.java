package fit.wenchao.db.codeWriter;

import java.util.function.Consumer;

public class JavaCodeWriter {

    StringBuilder codeBuilder;

    JavaCodeWriter() {
        this.codeBuilder = new StringBuilder();
    }

    public static JavaCodeWriter writing() {
        return new JavaCodeWriter();
    }

    public static JavaCodeWriter empty() {
        return new JavaCodeWriter();
    }

    public JavaCodeWriter packagel(String packName) {
        this.codeBuilder.append("package ").append(packName).append(";\n");
        return this;
    }

    public JavaCodeWriter importl(String classname) {
        this.codeBuilder.append("import ").append(classname).append(";\n");
        return this;
    }

    public JavaCodeWriter atl(String annotationName) {
        this.codeBuilder.append("@").append(annotationName).append("\n");
        return this;
    }

    public JavaCodeWriter publicl() {
        this.codeBuilder.append("public ");
        return this;
    }

    public JavaCodeWriter classl(String classname) {
        this.codeBuilder.append("class ").append(classname + " ");
        return this;
    }

    public JavaCodeWriter implementsl(String implClassName) {
        this.codeBuilder.append("implements ").append(implClassName + " ");
        return this;
    }

    public JavaCodeWriter blockl(JavaCodeWriter blockCode) {
        this.codeBuilder.append("{\n");

        this.codeBuilder.append(blockCode.toString());

        this.codeBuilder.append("}\n");
        return this;
    }

    public JavaCodeWriter blockl(Consumer<JavaCodeWriter> function) {
        this.codeBuilder.append("{\n");
        function.accept(this);
        this.codeBuilder.append("}\n");
        return this;
    }

    public JavaCodeWriter blockl() {
        this.codeBuilder.append("{\n");
        this.codeBuilder.append("}\n");
        return this;
    }

    public String toString() {
        return this.codeBuilder.toString();
    }

    public JavaCodeWriter interfacel(String interfaceName) {
        this.codeBuilder.append("interface ").append(interfaceName + " ");
        return this;
    }

    public JavaCodeWriter extendsl(String classname) {
        this.codeBuilder.append("extends ").append(classname + " ");
        return this;
    }

    public JavaCodeWriter write(String str) {
        this.codeBuilder.append(str);
        return this;
    }
}
