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
    public double convert10To4(double score10) {
        if (score10 >= 8.5) return 4.0;
        if (score10 >= 8.0) return 3.5;
        if (score10 >= 7.0) return 3.0;
        if (score10 >= 6.5) return 2.5;
        if (score10 >= 5.5) return 2.0;
        if (score10 >= 5.0) return 1.5;
        if (score10 >= 4.0) return 1.0;
        return 0.0;
    }

    public double calculateAverage10(String mssv, String courseId) {
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

    public double calculateGPA4(String mssv, String courseId) {
        double totalScore4 = 0;
        int totalCredits = 0;

        for (Score s : scores) {
            if (s.getMssv().equalsIgnoreCase(mssv) && (courseId == null || s.getCourseId().equalsIgnoreCase(courseId))) {
                Subject subject = findSubject(s.getSubjectId());
                if (subject != null) {
                    totalScore4 += convert10To4(s.getValue()) * subject.getCredit();
                    totalCredits += subject.getCredit();
                }
            }
        }
        return totalCredits == 0 ? 0 : totalScore4 / totalCredits;
    }

    public String classifyStudent(double gpa4) {
        if (gpa4 >= 3.6) return "Xuất sắc";
        if (gpa4 >= 3.2) return "Giỏi";
        if (gpa4 >= 2.5) return "Khá";
        if (gpa4 >= 2.0) return "Trung bình";
        return "Yếu";
    }

    public void displayStudentStatistics() {
        System.out.println("--- Thống kê Sinh viên ---");
        int xuatSac = 0, gioi = 0, kha = 0, tb = 0, yeu = 0;
        
        List<StudentScore> list = new ArrayList<>();
        for (Student s : students) {
            double avg10 = calculateAverage10(s.getMssv(), null); // ĐTB hệ 10 toàn khóa
            double gpa4 = calculateGPA4(s.getMssv(), null); // GPA hệ 4 toàn khóa
            list.add(new StudentScore(s, avg10, gpa4));
            
            String xepLoai = classifyStudent(gpa4);
            switch (xepLoai) {
                case "Xuất sắc": xuatSac++; break;
                case "Giỏi": gioi++; break;
                case "Khá": kha++; break;
                case "Trung bình": tb++; break;
                case "Yếu": yeu++; break;
            }
        }
        
        // Sắp xếp giảm dần theo GPA hệ 4
        list.sort((a, b) -> Double.compare(b.gpa4, a.gpa4));
        
        System.out.println("Xếp hạng sinh viên:");
        for (StudentScore ss : list) {
            System.out.printf("%s - ĐTB (10): %.2f - GPA (4): %.2f - Xếp loại: %s\n", ss.student.getName(), ss.avg10, ss.gpa4, classifyStudent(ss.gpa4));
        }
        
        System.out.println("\nTổng quan học lực:");
        System.out.println("Xuất sắc: " + xuatSac);
        System.out.println("Giỏi: " + gioi);
        System.out.println("Khá: " + kha);
        System.out.println("Trung bình: " + tb);
        System.out.println("Yếu: " + yeu);
    }
    
    // Lớp phụ trợ để sắp xếp
    private static class StudentScore {
        Student student;
        double avg10;
        double gpa4;
        StudentScore(Student student, double avg10, double gpa4) {
            this.student = student;
            this.avg10 = avg10;
            this.gpa4 = gpa4;
        }
    }
}
