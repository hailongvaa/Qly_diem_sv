package models;

import java.util.Objects;

/**
 * Đại diện cho một khóa học (học kỳ) trong hệ thống.
 * Mỗi khóa học gắn liền với một năm học và học kỳ cụ thể.
 */
public class Course {
    private String courseId;
    private String year;
    private int semester;

    /**
     * Khởi tạo một đối tượng Course.
     *
     * @param courseId  Mã khóa học (định danh duy nhất)
     * @param year     Năm học (VD: "2024-2025")
     * @param semester Học kỳ (1-8)
     */
    public Course(String courseId, String year, int semester) {
        this.courseId = courseId;
        this.year = year;
        this.semester = semester;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public int getSemester() {
        return semester;
    }

    public void setSemester(int semester) {
        this.semester = semester;
    }

    /**
     * Hai khóa học được coi là bằng nhau nếu có cùng mã khóa học.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Course course = (Course) o;
        return courseId != null && courseId.equalsIgnoreCase(course.courseId);
    }

    @Override
    public int hashCode() {
        return courseId != null ? courseId.toLowerCase().hashCode() : 0;
    }

    @Override
    public String toString() {
        return String.format("Mã KH: %-8s | Năm học: %-12s | Học kỳ: %d", courseId, year, semester);
    }
}
