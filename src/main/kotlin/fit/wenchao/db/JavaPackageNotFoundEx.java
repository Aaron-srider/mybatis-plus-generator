package fit.wenchao.db;

public class JavaPackageNotFoundEx extends RuntimeException {
    public JavaPackageNotFoundEx(String packageString) {
        super(packageString);
    }
}
