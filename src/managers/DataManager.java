package managers;

import models.Course;
import models.Score;
import models.Student;
import models.Subject;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Xử lý đọc/ghi dữ liệu từ/đến file văn bản.
 * <p>
 * Sử dụng TAB ({@code \t}) làm delimiter chính.
 * Hỗ trợ đọc ngược dữ liệu cũ dùng dấu phẩy ({@code ,}).
 * Có cơ chế backup trước khi ghi để tránh mất dữ liệu.
 */
public class DataManager {

    private static final String DATA_DIR = "data";
    private static final String STUDENTS_FILE = DATA_DIR + "/students.txt";
    private static final String SUBJECTS_FILE = DATA_DIR + "/subjects.txt";
    private static final String COURSES_FILE = DATA_DIR + "/courses.txt";
    private static final String SCORES_FILE = DATA_DIR + "/scores.txt";
    private static final String DELIMITER = "\t";

    public DataManager() {
        File dir = new File(DATA_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    // ==================== SAVE METHODS ====================

    /**
     * Lưu danh sách sinh viên ra file. Tạo backup trước khi ghi.
     *
     * @param students Danh sách sinh viên cần lưu
     */
    public void saveStudents(List<Student> students) {
        saveWithBackup(STUDENTS_FILE, writer -> {
            for (Student s : students) {
                writer.println(s.getMssv() + DELIMITER + s.getName() + DELIMITER + s.getClassName());
            }
        });
    }

    /**
     * Lưu danh sách môn học ra file. Tạo backup trước khi ghi.
     *
     * @param subjects Danh sách môn học cần lưu
     */
    public void saveSubjects(List<Subject> subjects) {
        saveWithBackup(SUBJECTS_FILE, writer -> {
            for (Subject s : subjects) {
                writer.println(s.getSubjectId() + DELIMITER + s.getSubjectName() + DELIMITER + s.getCredit());
            }
        });
    }

    /**
     * Lưu danh sách khóa học ra file. Tạo backup trước khi ghi.
     *
     * @param courses Danh sách khóa học cần lưu
     */
    public void saveCourses(List<Course> courses) {
        saveWithBackup(COURSES_FILE, writer -> {
            for (Course c : courses) {
                writer.println(c.getCourseId() + DELIMITER + c.getYear() + DELIMITER + c.getSemester());
            }
        });
    }

    /**
     * Lưu danh sách điểm ra file. Tạo backup trước khi ghi.
     *
     * @param scores Danh sách điểm cần lưu
     */
    public void saveScores(List<Score> scores) {
        saveWithBackup(SCORES_FILE, writer -> {
            for (Score s : scores) {
                writer.println(s.getMssv() + DELIMITER + s.getSubjectId() + DELIMITER + s.getCourseId() + DELIMITER + s.getValue());
            }
        });
    }

    // ==================== LOAD METHODS ====================

    /**
     * Đọc danh sách sinh viên từ file.
     * Hỗ trợ cả delimiter TAB (mới) và dấu phẩy (cũ).
     * Bỏ qua các dòng bị lỗi và tiếp tục đọc.
     *
     * @return Danh sách sinh viên, hoặc danh sách rỗng nếu file không tồn tại
     */
    public List<Student> loadStudents() {
        List<Student> students = new ArrayList<>();
        File file = new File(STUDENTS_FILE);
        if (!file.exists()) return students;

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"))) {
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                line = line.trim();
                if (line.isEmpty()) continue;

                try {
                    String[] parts = smartSplit(line, 3);
                    if (parts != null) {
                        students.add(new Student(parts[0].trim(), parts[1].trim(), parts[2].trim()));
                    } else {
                        System.err.println("[Cảnh báo] students.txt dòng " + lineNumber + ": Định dạng không hợp lệ, bỏ qua.");
                    }
                } catch (Exception ex) {
                    System.err.println("[Cảnh báo] students.txt dòng " + lineNumber + ": " + ex.getMessage() + ", bỏ qua.");
                }
            }
        } catch (IOException e) {
            System.err.println("Lỗi đọc file students.txt: " + e.getMessage());
        }
        return students;
    }

