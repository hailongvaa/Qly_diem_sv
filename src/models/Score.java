package models;

public class Score {
    private String mssv;
    private String subjectId;
    private String courseId;
    private double value;

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

    @Override
    public String toString() {
        return String.format("MSSV: %-10s | Mã MH: %-8s | Khóa học: %-8s | Điểm: %.2f", mssv, subjectId, courseId, value);
    }
}
