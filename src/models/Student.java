package models;

public class Student {
    private String mssv;
    private String name;
    private String className;

    public Student(String mssv, String name, String className) {
        this.mssv = mssv;
        this.name = name;
        this.className = className;
    }

    public String getMssv() {
        return mssv;
    }

    public void setMssv(String mssv) {
        this.mssv = mssv;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    @Override
    public String toString() {
        return String.format("MSSV: %-10s | Tên: %-20s | Lớp: %s", mssv, name, className);
    }
}
