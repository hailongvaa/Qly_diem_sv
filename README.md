# Hướng dẫn Ứng dụng Quản lý Điểm Sinh Viên

Ứng dụng quản lý điểm sinh viên đã được xây dựng hoàn tất bằng ngôn ngữ Java theo chuẩn OOP, bao gồm các chức năng quản lý, thêm/sửa/xóa, tính toán GPA và lưu trữ file tự động.

## 1. Cấu trúc Dự án

Mã nguồn được chia làm các package như sau:

*   **`models`**: Chứa các thực thể chính như `Student` (Sinh viên), `Subject` (Môn học), `Course` (Khóa học), và `Score` (Điểm số liên kết giữa 3 thực thể trên).
*   **`managers`**:
    *   `StudentManager`: Quản lý logic nghiệp vụ, tính điểm, thống kê.
    *   `DataManager`: Xử lý I/O, tự động tạo thư mục `data/` và ghi đè nội dung ra các file TXT để lưu trữ dữ liệu vĩnh viễn (Data Persistence).
*   **`utils`**:
    *   `ValidationUtils`: Đóng gói các hàm kiểm tra nhập liệu từ bàn phím, đảm bảo người dùng nhập đúng định dạng (chữ, số thực, ngày sinh) và không bị crash do lỗi ngoại lệ.
*   **`main`**:
    *   `Main`: Chứa hàm chạy chính và Menu giao diện người dùng CLI.

## 2. Các Tính năng Chính

*   **Toàn vẹn Dữ liệu (Validation)**: Không cho phép nhập trùng `MSSV`, `Mã MH`, `Mã KH`. Yêu cầu nhập đúng khoảng điểm (0-10) và số tín chỉ (1-10).
*   **Xử lý Ngoại lệ (Exception Handling)**: Nếu nhập sai kiểu (chữ thay vì số), chương trình sẽ hiển thị lỗi thay vì thoát (crash).
*   **Quản lý Điểm liên kết**: Cho phép nhập, sửa hoặc xóa điểm của 1 sinh viên trong 1 khóa học và môn học nhất định.
*   **Tính toán và Thống kê**: Tự động tính GPA toàn khóa theo trọng số tín chỉ và phân loại Học lực (Giỏi, Khá, Trung bình, Yếu), sắp xếp thứ hạng từ cao xuống thấp.
*   **Tự động Lưu trữ (Shutdown Hook)**: Dữ liệu được đọc lên khi mở app và được lưu tự động ra file khi người dùng bấm thoát (chức năng 8) nhờ `Runtime.getRuntime().addShutdownHook`.

## 3. Cách Biên dịch và Chạy

**Lưu ý:** Bạn cần biên dịch với cờ `-encoding UTF-8` để hiển thị đúng Tiếng Việt có dấu trên Terminal.

Mở Terminal tại thư mục `d:\qly_diem_sv` và chạy các lệnh sau:

### Biên dịch (Compile)
```powershell
mkdir bin
javac -encoding UTF-8 -d bin -sourcepath src src/main/Main.java
```

### Chạy ứng dụng (Run)
```powershell
java -cp bin main.Main
```

Sau khi chạy, giao diện sẽ xuất hiện với 8 tùy chọn. Hãy thử thêm một vài dữ liệu sau đó bấm phím `8` để thoát và xem dữ liệu được lưu trong thư mục `data/` sinh ra tại thư mục gốc của project.
