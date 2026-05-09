package managers;

import models.Course;
import models.Score;
import models.Student;
import models.Subject;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Quản lý dữ liệu sử dụng SQLite thông qua JDBC.
 * Tự động tạo database và các bảng nếu chưa tồn tại.
 * Hỗ trợ migrate dữ liệu từ file .txt cũ sang SQLite.
 */
public class DatabaseManager {

    private static final String DATA_DIR = "data";
    private static final String DB_PATH = DATA_DIR + "/student_management.db";
    private String connectionUrl;

    public DatabaseManager() {
        File dir = new File(DATA_DIR);
        if (!dir.exists()) dir.mkdirs();
        this.connectionUrl = "jdbc:sqlite:" + DB_PATH;
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            System.err.println("SQLite JDBC driver không tìm thấy. Đảm bảo sqlite-jdbc.jar trong classpath.");
        }
        initDatabase();
        migrateFromTextFiles();
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(connectionUrl);
    }

    /**
     * Tạo các bảng nếu chưa tồn tại.
     */
    private void initDatabase() {
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS students (" +
                "  mssv TEXT PRIMARY KEY COLLATE NOCASE," +
                "  name TEXT NOT NULL," +
                "  class_name TEXT NOT NULL" +
                ")"
            );
            stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS subjects (" +
                "  subject_id TEXT PRIMARY KEY COLLATE NOCASE," +
                "  subject_name TEXT NOT NULL," +
                "  credit INTEGER NOT NULL" +
                ")"
            );
            stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS courses (" +
                "  course_id TEXT PRIMARY KEY COLLATE NOCASE," +
                "  year TEXT NOT NULL," +
                "  semester INTEGER NOT NULL" +
                ")"
            );
            stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS scores (" +
                "  mssv TEXT NOT NULL COLLATE NOCASE," +
                "  subject_id TEXT NOT NULL COLLATE NOCASE," +
                "  course_id TEXT NOT NULL COLLATE NOCASE," +
                "  value REAL NOT NULL," +
                "  PRIMARY KEY (mssv, subject_id, course_id)," +
                "  FOREIGN KEY (mssv) REFERENCES students(mssv) ON DELETE CASCADE," +
                "  FOREIGN KEY (subject_id) REFERENCES subjects(subject_id) ON DELETE CASCADE," +
                "  FOREIGN KEY (course_id) REFERENCES courses(course_id) ON DELETE CASCADE" +
                ")"
            );
            // Bật foreign keys
            stmt.executeUpdate("PRAGMA foreign_keys = ON");
        } catch (SQLException e) {
            System.err.println("Lỗi khởi tạo database: " + e.getMessage());
        }
    }

    /**
     * Migrate dữ liệu từ file .txt cũ sang SQLite (chỉ chạy 1 lần).
     * Nếu bảng students đã có dữ liệu, bỏ qua migration.
     */
    private void migrateFromTextFiles() {
        try (Connection conn = getConnection()) {
            // Kiểm tra xem đã có dữ liệu chưa
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM students")) {
                if (rs.next() && rs.getInt(1) > 0) return; // Đã có dữ liệu
            }

            // Đọc từ DataManager cũ
            DataManager oldManager = new DataManager();
            List<Student> students = oldManager.loadStudents();
            List<Subject> subjects = oldManager.loadSubjects();
            List<Course> courses = oldManager.loadCourses();
            List<Score> scores = oldManager.loadScores();

            if (students.isEmpty() && subjects.isEmpty()) return;

            System.out.println("Đang migrate dữ liệu từ .txt sang SQLite...");
            conn.setAutoCommit(false);

            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT OR IGNORE INTO students (mssv, name, class_name) VALUES (?, ?, ?)")) {
                for (Student s : students) {
                    ps.setString(1, s.getMssv());
                    ps.setString(2, s.getName());
                    ps.setString(3, s.getClassName());
                    ps.addBatch();
                }
                ps.executeBatch();
            }

            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT OR IGNORE INTO subjects (subject_id, subject_name, credit) VALUES (?, ?, ?)")) {
                for (Subject s : subjects) {
                    ps.setString(1, s.getSubjectId());
                    ps.setString(2, s.getSubjectName());
                    ps.setInt(3, s.getCredit());
                    ps.addBatch();
                }
                ps.executeBatch();
            }

            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT OR IGNORE INTO courses (course_id, year, semester) VALUES (?, ?, ?)")) {
                for (Course c : courses) {
                    ps.setString(1, c.getCourseId());
                    ps.setString(2, c.getYear());
                    ps.setInt(3, c.getSemester());
                    ps.addBatch();
                }
                ps.executeBatch();
            }

            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT OR IGNORE INTO scores (mssv, subject_id, course_id, value) VALUES (?, ?, ?, ?)")) {
                for (Score s : scores) {
                    ps.setString(1, s.getMssv());
                    ps.setString(2, s.getSubjectId());
                    ps.setString(3, s.getCourseId());
                    ps.setDouble(4, s.getValue());
                    ps.addBatch();
                }
                ps.executeBatch();
            }

            conn.commit();
            System.out.println("Migration hoàn tất! " + students.size() + " SV, " +
                subjects.size() + " MH, " + courses.size() + " KH, " + scores.size() + " điểm.");
        } catch (SQLException e) {
            System.err.println("Lỗi migration: " + e.getMessage());
        }
    }

    // ==================== STUDENTS ====================

    public void saveStudent(Student s) {
        String sql = "INSERT OR REPLACE INTO students (mssv, name, class_name) VALUES (?, ?, ?)";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, s.getMssv());
            ps.setString(2, s.getName());
            ps.setString(3, s.getClassName());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Lỗi lưu sinh viên: " + e.getMessage());
        }
    }

    public void deleteStudent(String mssv) {
        try (Connection conn = getConnection()) {
            conn.createStatement().executeUpdate("PRAGMA foreign_keys = ON");
            try (PreparedStatement ps = conn.prepareStatement("DELETE FROM students WHERE mssv = ?")) {
                ps.setString(1, mssv);
                ps.executeUpdate();
            }
            // Cascade delete scores
            try (PreparedStatement ps = conn.prepareStatement("DELETE FROM scores WHERE mssv = ?")) {
                ps.setString(1, mssv);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            System.err.println("Lỗi xóa sinh viên: " + e.getMessage());
        }
    }

    public List<Student> loadStudents() {
        List<Student> list = new ArrayList<>();
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT mssv, name, class_name FROM students ORDER BY mssv")) {
            while (rs.next()) {
                list.add(new Student(rs.getString("mssv"), rs.getString("name"), rs.getString("class_name")));
            }
        } catch (SQLException e) {
            System.err.println("Lỗi đọc sinh viên: " + e.getMessage());
        }
        return list;
    }

    // ==================== SUBJECTS ====================

    public void saveSubject(Subject s) {
        String sql = "INSERT OR REPLACE INTO subjects (subject_id, subject_name, credit) VALUES (?, ?, ?)";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, s.getSubjectId());
            ps.setString(2, s.getSubjectName());
            ps.setInt(3, s.getCredit());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Lỗi lưu môn học: " + e.getMessage());
        }
    }

    public void deleteSubject(String subjectId) {
        try (Connection conn = getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement("DELETE FROM scores WHERE subject_id = ?")) {
                ps.setString(1, subjectId); ps.executeUpdate();
            }
            try (PreparedStatement ps = conn.prepareStatement("DELETE FROM subjects WHERE subject_id = ?")) {
                ps.setString(1, subjectId); ps.executeUpdate();
            }
        } catch (SQLException e) {
            System.err.println("Lỗi xóa môn học: " + e.getMessage());
        }
    }

    public List<Subject> loadSubjects() {
        List<Subject> list = new ArrayList<>();
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT subject_id, subject_name, credit FROM subjects ORDER BY subject_id")) {
            while (rs.next()) {
                list.add(new Subject(rs.getString("subject_id"), rs.getString("subject_name"), rs.getInt("credit")));
            }
        } catch (SQLException e) {
            System.err.println("Lỗi đọc môn học: " + e.getMessage());
        }
        return list;
    }

    // ==================== COURSES ====================

    public void saveCourse(Course c) {
        String sql = "INSERT OR REPLACE INTO courses (course_id, year, semester) VALUES (?, ?, ?)";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, c.getCourseId());
            ps.setString(2, c.getYear());
            ps.setInt(3, c.getSemester());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Lỗi lưu khóa học: " + e.getMessage());
        }
    }

    public void deleteCourse(String courseId) {
        try (Connection conn = getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement("DELETE FROM scores WHERE course_id = ?")) {
                ps.setString(1, courseId); ps.executeUpdate();
            }
            try (PreparedStatement ps = conn.prepareStatement("DELETE FROM courses WHERE course_id = ?")) {
                ps.setString(1, courseId); ps.executeUpdate();
            }
        } catch (SQLException e) {
            System.err.println("Lỗi xóa khóa học: " + e.getMessage());
        }
    }

    public List<Course> loadCourses() {
        List<Course> list = new ArrayList<>();
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT course_id, year, semester FROM courses ORDER BY course_id")) {
            while (rs.next()) {
                list.add(new Course(rs.getString("course_id"), rs.getString("year"), rs.getInt("semester")));
            }
        } catch (SQLException e) {
            System.err.println("Lỗi đọc khóa học: " + e.getMessage());
        }
        return list;
    }

    // ==================== SCORES ====================

    public void saveScore(Score s) {
        String sql = "INSERT OR REPLACE INTO scores (mssv, subject_id, course_id, value) VALUES (?, ?, ?, ?)";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, s.getMssv());
            ps.setString(2, s.getSubjectId());
            ps.setString(3, s.getCourseId());
            ps.setDouble(4, s.getValue());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Lỗi lưu điểm: " + e.getMessage());
        }
    }

    public void deleteScore(String mssv, String subjectId, String courseId) {
        String sql = "DELETE FROM scores WHERE mssv = ? AND subject_id = ? AND course_id = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, mssv);
            ps.setString(2, subjectId);
            ps.setString(3, courseId);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Lỗi xóa điểm: " + e.getMessage());
        }
    }

    public List<Score> loadScores() {
        List<Score> list = new ArrayList<>();
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT mssv, subject_id, course_id, value FROM scores ORDER BY mssv")) {
            while (rs.next()) {
                list.add(new Score(rs.getString("mssv"), rs.getString("subject_id"),
                    rs.getString("course_id"), rs.getDouble("value")));
            }
        } catch (SQLException e) {
            System.err.println("Lỗi đọc điểm: " + e.getMessage());
        }
        return list;
    }

    // ==================== BULK SAVE ====================

    public void saveAllStudents(List<Student> students) {
        String sql = "INSERT OR REPLACE INTO students (mssv, name, class_name) VALUES (?, ?, ?)";
        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                for (Student s : students) {
                    ps.setString(1, s.getMssv());
                    ps.setString(2, s.getName());
                    ps.setString(3, s.getClassName());
                    ps.addBatch();
                }
                ps.executeBatch();
            }
            conn.commit();
        } catch (SQLException e) {
            System.err.println("Lỗi lưu batch sinh viên: " + e.getMessage());
        }
    }

    public void saveAllSubjects(List<Subject> subjects) {
        String sql = "INSERT OR REPLACE INTO subjects (subject_id, subject_name, credit) VALUES (?, ?, ?)";
        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                for (Subject s : subjects) {
                    ps.setString(1, s.getSubjectId());
                    ps.setString(2, s.getSubjectName());
                    ps.setInt(3, s.getCredit());
                    ps.addBatch();
                }
                ps.executeBatch();
            }
            conn.commit();
        } catch (SQLException e) {
            System.err.println("Lỗi lưu batch môn học: " + e.getMessage());
        }
    }

    public void saveAllCourses(List<Course> courses) {
        String sql = "INSERT OR REPLACE INTO courses (course_id, year, semester) VALUES (?, ?, ?)";
        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                for (Course c : courses) {
                    ps.setString(1, c.getCourseId());
                    ps.setString(2, c.getYear());
                    ps.setInt(3, c.getSemester());
                    ps.addBatch();
                }
                ps.executeBatch();
            }
            conn.commit();
        } catch (SQLException e) {
            System.err.println("Lỗi lưu batch khóa học: " + e.getMessage());
        }
    }

    public void saveAllScores(List<Score> scores) {
        String sql = "INSERT OR REPLACE INTO scores (mssv, subject_id, course_id, value) VALUES (?, ?, ?, ?)";
        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                for (Score s : scores) {
                    ps.setString(1, s.getMssv());
                    ps.setString(2, s.getSubjectId());
                    ps.setString(3, s.getCourseId());
                    ps.setDouble(4, s.getValue());
                    ps.addBatch();
                }
                ps.executeBatch();
            }
            conn.commit();
        } catch (SQLException e) {
            System.err.println("Lỗi lưu batch điểm: " + e.getMessage());
        }
    }
}
