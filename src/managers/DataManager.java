package managers;

import models.Course;
import models.Score;
import models.Student;
import models.Subject;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class DataManager {

    private static final String DATA_DIR = "data";
    private static final String STUDENTS_FILE = DATA_DIR + "/students.txt";
    private static final String SUBJECTS_FILE = DATA_DIR + "/subjects.txt";
    private static final String COURSES_FILE = DATA_DIR + "/courses.txt";
    private static final String SCORES_FILE = DATA_DIR + "/scores.txt";

    public DataManager() {
        File dir = new File(DATA_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    public void saveStudents(List<Student> students) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(STUDENTS_FILE))) {
            for (Student s : students) {
                writer.println(s.getMssv() + "," + s.getName() + "," + s.getClassName());
            }
        } catch (IOException e) {
            System.out.println("Lỗi lưu file students.txt: " + e.getMessage());
        }
    }

    public List<Student> loadStudents() {
        List<Student> students = new ArrayList<>();
        File file = new File(STUDENTS_FILE);
        if (!file.exists()) return students;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 3) {
                    students.add(new Student(parts[0], parts[1], parts[2]));
                }
            }
        } catch (IOException e) {
            System.out.println("Lỗi đọc file students.txt: " + e.getMessage());
        }
        return students;
    }

    public void saveSubjects(List<Subject> subjects) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(SUBJECTS_FILE))) {
            for (Subject s : subjects) {
                writer.println(s.getSubjectId() + "," + s.getSubjectName() + "," + s.getCredit());
            }
        } catch (IOException e) {
            System.out.println("Lỗi lưu file subjects.txt: " + e.getMessage());
        }
    }

    public List<Subject> loadSubjects() {
        List<Subject> subjects = new ArrayList<>();
        File file = new File(SUBJECTS_FILE);
        if (!file.exists()) return subjects;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 3) {
                    subjects.add(new Subject(parts[0], parts[1], Integer.parseInt(parts[2])));
                }
            }
        } catch (IOException e) {
            System.out.println("Lỗi đọc file subjects.txt: " + e.getMessage());
        }
        return subjects;
    }

    public void saveCourses(List<Course> courses) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(COURSES_FILE))) {
            for (Course c : courses) {
                writer.println(c.getCourseId() + "," + c.getYear() + "," + c.getSemester());
            }
        } catch (IOException e) {
            System.out.println("Lỗi lưu file courses.txt: " + e.getMessage());
        }
    }

    public List<Course> loadCourses() {
        List<Course> courses = new ArrayList<>();
        File file = new File(COURSES_FILE);
        if (!file.exists()) return courses;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 3) {
                    courses.add(new Course(parts[0], parts[1], Integer.parseInt(parts[2])));
                }
            }
        } catch (IOException e) {
            System.out.println("Lỗi đọc file courses.txt: " + e.getMessage());
        }
        return courses;
    }

    public void saveScores(List<Score> scores) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(SCORES_FILE))) {
            for (Score s : scores) {
                writer.println(s.getMssv() + "," + s.getSubjectId() + "," + s.getCourseId() + "," + s.getValue());
            }
        } catch (IOException e) {
            System.out.println("Lỗi lưu file scores.txt: " + e.getMessage());
        }
    }

    public List<Score> loadScores() {
        List<Score> scores = new ArrayList<>();
        File file = new File(SCORES_FILE);
        if (!file.exists()) return scores;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 4) {
                    scores.add(new Score(parts[0], parts[1], parts[2], Double.parseDouble(parts[3])));
                }
            }
        } catch (IOException e) {
            System.out.println("Lỗi đọc file scores.txt: " + e.getMessage());
        }
        return scores;
    }
}
