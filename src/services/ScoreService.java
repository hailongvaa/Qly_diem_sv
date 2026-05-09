package services;

import exceptions.EntityNotFoundException;
import managers.DatabaseManager;
import models.Score;
import models.Subject;
import utils.GpaCalculator;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service xử lý logic nghiệp vụ cho Điểm số, tính GPA và thống kê.
 */
public class ScoreService {
    private List<Score> scores;
    private DatabaseManager db;

    public ScoreService(List<Score> scores, DatabaseManager db) {
        this.scores = scores;
        this.db = db;
    }

    public List<Score> getAll() { return scores; }

    /**
     * Thêm hoặc cập nhật điểm.
     * @return true nếu cập nhật, false nếu thêm mới
     */
    public boolean addOrUpdate(String mssv, String subjectId, String courseId, double value) {
        for (Score s : scores) {
            if (s.getMssv().equalsIgnoreCase(mssv)
                && s.getSubjectId().equalsIgnoreCase(subjectId)
                && s.getCourseId().equalsIgnoreCase(courseId)) {
                s.setValue(value);
                db.saveScore(s);
                return true;
            }
        }
        Score newScore = new Score(mssv, subjectId, courseId, value);
        scores.add(newScore);
        db.saveScore(newScore);
        return false;
    }

    public boolean remove(String mssv, String subjectId, String courseId) {
        boolean removed = scores.removeIf(s ->
            s.getMssv().equalsIgnoreCase(mssv)
            && s.getSubjectId().equalsIgnoreCase(subjectId)
            && s.getCourseId().equalsIgnoreCase(courseId));
        if (removed) db.deleteScore(mssv, subjectId, courseId);
        return removed;
    }

    public void removeByStudent(String mssv) {
        scores.removeIf(s -> s.getMssv().equalsIgnoreCase(mssv));
    }

    public void removeBySubject(String subjectId) {
        scores.removeIf(s -> s.getSubjectId().equalsIgnoreCase(subjectId));
    }

    public void removeByCourse(String courseId) {
        scores.removeIf(s -> s.getCourseId().equalsIgnoreCase(courseId));
    }

    public List<Score> getByStudent(String mssv) {
        return scores.stream()
            .filter(s -> s.getMssv().equalsIgnoreCase(mssv))
            .collect(Collectors.toList());
    }

    public double calculateAverage10(String mssv, String courseId, List<Subject> subjects) {
        return GpaCalculator.calculateAverage10(mssv, courseId, scores, subjects);
    }

    public double calculateGPA4(String mssv, String courseId, List<Subject> subjects) {
        return GpaCalculator.calculateGPA4(mssv, courseId, scores, subjects);
    }
}
