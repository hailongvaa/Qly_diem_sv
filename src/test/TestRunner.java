package test;

import exceptions.DuplicateEntityException;
import exceptions.EntityNotFoundException;
import managers.StudentManager;
import models.Course;
import models.Score;
import models.Student;
import models.Subject;
import utils.GpaCalculator;

import java.util.List;

/**
 * Test tự động không cần JUnit.
 * Chạy: java -cp bin test.TestRunner
 */
public class TestRunner {
    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("  RUNNING TESTS");
        System.out.println("========================================\n");

        testGpaCalculator();
        testStudentCRUD();
        testSubjectCRUD();
        testCourseCRUD();
        testScoreCRUD();
        testSearchStudents();
        testStatistics();
        testEqualsHashCode();

        System.out.println("\n========================================");
        System.out.printf("  RESULTS: %d passed, %d failed, %d total\n", passed, failed, passed + failed);
        System.out.println("========================================");

        if (failed > 0) {
            System.exit(1);
        }
    }

    // ==================== GPA TESTS ====================

    private static void testGpaCalculator() {
        section("GpaCalculator");

        assertEqual("10.0 -> 4.0", 4.0, GpaCalculator.convert10To4(10.0));
        assertEqual("8.5 -> 4.0", 4.0, GpaCalculator.convert10To4(8.5));
        assertEqual("8.0 -> 3.5", 3.5, GpaCalculator.convert10To4(8.0));
        assertEqual("7.0 -> 3.0", 3.0, GpaCalculator.convert10To4(7.0));
        assertEqual("6.5 -> 2.5", 2.5, GpaCalculator.convert10To4(6.5));
        assertEqual("5.5 -> 2.0", 2.0, GpaCalculator.convert10To4(5.5));
        assertEqual("5.0 -> 1.5", 1.5, GpaCalculator.convert10To4(5.0));
        assertEqual("4.0 -> 1.0", 1.0, GpaCalculator.convert10To4(4.0));
        assertEqual("3.9 -> 0.0", 0.0, GpaCalculator.convert10To4(3.9));

        assertEqual("classify 3.6", "Xuất sắc", GpaCalculator.classifyStudent(3.6));
        assertEqual("classify 3.2", "Giỏi", GpaCalculator.classifyStudent(3.2));
        assertEqual("classify 2.5", "Khá", GpaCalculator.classifyStudent(2.5));
        assertEqual("classify 2.0", "Trung bình", GpaCalculator.classifyStudent(2.0));
        assertEqual("classify 1.9", "Yếu", GpaCalculator.classifyStudent(1.9));
    }

    // ==================== STUDENT CRUD ====================

    private static void testStudentCRUD() {
        section("Student CRUD");
        StudentManager mgr = new StudentManager();
        int initialSize = mgr.getStudents().size();

        // Add
        mgr.addStudent(new Student("TEST001", "Nguyen Van Test", "TestClass"));
        assertEqual("add student", initialSize + 1, mgr.getStudents().size());

        // Find by MSSV
        Student found = mgr.findStudentByMssv("TEST001");
        assertNotNull("find by mssv", found);
        assertEqual("found name", "Nguyen Van Test", found.getName());

        // Update
        mgr.updateStudent("TEST001", "Tran Van Updated", null);
        found = mgr.findStudentByMssv("TEST001");
        assertEqual("update name", "Tran Van Updated", found.getName());
        assertEqual("class unchanged", "TestClass", found.getClassName());

        // Duplicate
        boolean threwDuplicate = false;
        try {
            mgr.addStudent(new Student("TEST001", "Dup", "Dup"));
        } catch (DuplicateEntityException e) {
            threwDuplicate = true;
        }
        assertTrue("duplicate throws exception", threwDuplicate);

        // Remove
        boolean removed = mgr.removeStudent("TEST001");
        assertTrue("remove returns true", removed);
        assertNull("removed student is null", mgr.findStudentByMssv("TEST001"));

        // Remove non-existent
        assertFalse("remove non-existent", mgr.removeStudent("NONEXIST"));
    }

    // ==================== SUBJECT CRUD ====================

    private static void testSubjectCRUD() {
        section("Subject CRUD");
        StudentManager mgr = new StudentManager();

        mgr.addSubject(new Subject("TSUB01", "Test Subject", 3));
        Subject found = mgr.findSubject("TSUB01");
        assertNotNull("add+find subject", found);
        assertEqual("subject name", "Test Subject", found.getSubjectName());
        assertEqual("subject credit", 3, found.getCredit());

        // Update
        mgr.updateSubject("TSUB01", "Updated Subject", 4);
        found = mgr.findSubject("TSUB01");
        assertEqual("updated name", "Updated Subject", found.getSubjectName());
        assertEqual("updated credit", 4, found.getCredit());

        // Remove
        assertTrue("remove subject", mgr.removeSubject("TSUB01"));
        assertNull("removed subject null", mgr.findSubject("TSUB01"));
    }

    // ==================== COURSE CRUD ====================

    private static void testCourseCRUD() {
        section("Course CRUD");
        StudentManager mgr = new StudentManager();

        mgr.addCourse(new Course("TCRS01", "2024-2025", 1));
        Course found = mgr.findCourse("TCRS01");
        assertNotNull("add+find course", found);
        assertEqual("course year", "2024-2025", found.getYear());

        // Update
        mgr.updateCourse("TCRS01", "2025-2026", 2);
        found = mgr.findCourse("TCRS01");
        assertEqual("updated year", "2025-2026", found.getYear());
        assertEqual("updated semester", 2, found.getSemester());

        // Remove
        assertTrue("remove course", mgr.removeCourse("TCRS01"));
    }

    // ==================== SCORE CRUD ====================

    private static void testScoreCRUD() {
        section("Score CRUD");
        StudentManager mgr = new StudentManager();

        mgr.addStudent(new Student("TSCR01", "Score Student", "CLS"));
        mgr.addSubject(new Subject("TSCS01", "Score Subject", 3));
        mgr.addCourse(new Course("TSCC01", "2024-2025", 1));

        // Add score
        boolean updated = mgr.addScore("TSCR01", "TSCS01", "TSCC01", 8.5);
        assertFalse("new score returns false", updated);

        // Update score
        updated = mgr.addScore("TSCR01", "TSCS01", "TSCC01", 9.0);
        assertTrue("update score returns true", updated);

        // Score for non-existent student
        boolean threw = false;
        try {
            mgr.addScore("NONEXIST", "TSCS01", "TSCC01", 5.0);
        } catch (EntityNotFoundException e) {
            threw = true;
        }
        assertTrue("score for nonexist student throws", threw);

        // Remove score
        assertTrue("remove score", mgr.removeScore("TSCR01", "TSCS01", "TSCC01"));
        assertFalse("remove again returns false", mgr.removeScore("TSCR01", "TSCS01", "TSCC01"));

        // Cleanup
        mgr.removeStudent("TSCR01");
        mgr.removeSubject("TSCS01");
        mgr.removeCourse("TSCC01");
    }

    // ==================== SEARCH ====================

    private static void testSearchStudents() {
        section("Search Students");
        StudentManager mgr = new StudentManager();

        mgr.addStudent(new Student("TSRCH1", "Nguyen Van A", "CLS1"));
        mgr.addStudent(new Student("TSRCH2", "Nguyen Van B", "CLS1"));
        mgr.addStudent(new Student("TSRCH3", "Tran Thi C", "CLS2"));

        // Search by partial name
        List<Student> results = mgr.searchStudents("Nguyen");
        assertTrue("search Nguyen >= 2 results", results.size() >= 2);

        // Search by MSSV
        results = mgr.searchStudents("TSRCH1");
        assertTrue("search by mssv", results.size() >= 1);
        assertEqual("search mssv match", "TSRCH1", results.get(0).getMssv());

        // Search with no results
        results = mgr.searchStudents("ZZZZZZNOTEXIST");
        assertEqual("no results", 0, results.size());

        // Cleanup
        mgr.removeStudent("TSRCH1");
        mgr.removeStudent("TSRCH2");
        mgr.removeStudent("TSRCH3");
    }

    // ==================== STATISTICS ====================

    private static void testStatistics() {
        section("Statistics");
        StudentManager mgr = new StudentManager();

        mgr.addStudent(new Student("TSTAT1", "Student A", "CLS"));
        mgr.addStudent(new Student("TSTAT2", "Student B", "CLS"));
        mgr.addSubject(new Subject("TSTM1", "Math", 3));
        mgr.addCourse(new Course("TSTC1", "2024-2025", 1));

        mgr.addScore("TSTAT1", "TSTM1", "TSTC1", 9.0);
        mgr.addScore("TSTAT2", "TSTM1", "TSTC1", 5.0);

        List<StudentManager.StudentStatistic> stats = mgr.getStudentStatistics();
        assertTrue("stats not empty", stats.size() >= 2);

        // Should be sorted descending by GPA
        boolean sorted = true;
        for (int i = 1; i < stats.size(); i++) {
            if (stats.get(i).getGpa4() > stats.get(i - 1).getGpa4()) {
                sorted = false;
                break;
            }
        }
        assertTrue("stats sorted descending", sorted);

        // GPA calculation
        double gpa = mgr.calculateGPA4("TSTAT1", null);
        assertEqual("TSTAT1 GPA=4.0", 4.0, gpa);

        double avg = mgr.calculateAverage10("TSTAT2", null);
        assertEqual("TSTAT2 avg=5.0", 5.0, avg);

        // Cleanup
        mgr.removeStudent("TSTAT1");
        mgr.removeStudent("TSTAT2");
        mgr.removeSubject("TSTM1");
        mgr.removeCourse("TSTC1");
    }

    // ==================== EQUALS & HASHCODE ====================

    private static void testEqualsHashCode() {
        section("equals() & hashCode()");

        Student s1 = new Student("SV001", "A", "CLS");
        Student s2 = new Student("sv001", "B", "CLS2");
        Student s3 = new Student("SV002", "C", "CLS");
        assertTrue("same mssv equals", s1.equals(s2));
        assertFalse("diff mssv not equals", s1.equals(s3));
        assertEqual("same mssv same hash", s1.hashCode(), s2.hashCode());

        Subject sub1 = new Subject("MH01", "A", 3);
        Subject sub2 = new Subject("mh01", "B", 4);
        assertTrue("subject equals ignore case", sub1.equals(sub2));

        Course c1 = new Course("KH01", "2024", 1);
        Course c2 = new Course("kh01", "2025", 2);
        assertTrue("course equals ignore case", c1.equals(c2));

        Score sc1 = new Score("SV01", "MH01", "KH01", 8.0);
        Score sc2 = new Score("sv01", "mh01", "kh01", 9.0);
        Score sc3 = new Score("SV01", "MH02", "KH01", 8.0);
        assertTrue("score equals composite key", sc1.equals(sc2));
        assertFalse("score diff key not equals", sc1.equals(sc3));
    }

    // ==================== ASSERTION HELPERS ====================

    private static void section(String name) {
        System.out.println("--- " + name + " ---");
    }

    private static void assertEqual(String testName, Object expected, Object actual) {
        if (expected.equals(actual)) {
            System.out.println("  PASS: " + testName);
            passed++;
        } else {
            System.out.println("  FAIL: " + testName + " | expected: " + expected + " | actual: " + actual);
            failed++;
        }
    }

    private static void assertEqual(String testName, double expected, double actual) {
        if (Math.abs(expected - actual) < 0.001) {
            System.out.println("  PASS: " + testName);
            passed++;
        } else {
            System.out.println("  FAIL: " + testName + " | expected: " + expected + " | actual: " + actual);
            failed++;
        }
    }

    private static void assertTrue(String testName, boolean condition) {
        if (condition) {
            System.out.println("  PASS: " + testName);
            passed++;
        } else {
            System.out.println("  FAIL: " + testName + " | expected: true | actual: false");
            failed++;
        }
    }

    private static void assertFalse(String testName, boolean condition) {
        assertTrue(testName, !condition);
    }

    private static void assertNotNull(String testName, Object obj) {
        assertTrue(testName + " (not null)", obj != null);
    }

    private static void assertNull(String testName, Object obj) {
        assertTrue(testName + " (is null)", obj == null);
    }
}