    /**
     * Đọc danh sách môn học từ file.
     *
     * @return Danh sách môn học, hoặc danh sách rỗng nếu file không tồn tại
     */
    public List<Subject> loadSubjects() {
        List<Subject> subjects = new ArrayList<>();
        File file = new File(SUBJECTS_FILE);
        if (!file.exists()) return subjects;

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"))) {
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                line = line.trim();
                if (line.isEmpty()) continue;

                try {
                    String[] parts = smartSplit(line, 3);
                    if (parts != null) {
                        int credit = Integer.parseInt(parts[2].trim());
                        subjects.add(new Subject(parts[0].trim(), parts[1].trim(), credit));
                    } else {
                        System.err.println("[Cảnh báo] subjects.txt dòng " + lineNumber + ": Định dạng không hợp lệ, bỏ qua.");
                    }
                } catch (NumberFormatException ex) {
                    System.err.println("[Cảnh báo] subjects.txt dòng " + lineNumber + ": Số tín chỉ không hợp lệ, bỏ qua.");
                } catch (Exception ex) {
                    System.err.println("[Cảnh báo] subjects.txt dòng " + lineNumber + ": " + ex.getMessage() + ", bỏ qua.");
                }
            }
        } catch (IOException e) {
            System.err.println("Lỗi đọc file subjects.txt: " + e.getMessage());
        }
        return subjects;
    }

    /**
     * Đọc danh sách khóa học từ file.
     *
     * @return Danh sách khóa học, hoặc danh sách rỗng nếu file không tồn tại
     */
    public List<Course> loadCourses() {
        List<Course> courses = new ArrayList<>();
        File file = new File(COURSES_FILE);
        if (!file.exists()) return courses;

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"))) {
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                line = line.trim();
                if (line.isEmpty()) continue;

                try {
                    String[] parts = smartSplit(line, 3);
                    if (parts != null) {
                        int semester = Integer.parseInt(parts[2].trim());
                        courses.add(new Course(parts[0].trim(), parts[1].trim(), semester));
                    } else {
                        System.err.println("[Cảnh báo] courses.txt dòng " + lineNumber + ": Định dạng không hợp lệ, bỏ qua.");
                    }
                } catch (NumberFormatException ex) {
                    System.err.println("[Cảnh báo] courses.txt dòng " + lineNumber + ": Học kỳ không hợp lệ, bỏ qua.");
                } catch (Exception ex) {
                    System.err.println("[Cảnh báo] courses.txt dòng " + lineNumber + ": " + ex.getMessage() + ", bỏ qua.");
                }
            }
        } catch (IOException e) {
            System.err.println("Lỗi đọc file courses.txt: " + e.getMessage());
        }
        return courses;
    }

    /**
     * Đọc danh sách điểm từ file.
     *
     * @return Danh sách điểm, hoặc danh sách rỗng nếu file không tồn tại
     */
    public List<Score> loadScores() {
        List<Score> scores = new ArrayList<>();
        File file = new File(SCORES_FILE);
        if (!file.exists()) return scores;

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"))) {
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                line = line.trim();
                if (line.isEmpty()) continue;

                try {
                    String[] parts = smartSplit(line, 4);
                    if (parts != null) {
                        double value = Double.parseDouble(parts[3].trim());
                        scores.add(new Score(parts[0].trim(), parts[1].trim(), parts[2].trim(), value));
                    } else {
                        System.err.println("[Cảnh báo] scores.txt dòng " + lineNumber + ": Định dạng không hợp lệ, bỏ qua.");
                    }
                } catch (NumberFormatException ex) {
                    System.err.println("[Cảnh báo] scores.txt dòng " + lineNumber + ": Điểm số không hợp lệ, bỏ qua.");
                } catch (Exception ex) {
                    System.err.println("[Cảnh báo] scores.txt dòng " + lineNumber + ": " + ex.getMessage() + ", bỏ qua.");
                }
            }
        } catch (IOException e) {
            System.err.println("Lỗi đọc file scores.txt: " + e.getMessage());
        }
        return scores;
    }

    // ==================== HELPER METHODS ====================

    /**
     * Tách dòng theo delimiter thông minh: ưu tiên TAB, nếu không đúng số cột thì thử dấu phẩy.
     * Hỗ trợ backward compatibility với dữ liệu cũ dùng dấu phẩy.
     *
     * @param line          Dòng cần tách
     * @param expectedParts Số cột mong đợi
     * @return Mảng chuỗi đã tách, hoặc null nếu không thể tách đúng
     */
    private String[] smartSplit(String line, int expectedParts) {
        // Thử tách bằng TAB trước
        String[] parts = line.split("\t");
        if (parts.length == expectedParts) {
            return parts;
        }
        // Fallback: tách bằng dấu phẩy (backward compatibility)
        parts = line.split(",");
        if (parts.length == expectedParts) {
            return parts;
        }
        // Nếu dấu phẩy cho nhiều hơn expected (do data chứa dấu phẩy),
        // gộp các phần thừa vào phần cuối cùng
        if (parts.length > expectedParts) {
            String[] result = new String[expectedParts];
            for (int i = 0; i < expectedParts - 1; i++) {
                result[i] = parts[i];
            }
            // Gộp phần còn lại
            StringBuilder last = new StringBuilder(parts[expectedParts - 1]);
            for (int i = expectedParts; i < parts.length; i++) {
                last.append(",").append(parts[i]);
            }
            result[expectedParts - 1] = last.toString();
            return result;
        }
        return null;
    }

    /**
     * Ghi file với cơ chế backup: tạo file .bak trước khi ghi,
     * nếu ghi thất bại thì khôi phục từ backup.
     *
     * @param filePath Đường dẫn file cần ghi
     * @param writeAction Hành động ghi (lambda nhận PrintWriter)
     */
    private void saveWithBackup(String filePath, WriteAction writeAction) {
        File file = new File(filePath);
        File backup = new File(filePath + ".bak");

        // Tạo backup nếu file hiện tại tồn tại
        if (file.exists()) {
            if (backup.exists()) {
                backup.delete();
            }
            file.renameTo(backup);
        }

        try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(filePath), "UTF-8"))) {
            writeAction.write(writer);
            // Ghi thành công → xóa backup
            if (backup.exists()) {
                backup.delete();
            }
        } catch (IOException e) {
            System.err.println("Lỗi lưu file " + filePath + ": " + e.getMessage());
            // Khôi phục từ backup nếu ghi thất bại
            if (backup.exists()) {
                File failedFile = new File(filePath);
                if (failedFile.exists()) {
                    failedFile.delete();
                }
                backup.renameTo(failedFile);
                System.err.println("Đã khôi phục dữ liệu từ backup.");
            }
        }
    }

    /**
     * Functional interface cho hành động ghi file.
     */
    @FunctionalInterface
    private interface WriteAction {
        void write(PrintWriter writer) throws IOException;
    }
}
