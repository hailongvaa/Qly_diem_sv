package services;

import exceptions.DuplicateEntityException;
import exceptions.EntityNotFoundException;
import managers.DatabaseManager;
import models.Student;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service xử lý logic nghiệp vụ cho Sinh viên.
 */
public class StudentService {
    private List<Student> students;
    private DatabaseManager db;

    public StudentService(List<Student> students, DatabaseManager db) {
        this.students = students;
        this.db = db;
    }

    public List<Student> getAll() { return students; }

    public void add(Student student) {
        if (findByMssv(student.getMssv()) != null) {
            throw new DuplicateEntityException("MSSV đã tồn tại: " + student.getMssv());
        }
        students.add(student);
        db.saveStudent(student);
    }

    public void update(String mssv, String newName, String newClassName) {
        Student s = findByMssv(mssv);
        if (s == null) throw new EntityNotFoundException("Không tìm thấy SV: " + mssv);
        if (newName != null && !newName.trim().isEmpty()) s.setName(newName.trim());
        if (newClassName != null && !newClassName.trim().isEmpty()) s.setClassName(newClassName.trim());
        db.saveStudent(s);
    }

    public boolean remove(String mssv) {
        boolean removed = students.removeIf(s -> s.getMssv().equalsIgnoreCase(mssv));
        if (removed) db.deleteStudent(mssv);
        return removed;
    }

    public Student findByMssv(String mssv) {
        return students.stream()
            .filter(s -> s.getMssv().equalsIgnoreCase(mssv))
            .findFirst().orElse(null);
    }

    public List<Student> search(String keyword) {
        String lower = keyword.toLowerCase();
        return students.stream()
            .filter(s -> s.getMssv().toLowerCase().contains(lower)
                      || s.getName().toLowerCase().contains(lower))
            .collect(Collectors.toList());
    }
}
