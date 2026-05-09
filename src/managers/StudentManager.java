package managers;

import exceptions.DuplicateEntityException;
import exceptions.EntityNotFoundException;
import models.Course;
import models.Score;
import models.Student;
import models.Subject;
import services.CourseService;
import services.ScoreService;
import services.StudentService;
import services.SubjectService;
import utils.GpaCalculator;

import java.util.ArrayList;
import java.util.List;

/**
 * Facade trung tâm cho hệ thống quản lý điểm sinh viên.
 * Delegate logic nghiệp vụ sang các Service classes.
 * Sử dụng SQLite (DatabaseManager) cho persistence.
 */
public class StudentManager {
    private StudentService studentService;
    private SubjectService subjectService;
    private CourseService courseService;
    private ScoreService scoreService;
    private DatabaseManager dbManager;

    public StudentManager() {
        dbManager = new DatabaseManager();
        List<Student> students = dbManager.loadStudents();
        List<Subject> subjects = dbManager.loadSubjects();
        List<Course> courses = dbManager.loadCourses();
        List<Score> scores = dbManager.loadScores();

        studentService = new StudentService(students, dbManager);
        subjectService = new SubjectService(subjects, dbManager);
        courseService = new CourseService(courses, dbManager);
        scoreService = new ScoreService(scores, dbManager);
    }

    // ==================== GETTERS ====================
    public List<Student> getStudents() { return studentService.getAll(); }
    public List<Subject> getSubjects() { return subjectService.getAll(); }
    public List<Course> getCourses() { return courseService.getAll(); }
    public List<Score> getScores() { return scoreService.getAll(); }

    // ==================== PERSISTENCE ====================
    /** Dữ liệu đã được lưu tự động qua SQLite. Method này giữ lại để tương thích. */
    public void saveData() {
        // SQLite lưu tự động sau mỗi thao tác, method này giữ cho backward compat
    }

    // ==================== SINH VIÊN ====================
    public void addStudent(Student student) { studentService.add(student); }

    public void updateStudent(String mssv, String newName, String newClassName) {
        studentService.update(mssv, newName, newClassName);
    }

    public boolean removeStudent(String mssv) {
        boolean removed = studentService.remove(mssv);
        if (removed) scoreService.removeByStudent(mssv);
        return removed;
    }

    public Student findStudentByMssv(String mssv) { return studentService.findByMssv(mssv); }
    public List<Student> searchStudents(String keyword) { return studentService.search(keyword); }

    // ==================== MÔN HỌC ====================
    public void addSubject(Subject subject) { subjectService.add(subject); }

    public void updateSubject(String subjectId, String newName, int newCredit) {
        subjectService.update(subjectId, newName, newCredit);
    }

    public boolean removeSubject(String subjectId) {
        boolean removed = subjectService.remove(subjectId);
        if (removed) scoreService.removeBySubject(subjectId);
        return removed;
    }

    public Subject findSubject(String subjectId) { return subjectService.findById(subjectId); }

    // ==================== KHÓA HỌC ====================
    public void addCourse(Course course) { courseService.add(course); }

    public void updateCourse(String courseId, String newYear, int newSemester) {
        courseService.update(courseId, newYear, newSemester);
    }

    public boolean removeCourse(String courseId) {
        boolean removed = courseService.remove(courseId);
        if (removed) scoreService.removeByCourse(courseId);
        return removed;
    }

    public Course findCourse(String courseId) { return courseService.findById(courseId); }

    // ==================== ĐIỂM ====================
    public boolean addScore(String mssv, String subjectId, String courseId, double value) {
        if (findStudentByMssv(mssv) == null)
            throw new EntityNotFoundException("Sinh viên không tồn tại: " + mssv);
        if (findSubject(subjectId) == null)
            throw new EntityNotFoundException("Môn học không tồn tại: " + subjectId);
        if (findCourse(courseId) == null)
            throw new EntityNotFoundException("Khóa học không tồn tại: " + courseId);
        return scoreService.addOrUpdate(mssv, subjectId, courseId, value);
    }

    public boolean removeScore(String mssv, String subjectId, String courseId) {
        return scoreService.remove(mssv, subjectId, courseId);
    }

    public List<Score> getScoresByStudent(String mssv) {
        return scoreService.getByStudent(mssv);
    }

    // ==================== TÍNH TOÁN ====================
    public double calculateAverage10(String mssv, String courseId) {
        return scoreService.calculateAverage10(mssv, courseId, subjectService.getAll());
    }

    public double calculateGPA4(String mssv, String courseId) {
        return scoreService.calculateGPA4(mssv, courseId, subjectService.getAll());
    }

    public String classifyStudent(double gpa4) {
        return GpaCalculator.classifyStudent(gpa4);
    }

    public List<StudentStatistic> getStudentStatistics() {
        List<StudentStatistic> list = new ArrayList<>();
        for (Student s : getStudents()) {
            double avg10 = calculateAverage10(s.getMssv(), null);
            double gpa4 = calculateGPA4(s.getMssv(), null);
            list.add(new StudentStatistic(s, avg10, gpa4, classifyStudent(gpa4)));
        }
        list.sort((a, b) -> Double.compare(b.getGpa4(), a.getGpa4()));
        return list;
    }

    public static class StudentStatistic {
        private final Student student;
        private final double avg10;
        private final double gpa4;
        private final String rank;

        public StudentStatistic(Student student, double avg10, double gpa4, String rank) {
            this.student = student; this.avg10 = avg10; this.gpa4 = gpa4; this.rank = rank;
        }
        public Student getStudent() { return student; }
        public double getAvg10() { return avg10; }
        public double getGpa4() { return gpa4; }
        public String getRank() { return rank; }
    }
}
