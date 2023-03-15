package fit.wenchao.db;

import lombok.Data;

@Data
public class ClassFullName {
    private String fullname;

    public ClassFullName(String fullname) {
        this.fullname = fullname;
    }

    public String toString(){
        return fullname;
    }
}
