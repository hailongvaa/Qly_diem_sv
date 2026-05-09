package models;

import java.util.Objects;

/**
 * Đại diện cho điểm số của một sinh viên trong một môn học thuộc một khóa học.
 * Mỗi bản ghi điểm được xác định bởi bộ ba (MSSV, Mã môn học, Mã khóa học).
 */
public class Score {
    private String mssv;
    private String subjectId;
    private String courseId;
    private double value;

    /**
     * Khởi tạo một đối tượng Score.
     *
     * @param mssv      Mã số sinh viên
     * @param subjectId Mã môn học
     * @param courseId  Mã khóa học
     * @param value     Điểm số (thang 10, từ 0.0 đến 10.0)
     */
    public Score(String mssv, String subjectId, String courseId, double value) {
        this.mssv = mssv;
        this.subjectId = subjectId;
        this.courseId = courseId;
        this.value = value;
    }

    public String getMssv() {
        return mssv;
    }

    public void setMssv(String mssv) {
        this.mssv = mssv;
    }

    public String getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(String subjectId) {
        this.subjectId = subjectId;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    /**
     * Hai bản ghi điểm được coi là bằng nhau nếu có cùng bộ ba (MSSV, Mã MH, Mã KH).
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Score score = (Score) o;
        return (mssv != null && mssv.equalsIgnoreCase(score.mssv))
            && (subjectId != null && subjectId.equalsIgnoreCase(score.subjectId))
            && (courseId != null && courseId.equalsIgnoreCase(score.courseId));
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            mssv != null ? mssv.toLowerCase() : null,
            subjectId != null ? subjectId.toLowerCase() : null,
            courseId != null ? courseId.toLowerCase() : null
        );
    }

    @Override
    public String toString() {
        return String.format("MSSV: %-10s | Mã MH: %-8s | Khóa học: %-8s | Điểm: %.2f", mssv, subjectId, courseId, value);
    }
}
