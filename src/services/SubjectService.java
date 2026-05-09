package services;

import exceptions.DuplicateEntityException;
import exceptions.EntityNotFoundException;
import managers.DatabaseManager;
import models.Subject;

import java.util.List;

/**
 * Service xử lý logic nghiệp vụ cho Môn học.
 */
public class SubjectService {
    private List<Subject> subjects;
    private DatabaseManager db;

    public SubjectService(List<Subject> subjects, DatabaseManager db) {
        this.subjects = subjects;
        this.db = db;
    }

    public List<Subject> getAll() { return subjects; }

    public void add(Subject subject) {
        if (findById(subject.getSubjectId()) != null) {
            throw new DuplicateEntityException("Mã môn học đã tồn tại: " + subject.getSubjectId());
        }
        subjects.add(subject);
        db.saveSubject(subject);
    }

    public void update(String subjectId, String newName, int newCredit) {
        Subject s = findById(subjectId);
        if (s == null) throw new EntityNotFoundException("Không tìm thấy MH: " + subjectId);
        if (newName != null && !newName.trim().isEmpty()) s.setSubjectName(newName.trim());
        if (newCredit > 0) s.setCredit(newCredit);
        db.saveSubject(s);
    }

    public boolean remove(String subjectId) {
        boolean removed = subjects.removeIf(s -> s.getSubjectId().equalsIgnoreCase(subjectId));
        if (removed) db.deleteSubject(subjectId);
        return removed;
    }

    public Subject findById(String subjectId) {
        return subjects.stream()
            .filter(s -> s.getSubjectId().equalsIgnoreCase(subjectId))
            .findFirst().orElse(null);
    }
}
