package exceptions;

/**
 * Được ném ra khi không tìm thấy thực thể theo mã định danh.
 * Ví dụ: nhập điểm cho sinh viên không tồn tại, tìm môn học không có trong hệ thống.
 */
public class EntityNotFoundException extends RuntimeException {
    public EntityNotFoundException(String message) {
        super(message);
    }
}
