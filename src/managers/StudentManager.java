package managers;

import models.Course;
import models.Score;
import models.Student;
import models.Subject;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class StudentManager {
    private List<Student> students;
    private List<Subject> subjects;
    private List<Course> courses;
    private List<Score> scores;
    private DataManager dataManager;

    public StudentManager() {
        dataManager = new DataManager();
        students = dataManager.loadStudents();
        subjects = dataManager.loadSubjects();
        courses = dataManager.loadCourses();
        scores = dataManager.loadScores();
    }

    public void saveData() {
        dataManager.saveStudents(students);
        dataManager.saveSubjects(subjects);
        dataManager.saveCourses(courses);
        dataManager.saveScores(scores);
    }

    // --- Quản lý Sinh viên ---
    public void addStudent(Student student) throws Exception {
        for (Student s : students) {
            if (s.getMssv().equalsIgnoreCase(student.getMssv())) {
                throw new Exception("MSSV đã tồn tại.");
            }
        }
        students.add(student);
        System.out.println("Thêm sinh viên thành công!");
    }

    public void removeStudent(String mssv) {
        boolean removed = students.removeIf(s -> s.getMssv().equalsIgnoreCase(mssv));
        if (removed) {
            // Xóa điểm liên quan
            scores.removeIf(score -> score.getMssv().equalsIgnoreCase(mssv));
            System.out.println("Đã xóa sinh viên và điểm liên quan.");
        } else {
            System.out.println("Không tìm thấy sinh viên với MSSV này.");
        }
    }

    public Student findStudent(String keyword) {
        for (Student s : students) {
            if (s.getMssv().equalsIgnoreCase(keyword) || s.getName().toLowerCase().contains(keyword.toLowerCase())) {
                return s;
            }
        }
        return null;
    }

    public void displayStudents() {
        if (students.isEmpty()) {
            System.out.println("Danh sách sinh viên trống.");
        } else {
            for (Student s : students) {
                System.out.println(s);
            }
        }
    }

    // --- Quản lý Môn học ---
    public void addSubject(Subject subject) throws Exception {
        for (Subject s : subjects) {
            if (s.getSubjectId().equalsIgnoreCase(subject.getSubjectId())) {
                throw new Exception("Mã môn học đã tồn tại.");
            }
        }
        subjects.add(subject);
        System.out.println("Thêm môn học thành công!");
    }

    public void displaySubjects() {
        if (subjects.isEmpty()) {
            System.out.println("Danh sách môn học trống.");
        } else {
            for (Subject s : subjects) {
                System.out.println(s);
            }
        }
    }

    public Subject findSubject(String subjectId) {
        return subjects.stream().filter(s -> s.getSubjectId().equalsIgnoreCase(subjectId)).findFirst().orElse(null);
    }

    // --- Quản lý Khóa học ---
    public void addCourse(Course course) throws Exception {
        for (Course c : courses) {
            if (c.getCourseId().equalsIgnoreCase(course.getCourseId())) {
                throw new Exception("Mã khóa học đã tồn tại.");
            }
        }
        courses.add(course);
        System.out.println("Thêm khóa học thành công!");
    }

    public void displayCourses() {
        if (courses.isEmpty()) {
            System.out.println("Danh sách khóa học trống.");
        } else {
            for (Course c : courses) {
                System.out.println(c);
            }
        }
    }

    public Course findCourse(String courseId) {
        return courses.stream().filter(c -> c.getCourseId().equalsIgnoreCase(courseId)).findFirst().orElse(null);
    }

    // --- Quản lý Điểm ---
    public void addScore(String mssv, String subjectId, String courseId, double value) throws Exception {
        if (findStudent(mssv) == null) throw new Exception("Sinh viên không tồn tại.");
        if (findSubject(subjectId) == null) throw new Exception("Môn học không tồn tại.");
        if (findCourse(courseId) == null) throw new Exception("Khóa học không tồn tại.");

        for (Score s : scores) {
            if (s.getMssv().equalsIgnoreCase(mssv) && s.getSubjectId().equalsIgnoreCase(subjectId) && s.getCourseId().equalsIgnoreCase(courseId)) {
                // Sửa điểm
                s.setValue(value);
                System.out.println("Cập nhật điểm thành công.");
                return;
            }
        }
        scores.add(new Score(mssv, subjectId, courseId, value));
        System.out.println("Thêm điểm thành công.");
    }

    public void removeScore(String mssv, String subjectId, String courseId) {
        boolean removed = scores.removeIf(s -> s.getMssv().equalsIgnoreCase(mssv) && s.getSubjectId().equalsIgnoreCase(subjectId) && s.getCourseId().equalsIgnoreCase(courseId));
        if (removed) {
            System.out.println("Đã xóa điểm.");
        } else {
            System.out.println("Không tìm thấy điểm.");
        }
    }

    // --- Tính toán & Thống kê ---
    public double calculateGPA(String mssv, String courseId) {
        double totalScore = 0;
        int totalCredits = 0;

        for (Score s : scores) {
            if (s.getMssv().equalsIgnoreCase(mssv) && (courseId == null || s.getCourseId().equalsIgnoreCase(courseId))) {
                Subject subject = findSubject(s.getSubjectId());
                if (subject != null) {
                    totalScore += s.getValue() * subject.getCredit();
                    totalCredits += subject.getCredit();
                }
            }
        }
        return totalCredits == 0 ? 0 : totalScore / totalCredits;
    }

    public String classifyStudent(double gpa) {
        if (gpa >= 8.0) return "Giỏi";
        if (gpa >= 6.5) return "Khá";
        if (gpa >= 5.0) return "Trung bình";
        return "Yếu";
    }

    public void displayStudentStatistics() {
        System.out.println("--- Thống kê Sinh viên ---");
        int gioi = 0, kha = 0, tb = 0, yeu = 0;
        
        List<StudentScore> list = new ArrayList<>();
        for (Student s : students) {
            double gpa = calculateGPA(s.getMssv(), null); // GPA toàn khóa
            list.add(new StudentScore(s, gpa));
            
            String xepLoai = classifyStudent(gpa);
            switch (xepLoai) {
                case "Giỏi": gioi++; break;
                case "Khá": kha++; break;
                case "Trung bình": tb++; break;
                case "Yếu": yeu++; break;
            }
        }
        
        // Sắp xếp giảm dần theo GPA
        list.sort((a, b) -> Double.compare(b.gpa, a.gpa));
        
        System.out.println("Xếp hạng sinh viên:");
        for (StudentScore ss : list) {
            System.out.printf("%s - GPA: %.2f - Xếp loại: %s\n", ss.student.getName(), ss.gpa, classifyStudent(ss.gpa));
        }
        
        System.out.println("\nTổng quan học lực:");
        System.out.println("Giỏi: " + gioi);
        System.out.println("Khá: " + kha);
        System.out.println("Trung bình: " + tb);
        System.out.println("Yếu: " + yeu);
    }
    
    // Lớp phụ trợ để sắp xếp
    private static class StudentScore {
        Student student;
        double gpa;
        StudentScore(Student student, double gpa) {
            this.student = student;
            this.gpa = gpa;
        }
    }
}
