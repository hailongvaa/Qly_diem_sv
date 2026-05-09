package services;

import exceptions.DuplicateEntityException;
import exceptions.EntityNotFoundException;
import managers.DatabaseManager;
import models.Course;

import java.util.List;

/**
 * Service xử lý logic nghiệp vụ cho Khóa học.
 */
public class CourseService {
    private List<Course> courses;
    private DatabaseManager db;

    public CourseService(List<Course> courses, DatabaseManager db) {
        this.courses = courses;
        this.db = db;
    }

    public List<Course> getAll() { return courses; }

    public void add(Course course) {
        if (findById(course.getCourseId()) != null) {
            throw new DuplicateEntityException("Mã khóa học đã tồn tại: " + course.getCourseId());
        }
        courses.add(course);
        db.saveCourse(course);
    }

    public void update(String courseId, String newYear, int newSemester) {
        Course c = findById(courseId);
        if (c == null) throw new EntityNotFoundException("Không tìm thấy KH: " + courseId);
        if (newYear != null && !newYear.trim().isEmpty()) c.setYear(newYear.trim());
        if (newSemester > 0) c.setSemester(newSemester);
        db.saveCourse(c);
    }

    public boolean remove(String courseId) {
        boolean removed = courses.removeIf(c -> c.getCourseId().equalsIgnoreCase(courseId));
        if (removed) db.deleteCourse(courseId);
        return removed;
    }

    public Course findById(String courseId) {
        return courses.stream()
            .filter(c -> c.getCourseId().equalsIgnoreCase(courseId))
            .findFirst().orElse(null);
    }
}
