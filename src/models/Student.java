package models;

import java.util.Objects;

/**
 * Đại diện cho một sinh viên trong hệ thống quản lý điểm.
 * Mỗi sinh viên được định danh duy nhất bởi MSSV (Mã số sinh viên).
 */
public class Student {
    private String mssv;
    private String name;
    private String className;

    /**
     * Khởi tạo một đối tượng Student.
     *
     * @param mssv      Mã số sinh viên (định danh duy nhất)
     * @param name      Họ và tên sinh viên
     * @param className Tên lớp sinh viên đang theo học
     */
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

    /**
     * Hai sinh viên được coi là bằng nhau nếu có cùng MSSV (không phân biệt hoa thường).
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Student student = (Student) o;
        return mssv != null && mssv.equalsIgnoreCase(student.mssv);
    }

    @Override
    public int hashCode() {
        return mssv != null ? mssv.toLowerCase().hashCode() : 0;
    }

    @Override
    public String toString() {
        return String.format("MSSV: %-10s | Tên: %-20s | Lớp: %s", mssv, name, className);
    }
}
