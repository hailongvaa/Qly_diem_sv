package test;

import exceptions.DuplicateEntityException;
import exceptions.EntityNotFoundException;
import managers.StudentManager;
import managers.StudentManager.StudentStatistic;
import models.Course;
import models.Score;
import models.Student;
import models.Subject;
import utils.GpaCalculator;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

/**
 * JUnit 5 test suite cho hệ thống quản lý điểm sinh viên.
 * Chạy: java -jar lib/junit-platform-console-standalone-1.10.2.jar --class-path "bin;lib/*" --scan-class-path
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class JUnit5Tests {

    // ==================== GPA Calculator ====================
    @Nested
    @DisplayName("GPA Calculator Tests")
    class GpaTests {
        @Test @DisplayName("10.0 → 4.0")
        void testPerfectScore() { assertEquals(4.0, GpaCalculator.convert10To4(10.0)); }

        @Test @DisplayName("8.5 → 4.0")
        void test85() { assertEquals(4.0, GpaCalculator.convert10To4(8.5)); }

        @Test @DisplayName("7.0 → 3.0")
        void test70() { assertEquals(3.0, GpaCalculator.convert10To4(7.0)); }

        @Test @DisplayName("3.9 → 0.0")
        void testFail() { assertEquals(0.0, GpaCalculator.convert10To4(3.9)); }

        @Test @DisplayName("Classify Xuất sắc (≥3.6)")
        void testExcellent() { assertEquals("Xuất sắc", GpaCalculator.classifyStudent(3.6)); }

        @Test @DisplayName("Classify Giỏi (≥3.2)")
        void testGood() { assertEquals("Giỏi", GpaCalculator.classifyStudent(3.2)); }

        @Test @DisplayName("Classify Yếu (<2.0)")
        void testWeak() { assertEquals("Yếu", GpaCalculator.classifyStudent(1.9)); }
    }

    // ==================== Student CRUD ====================
    @Nested
    @DisplayName("Student CRUD Tests")
    class StudentTests {
        private StudentManager mgr;

        @BeforeEach
        void setup() { mgr = new StudentManager(); }

        @Test @DisplayName("Add + Find student")
        void testAddFind() {
            mgr.addStudent(new Student("JU_SV01", "JUnit Student", "TestCls"));
            Student s = mgr.findStudentByMssv("JU_SV01");
            assertNotNull(s);
            assertEquals("JUnit Student", s.getName());
            mgr.removeStudent("JU_SV01");
        }

        @Test @DisplayName("Duplicate MSSV throws")
        void testDuplicate() {
            mgr.addStudent(new Student("JU_SV02", "A", "C"));
            assertThrows(DuplicateEntityException.class,
                () -> mgr.addStudent(new Student("JU_SV02", "B", "D")));
            mgr.removeStudent("JU_SV02");
        }

        @Test @DisplayName("Update student")
        void testUpdate() {
            mgr.addStudent(new Student("JU_SV03", "Old", "OldCls"));
            mgr.updateStudent("JU_SV03", "New", "NewCls");
            Student s = mgr.findStudentByMssv("JU_SV03");
            assertEquals("New", s.getName());
            assertEquals("NewCls", s.getClassName());
            mgr.removeStudent("JU_SV03");
        }

        @Test @DisplayName("Remove student + cascade scores")
        void testRemove() {
            mgr.addStudent(new Student("JU_SV04", "Del", "C"));
            assertTrue(mgr.removeStudent("JU_SV04"));
            assertNull(mgr.findStudentByMssv("JU_SV04"));
        }

        @Test @DisplayName("Search returns multiple results")
        void testSearch() {
            mgr.addStudent(new Student("JU_SR1", "Nguyen A", "C"));
            mgr.addStudent(new Student("JU_SR2", "Nguyen B", "C"));
            List<Student> results = mgr.searchStudents("Nguyen");
            assertTrue(results.size() >= 2);
            mgr.removeStudent("JU_SR1");
            mgr.removeStudent("JU_SR2");
        }
    }

    // ==================== Score + GPA Integration ====================
    @Nested
    @DisplayName("Score & GPA Integration Tests")
    class ScoreTests {
        private StudentManager mgr;

        @BeforeEach
        void setup() {
            mgr = new StudentManager();
            mgr.addStudent(new Student("JU_SC1", "Score Tester", "C"));
            mgr.addSubject(new Subject("JU_MH1", "Math", 3));
            mgr.addCourse(new Course("JU_KH1", "2024-2025", 1));
        }

        @AfterEach
        void cleanup() {
            mgr.removeStudent("JU_SC1");
            mgr.removeSubject("JU_MH1");
            mgr.removeCourse("JU_KH1");
        }

        @Test @DisplayName("Add new score")
        void testAddScore() {
            assertFalse(mgr.addScore("JU_SC1", "JU_MH1", "JU_KH1", 8.5));
            mgr.removeScore("JU_SC1", "JU_MH1", "JU_KH1");
        }

        @Test @DisplayName("Update existing score")
        void testUpdateScore() {
            mgr.addScore("JU_SC1", "JU_MH1", "JU_KH1", 7.0);
            assertTrue(mgr.addScore("JU_SC1", "JU_MH1", "JU_KH1", 9.0));
            mgr.removeScore("JU_SC1", "JU_MH1", "JU_KH1");
        }

        @Test @DisplayName("Score for nonexistent student throws")
        void testScoreNonExist() {
            assertThrows(EntityNotFoundException.class,
                () -> mgr.addScore("NONEXIST", "JU_MH1", "JU_KH1", 5.0));
        }

        @Test @DisplayName("GPA calculation")
        void testGpa() {
            mgr.addScore("JU_SC1", "JU_MH1", "JU_KH1", 9.0);
            assertEquals(4.0, mgr.calculateGPA4("JU_SC1", null), 0.01);
            assertEquals(9.0, mgr.calculateAverage10("JU_SC1", null), 0.01);
            mgr.removeScore("JU_SC1", "JU_MH1", "JU_KH1");
        }
    }

    // ==================== Equals & HashCode ====================
    @Nested
    @DisplayName("Model equals/hashCode Tests")
    class ModelTests {
        @Test @DisplayName("Student equals by MSSV (case insensitive)")
        void testStudentEquals() {
            Student s1 = new Student("SV01", "A", "C1");
            Student s2 = new Student("sv01", "B", "C2");
            assertEquals(s1, s2);
            assertEquals(s1.hashCode(), s2.hashCode());
        }

        @Test @DisplayName("Score equals by composite key")
        void testScoreEquals() {
            Score sc1 = new Score("SV", "MH", "KH", 8.0);
            Score sc2 = new Score("sv", "mh", "kh", 9.0);
            assertEquals(sc1, sc2);
        }

        @Test @DisplayName("Different keys not equal")
        void testNotEqual() {
            Student s1 = new Student("SV01", "A", "C");
            Student s2 = new Student("SV02", "A", "C");
            assertNotEquals(s1, s2);
        }
    }

    // ==================== Statistics ====================
    @Nested
    @DisplayName("Statistics Tests")
    class StatsTests {
        @Test @DisplayName("Statistics sorted descending by GPA")
        void testStatsSorted() {
            StudentManager mgr = new StudentManager();
            List<StudentStatistic> stats = mgr.getStudentStatistics();
            for (int i = 1; i < stats.size(); i++) {
                assertTrue(stats.get(i).getGpa4() <= stats.get(i - 1).getGpa4(),
                    "Stats should be sorted descending by GPA");
            }
        }
    }
}
