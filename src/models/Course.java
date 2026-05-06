package models;

public class Course {
    private String courseId;
    private String year;
    private int semester;

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

    @Override
    public String toString() {
        return String.format("Mã KH: %-8s | Năm học: %-12s | Học kỳ: %d", courseId, year, semester);
    }
}
