package models;

import java.util.Objects;

/**
 * Đại diện cho một môn học trong hệ thống.
 * Mỗi môn học được định danh bởi mã môn học (subjectId) và có số tín chỉ tương ứng.
 */
public class Subject {
    private String subjectId;
    private String subjectName;
    private int credit;

    /**
     * Khởi tạo một đối tượng Subject.
     *
     * @param subjectId   Mã môn học (định danh duy nhất)
     * @param subjectName Tên môn học
     * @param credit      Số tín chỉ (thường từ 1-10)
     */
    public Subject(String subjectId, String subjectName, int credit) {
        this.subjectId = subjectId;
        this.subjectName = subjectName;
        this.credit = credit;
    }

    public String getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(String subjectId) {
        this.subjectId = subjectId;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public int getCredit() {
        return credit;
    }

    public void setCredit(int credit) {
        this.credit = credit;
    }

    /**
     * Hai môn học được coi là bằng nhau nếu có cùng mã môn học.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Subject subject = (Subject) o;
        return subjectId != null && subjectId.equalsIgnoreCase(subject.subjectId);
    }

    @Override
    public int hashCode() {
        return subjectId != null ? subjectId.toLowerCase().hashCode() : 0;
    }

    @Override
    public String toString() {
        return String.format("Mã MH: %-8s | Tên môn: %-25s | Số TC: %d", subjectId, subjectName, credit);
    }
}
