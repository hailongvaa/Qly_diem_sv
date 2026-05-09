package utils;

import models.Score;
import models.Subject;

import java.util.List;

/**
 * Lớp tiện ích chuyên xử lý tính toán GPA và xếp loại học lực.
 * <p>
 * Bảng quy đổi điểm hệ 10 sang hệ 4:
 * <ul>
 *   <li>8.5 - 10.0 → 4.0</li>
 *   <li>8.0 - 8.4  → 3.5</li>
 *   <li>7.0 - 7.9  → 3.0</li>
 *   <li>6.5 - 6.9  → 2.5</li>
 *   <li>5.5 - 6.4  → 2.0</li>
 *   <li>5.0 - 5.4  → 1.5</li>
 *   <li>4.0 - 4.9  → 1.0</li>
 *   <li>0.0 - 3.9  → 0.0</li>
 * </ul>
 */
public class GpaCalculator {

    /**
     * Quy đổi điểm từ hệ 10 sang hệ 4.
     *
     * @param score10 Điểm hệ 10 (0.0 - 10.0)
     * @return Điểm hệ 4 tương ứng (0.0 - 4.0)
     */
    public static double convert10To4(double score10) {
        if (score10 >= 8.5) return 4.0;
        if (score10 >= 8.0) return 3.5;
        if (score10 >= 7.0) return 3.0;
        if (score10 >= 6.5) return 2.5;
        if (score10 >= 5.5) return 2.0;
        if (score10 >= 5.0) return 1.5;
        if (score10 >= 4.0) return 1.0;
        return 0.0;
    }

    /**
     * Tính điểm trung bình hệ 10 có trọng số theo tín chỉ.
     *
     * @param mssv     Mã số sinh viên
     * @param courseId Mã khóa học (null = tính toàn bộ)
     * @param scores   Danh sách tất cả điểm
     * @param subjects Danh sách tất cả môn học (để tra cứu tín chỉ)
     * @return Điểm trung bình hệ 10, hoặc 0 nếu không có dữ liệu
     */
    public static double calculateAverage10(String mssv, String courseId,
                                             List<Score> scores, List<Subject> subjects) {
        double totalScore = 0;
        int totalCredits = 0;

        for (Score s : scores) {
            if (s.getMssv().equalsIgnoreCase(mssv)
                    && (courseId == null || s.getCourseId().equalsIgnoreCase(courseId))) {
                Subject subject = findSubjectById(s.getSubjectId(), subjects);
                if (subject != null) {
                    totalScore += s.getValue() * subject.getCredit();
                    totalCredits += subject.getCredit();
                }
            }
        }
        return totalCredits == 0 ? 0 : totalScore / totalCredits;
    }

    /**
     * Tính GPA hệ 4 có trọng số theo tín chỉ.
     *
     * @param mssv     Mã số sinh viên
     * @param courseId Mã khóa học (null = tính toàn bộ)
     * @param scores   Danh sách tất cả điểm
     * @param subjects Danh sách tất cả môn học (để tra cứu tín chỉ)
     * @return GPA hệ 4, hoặc 0 nếu không có dữ liệu
     */
    public static double calculateGPA4(String mssv, String courseId,
                                        List<Score> scores, List<Subject> subjects) {
        double totalScore4 = 0;
        int totalCredits = 0;

        for (Score s : scores) {
            if (s.getMssv().equalsIgnoreCase(mssv)
                    && (courseId == null || s.getCourseId().equalsIgnoreCase(courseId))) {
                Subject subject = findSubjectById(s.getSubjectId(), subjects);
                if (subject != null) {
                    totalScore4 += convert10To4(s.getValue()) * subject.getCredit();
                    totalCredits += subject.getCredit();
                }
            }
        }
        return totalCredits == 0 ? 0 : totalScore4 / totalCredits;
    }

    /**
     * Xếp loại học lực dựa trên GPA hệ 4.
     * <ul>
     *   <li>≥ 3.6: Xuất sắc</li>
     *   <li>≥ 3.2: Giỏi</li>
     *   <li>≥ 2.5: Khá</li>
     *   <li>≥ 2.0: Trung bình</li>
     *   <li>&lt; 2.0: Yếu</li>
     * </ul>
     *
     * @param gpa4 GPA hệ 4 (0.0 - 4.0)
     * @return Chuỗi xếp loại học lực
     */
    public static String classifyStudent(double gpa4) {
        if (gpa4 >= 3.6) return "Xuất sắc";
        if (gpa4 >= 3.2) return "Giỏi";
        if (gpa4 >= 2.5) return "Khá";
        if (gpa4 >= 2.0) return "Trung bình";
        return "Yếu";
    }

    /**
     * Tra cứu môn học theo mã trong danh sách.
     */
    private static Subject findSubjectById(String subjectId, List<Subject> subjects) {
        for (Subject s : subjects) {
            if (s.getSubjectId().equalsIgnoreCase(subjectId)) {
                return s;
            }
        }
        return null;
    }
}
