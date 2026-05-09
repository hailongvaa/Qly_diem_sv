package exceptions;

/**
 * Được ném ra khi cố gắng thêm một thực thể có mã định danh đã tồn tại.
 * Ví dụ: thêm sinh viên có MSSV trùng, thêm môn học có mã trùng.
 */
public class DuplicateEntityException extends RuntimeException {
    public DuplicateEntityException(String message) {
        super(message);
    }
}
