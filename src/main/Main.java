package main;

import managers.StudentManager;
import models.Course;
import models.Student;
import models.Subject;
import utils.ValidationUtils;

import java.util.Scanner;

public class Main {
    private static StudentManager manager = new StudentManager();
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Đang lưu dữ liệu trước khi thoát...");
            manager.saveData();
        }));

        while (true) {
            showMenu();
            int choice = ValidationUtils.readInt(scanner, "Chọn chức năng: ", 1, 8);
            switch (choice) {
                case 1:
                    addStudent();
                    break;
                case 2:
                    addSubject();
                    break;
                case 3:
                    addCourse();
                    break;
                case 4:
                    manageScores();
                    break;
                case 5:
                    displayLists();
                    break;
                case 6:
                    search();
                    break;
                case 7:
                    manager.displayStudentStatistics();
                    break;
                case 8:
                    System.out.println("Thoát chương trình. Tạm biệt!");
                    System.exit(0);
            }
            System.out.println("\nNhấn Enter để tiếp tục...");
            scanner.nextLine();
        }
    }

    private static void showMenu() {
        System.out.println("\n=================================");
        System.out.println("  QUẢN LÝ ĐIỂM SINH VIÊN");
        System.out.println("=================================");
        System.out.println("1. Thêm sinh viên");
        System.out.println("2. Thêm môn học");
        System.out.println("3. Thêm khóa học");
        System.out.println("4. Nhập/Sửa/Xóa điểm");
        System.out.println("5. Hiển thị danh sách");
        System.out.println("6. Tìm kiếm sinh viên");
        System.out.println("7. Thống kê xếp loại");
        System.out.println("8. Thoát");
        System.out.println("=================================");
    }

    private static void addStudent() {
        System.out.println("\n--- Thêm Sinh Viên ---");
        String mssv = ValidationUtils.readString(scanner, "Nhập MSSV: ");
        String name = ValidationUtils.readString(scanner, "Nhập Tên: ");
        String dob = ValidationUtils.readDate(scanner, "Nhập Ngày sinh (dd/MM/yyyy): ");
        String className = ValidationUtils.readString(scanner, "Nhập Lớp: ");
        
        try {
            manager.addStudent(new Student(mssv, name, dob, className));
        } catch (Exception e) {
            System.out.println("Lỗi: " + e.getMessage());
        }
    }

    private static void addSubject() {
        System.out.println("\n--- Thêm Môn Học ---");
        String subjectId = ValidationUtils.readString(scanner, "Nhập Mã môn học: ");
        String name = ValidationUtils.readString(scanner, "Nhập Tên môn học: ");
        int credit = ValidationUtils.readInt(scanner, "Nhập Số tín chỉ (1-10): ", 1, 10);
        
        try {
            manager.addSubject(new Subject(subjectId, name, credit));
        } catch (Exception e) {
            System.out.println("Lỗi: " + e.getMessage());
        }
    }

    private static void addCourse() {
        System.out.println("\n--- Thêm Khóa Học ---");
        String courseId = ValidationUtils.readString(scanner, "Nhập Mã khóa học: ");
        String year = ValidationUtils.readString(scanner, "Nhập Năm học (VD: 2024-2025): ");
        int semester = ValidationUtils.readInt(scanner, "Nhập Học kỳ (1-8): ", 1, 8);
        
        try {
            manager.addCourse(new Course(courseId, year, semester));
        } catch (Exception e) {
            System.out.println("Lỗi: " + e.getMessage());
        }
    }

    private static void manageScores() {
        System.out.println("\n--- Quản lý Điểm ---");
        System.out.println("1. Nhập/Sửa điểm");
        System.out.println("2. Xóa điểm");
        int choice = ValidationUtils.readInt(scanner, "Chọn thao tác (1-2): ", 1, 2);
        
        String mssv = ValidationUtils.readString(scanner, "Nhập MSSV: ");
        String subjectId = ValidationUtils.readString(scanner, "Nhập Mã môn học: ");
        String courseId = ValidationUtils.readString(scanner, "Nhập Mã khóa học: ");
        
        if (choice == 1) {
            double score = ValidationUtils.readDouble(scanner, "Nhập điểm (0-10): ", 0, 10);
            try {
                manager.addScore(mssv, subjectId, courseId, score);
            } catch (Exception e) {
                System.out.println("Lỗi: " + e.getMessage());
            }
        } else {
            manager.removeScore(mssv, subjectId, courseId);
        }
    }

    private static void displayLists() {
        System.out.println("\n--- Hiển thị danh sách ---");
        System.out.println("1. Sinh viên");
        System.out.println("2. Môn học");
        System.out.println("3. Khóa học");
        int choice = ValidationUtils.readInt(scanner, "Chọn danh sách (1-3): ", 1, 3);
        
        switch (choice) {
            case 1: manager.displayStudents(); break;
            case 2: manager.displaySubjects(); break;
            case 3: manager.displayCourses(); break;
        }
    }

    private static void search() {
        System.out.println("\n--- Tìm kiếm Sinh viên ---");
        String keyword = ValidationUtils.readString(scanner, "Nhập MSSV hoặc Tên: ");
        Student s = manager.findStudent(keyword);
        if (s != null) {
            System.out.println("Kết quả: " + s);
            System.out.printf("Điểm TB hệ 10 toàn khóa: %.2f\n", manager.calculateAverage10(s.getMssv(), null));
            System.out.printf("GPA hệ 4 toàn khóa: %.2f\n", manager.calculateGPA4(s.getMssv(), null));
            
            System.out.print("Bạn có muốn xem điểm theo khóa học không? (y/n): ");
            String viewCourse = scanner.nextLine().trim();
            if (viewCourse.equalsIgnoreCase("y")) {
                String courseId = ValidationUtils.readString(scanner, "Nhập Mã khóa học: ");
                System.out.printf("Điểm TB hệ 10 khóa %s: %.2f\n", courseId, manager.calculateAverage10(s.getMssv(), courseId));
                System.out.printf("GPA hệ 4 khóa %s: %.2f\n", courseId, manager.calculateGPA4(s.getMssv(), courseId));
            }
        } else {
            System.out.println("Không tìm thấy sinh viên nào khớp với từ khóa.");
        }
    }
}
