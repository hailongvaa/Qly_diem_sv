package main;

import exceptions.DuplicateEntityException;
import exceptions.EntityNotFoundException;
import managers.StudentManager;
import managers.StudentManager.StudentStatistic;
import models.Course;
import models.Score;
import models.Student;
import models.Subject;
import utils.ReportExporter;
import utils.ValidationUtils;

import java.util.List;
import java.util.Scanner;

/**
 * Giao diện dòng lệnh (CLI) cho hệ thống quản lý điểm sinh viên.
 */
public class Main {
    private static StudentManager manager = new StudentManager();
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        while (true) {
            showMenu();
            int choice = ValidationUtils.readInt(scanner, "Chọn chức năng: ", 1, 10);
            switch (choice) {
                case 1: manageStudents(); break;
                case 2: manageSubjects(); break;
                case 3: manageCourses(); break;
                case 4: manageScores(); break;
                case 5: displayLists(); break;
                case 6: search(); break;
                case 7: displayStatistics(); break;
                case 8: exportReport(); break;
                case 9: System.out.println("Thoát. Tạm biệt!"); System.exit(0);
            }
            System.out.println("\nNhấn Enter để tiếp tục...");
            scanner.nextLine();
        }
    }

    private static void showMenu() {
        System.out.println("\n╔═══════════════════════════════════╗");
        System.out.println("║   QUẢN LÝ ĐIỂM SINH VIÊN (v2)   ║");
        System.out.println("╠═══════════════════════════════════╣");
        System.out.println("║ 1. Quản lý sinh viên              ║");
        System.out.println("║ 2. Quản lý môn học                ║");
        System.out.println("║ 3. Quản lý khóa học               ║");
        System.out.println("║ 4. Quản lý điểm                   ║");
        System.out.println("║ 5. Hiển thị danh sách              ║");
        System.out.println("║ 6. Tìm kiếm sinh viên             ║");
        System.out.println("║ 7. Thống kê xếp loại              ║");
        System.out.println("║ 8. Xuất báo cáo (Excel/PDF)       ║");
        System.out.println("║ 9. Thoát                           ║");
        System.out.println("╚═══════════════════════════════════╝");
    }

    private static void manageStudents() {
        System.out.println("\n--- Quản lý Sinh Viên ---");
        System.out.println("1. Thêm  2. Sửa  3. Xóa");
        int c = ValidationUtils.readInt(scanner, "Chọn: ", 1, 3);
        if (c == 1) {
            String mssv = ValidationUtils.readString(scanner, "MSSV: ");
            String name = ValidationUtils.readString(scanner, "Tên: ");
            String cls = ValidationUtils.readString(scanner, "Lớp: ");
            try { manager.addStudent(new Student(mssv, name, cls)); System.out.println("✓ Thêm thành công!"); }
            catch (Exception e) { System.out.println("✗ " + e.getMessage()); }
        } else if (c == 2) {
            String mssv = ValidationUtils.readString(scanner, "MSSV cần sửa: ");
            Student s = manager.findStudentByMssv(mssv);
            if (s == null) { System.out.println("✗ Không tìm thấy."); return; }
            System.out.println("Hiện tại: " + s);
            System.out.print("Tên mới (Enter=giữ): "); String n = scanner.nextLine().trim();
            System.out.print("Lớp mới (Enter=giữ): "); String cl = scanner.nextLine().trim();
            try { manager.updateStudent(mssv, n.isEmpty()?null:n, cl.isEmpty()?null:cl); System.out.println("✓ Đã cập nhật!"); }
            catch (Exception e) { System.out.println("✗ " + e.getMessage()); }
        } else {
            String mssv = ValidationUtils.readString(scanner, "MSSV cần xóa: ");
            System.out.println(manager.removeStudent(mssv) ? "✓ Đã xóa." : "✗ Không tìm thấy.");
        }
    }

    private static void manageSubjects() {
        System.out.println("\n--- Quản lý Môn Học ---");
        System.out.println("1. Thêm  2. Sửa  3. Xóa");
        int c = ValidationUtils.readInt(scanner, "Chọn: ", 1, 3);
        if (c == 1) {
            String id = ValidationUtils.readString(scanner, "Mã MH: ");
            String name = ValidationUtils.readString(scanner, "Tên: ");
            int cr = ValidationUtils.readInt(scanner, "Số TC (1-10): ", 1, 10);
            try { manager.addSubject(new Subject(id, name, cr)); System.out.println("✓ Thêm thành công!"); }
            catch (Exception e) { System.out.println("✗ " + e.getMessage()); }
        } else if (c == 2) {
            String id = ValidationUtils.readString(scanner, "Mã MH cần sửa: ");
            if (manager.findSubject(id) == null) { System.out.println("✗ Không tìm thấy."); return; }
            System.out.print("Tên mới (Enter=giữ): "); String n = scanner.nextLine().trim();
            System.out.print("Số TC mới (Enter=giữ): "); String cs = scanner.nextLine().trim();
            int cr = cs.isEmpty() ? -1 : Integer.parseInt(cs);
            try { manager.updateSubject(id, n.isEmpty()?null:n, cr); System.out.println("✓ Đã cập nhật!"); }
            catch (Exception e) { System.out.println("✗ " + e.getMessage()); }
        } else {
            String id = ValidationUtils.readString(scanner, "Mã MH cần xóa: ");
            System.out.println(manager.removeSubject(id) ? "✓ Đã xóa." : "✗ Không tìm thấy.");
        }
    }

    private static void manageCourses() {
        System.out.println("\n--- Quản lý Khóa Học ---");
        System.out.println("1. Thêm  2. Sửa  3. Xóa");
        int c = ValidationUtils.readInt(scanner, "Chọn: ", 1, 3);
        if (c == 1) {
            String id = ValidationUtils.readString(scanner, "Mã KH: ");
            String yr = ValidationUtils.readString(scanner, "Năm học: ");
            int sem = ValidationUtils.readInt(scanner, "Học kỳ (1-8): ", 1, 8);
            try { manager.addCourse(new Course(id, yr, sem)); System.out.println("✓ Thêm thành công!"); }
            catch (Exception e) { System.out.println("✗ " + e.getMessage()); }
        } else if (c == 2) {
            String id = ValidationUtils.readString(scanner, "Mã KH cần sửa: ");
            if (manager.findCourse(id) == null) { System.out.println("✗ Không tìm thấy."); return; }
            System.out.print("Năm mới (Enter=giữ): "); String yr = scanner.nextLine().trim();
            System.out.print("HK mới (Enter=giữ): "); String ss = scanner.nextLine().trim();
            int sem = ss.isEmpty() ? -1 : Integer.parseInt(ss);
            try { manager.updateCourse(id, yr.isEmpty()?null:yr, sem); System.out.println("✓ Đã cập nhật!"); }
            catch (Exception e) { System.out.println("✗ " + e.getMessage()); }
        } else {
            String id = ValidationUtils.readString(scanner, "Mã KH cần xóa: ");
            System.out.println(manager.removeCourse(id) ? "✓ Đã xóa." : "✗ Không tìm thấy.");
        }
    }

    private static void manageScores() {
        System.out.println("\n--- Quản lý Điểm ---");
        System.out.println("1. Nhập/Sửa  2. Xóa");
        int c = ValidationUtils.readInt(scanner, "Chọn: ", 1, 2);
        String mssv = ValidationUtils.readString(scanner, "MSSV: ");
        String sub = ValidationUtils.readString(scanner, "Mã MH: ");
        String crs = ValidationUtils.readString(scanner, "Mã KH: ");
        if (c == 1) {
            double v = ValidationUtils.readDouble(scanner, "Điểm (0-10): ", 0, 10);
            try { boolean u = manager.addScore(mssv, sub, crs, v); System.out.println(u ? "✓ Cập nhật!" : "✓ Thêm mới!"); }
            catch (Exception e) { System.out.println("✗ " + e.getMessage()); }
        } else {
            System.out.println(manager.removeScore(mssv, sub, crs) ? "✓ Đã xóa." : "✗ Không tìm thấy.");
        }
    }

    private static void displayLists() {
        System.out.println("\n1. SV  2. MH  3. KH  4. Điểm");
        int c = ValidationUtils.readInt(scanner, "Chọn: ", 1, 4);
        switch (c) {
            case 1: for (Student s : manager.getStudents()) System.out.println(s); break;
            case 2: for (Subject s : manager.getSubjects()) System.out.println(s); break;
            case 3: for (Course co : manager.getCourses()) System.out.println(co); break;
            case 4: for (Score s : manager.getScores()) System.out.println(s); break;
        }
    }

    private static void search() {
        String kw = ValidationUtils.readString(scanner, "Tìm (MSSV/Tên): ");
        List<Student> results = manager.searchStudents(kw);
        if (results.isEmpty()) { System.out.println("Không tìm thấy."); return; }
        System.out.println("Tìm thấy " + results.size() + " SV:");
        for (Student s : results) {
            double gpa = manager.calculateGPA4(s.getMssv(), null);
            System.out.printf("  %s | GPA: %.2f | %s\n", s, gpa, manager.classifyStudent(gpa));
        }
    }

    private static void displayStatistics() {
        List<StudentStatistic> stats = manager.getStudentStatistics();
        if (stats.isEmpty()) { System.out.println("Chưa có dữ liệu."); return; }
        System.out.println("─────────────────────────────────────────────────────────────");
        for (StudentStatistic ss : stats)
            System.out.printf("%-25s | ĐTB: %5.2f | GPA: %4.2f | %s\n",
                ss.getStudent().getName(), ss.getAvg10(), ss.getGpa4(), ss.getRank());
        System.out.println("─────────────────────────────────────────────────────────────");
    }

    private static void exportReport() {
        System.out.println("\n--- Xuất Báo Cáo ---");
        System.out.println("1. Excel (.xlsx)  2. PDF");
        int c = ValidationUtils.readInt(scanner, "Chọn: ", 1, 2);
        String file = ValidationUtils.readString(scanner, "Tên file (VD: report): ");
        try {
            List<StudentStatistic> stats = manager.getStudentStatistics();
            if (c == 1) {
                ReportExporter.exportToExcel(stats, file + ".xlsx");
                System.out.println("✓ Đã xuất: " + file + ".xlsx");
            } else {
                ReportExporter.exportToPdf(stats, file + ".pdf");
                System.out.println("✓ Đã xuất: " + file + ".pdf");
            }
        } catch (Exception e) {
            System.out.println("✗ Lỗi xuất: " + e.getMessage());
        }
    }
}
