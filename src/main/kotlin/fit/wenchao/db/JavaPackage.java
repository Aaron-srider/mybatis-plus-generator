package fit.wenchao.db;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class JavaPackage {
    private String absolutePackageString;

    private String relativePackageString;

    public static JavaPackage from(String relativePackageString) {
        JavaPackage javaPackage = new JavaPackage();

        File file = new File("");
        String projectPath = file.getAbsolutePath();

        javaPackage.relativePackageString = relativePackageString;
        javaPackage.setPackage(projectPath + "/src/main/kotlin/" + relativePackageString);
        if(!new File(javaPackage.absolutePackageString).exists()) {
            try {
                Files.createDirectories(new File(javaPackage.absolutePackageString).toPath());
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return javaPackage;
    }

    public static void main(String[] args) {
        System.out.println(JavaPackage.from ("fit/wenchao/db"));
    }

    private void setPackage(String packageString) {
        this.absolutePackageString = packageString;
    }

    @Override
    public String toString() {
        return "JavaPackage{" +
                "packageString='" + absolutePackageString + '\'' +
                '}';
    }

    public OutputStream getOutputStream() throws IOException {

        File file = new File(this.absolutePackageString);
        if(file.exists()) {
            return Files.newOutputStream(file.toPath());
        }
        throw new RuntimeException("package not exists");
    }

    public List<String> listSourceFileNames() {
        File file = new File(absolutePackageString);
        String[] list = file.list();
        List<String> sourceFileNames = new ArrayList<>();
        if(list != null) {
            sourceFileNames = Arrays.stream(list).collect(Collectors.toList());
        }
        return sourceFileNames;
    }

    public File getFile() {
        return new File(this.absolutePackageString);
    }

    public String getDotSplitName() {
        return relativePackageString.replace("/", ".");
    }
}
