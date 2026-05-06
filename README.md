# 🎓 Hệ Thống Quản Lý Điểm Sinh Viên (Java CLI)

![Java](https://img.shields.io/badge/Language-Java-orange?style=for-the-badge&logo=java)
![Status](https://img.shields.io/badge/Status-Completed-success?style=for-the-badge)
![License](https://img.shields.io/badge/License-MIT-blue?style=for-the-badge)

Một giải pháp quản lý học tập toàn diện dành cho môi trường giáo dục, được tối ưu hóa cho hiệu năng và độ tin cậy của dữ liệu trên giao diện dòng lệnh (CLI).

---

## 🌟 Giới thiệu

**Hệ Thống Quản Lý Điểm Sinh Viên** là một ứng dụng Java được thiết kế theo mô hình hướng đối tượng (OOP) hiện đại. Ứng dụng cung cấp một quy trình quản lý khép kín từ việc lưu trữ thông tin sinh viên, môn học đến việc tính toán GPA và xếp loại học lực tự động. Với cơ chế lưu trữ dữ liệu thông minh, chương trình đảm bảo tính toàn vẹn của thông tin ngay cả khi xảy ra các tình huống ngắt đột ngột.

## 🚀 Các Tính Năng Nổi Bật

| Tính năng | Mô tả |
| :--- | :--- |
| **Quản lý Thực thể** | Quản lý linh hoạt danh sách Sinh viên, Môn học và Khóa học với mã định danh duy nhất. |
| **Quản lý Điểm số** | Cho phép nhập mới, cập nhật hoặc xóa điểm số theo mối quan hệ đa chiều (Sinh viên - Môn học - Khóa học). |
| **Tính toán GPA** | Tự động tính điểm trung bình hệ 10 và GPA hệ 4 theo trọng số tín chỉ của từng môn học. |
| **Xếp loại & Thống kê** | Phân loại học lực (Giỏi, Khá, Trung bình, Yếu) và báo cáo tỷ lệ phần trăm trực quan. |
| **Tìm kiếm Thông minh** | Truy xuất nhanh thông tin sinh viên và bảng điểm chi tiết chỉ với MSSV hoặc một phần tên. |
| **Lưu trữ Tự động** | Cơ chế `Shutdown Hook` giúp tự động đồng bộ dữ liệu vào tệp `.txt` khi thoát ứng dụng. |

## 🛠 Điểm Nhấn Kỹ Thuật

*   **Kiến trúc Module:** Dự án được chia nhỏ thành các package chuyên biệt (`models`, `managers`, `utils`), giúp dễ dàng bảo trì và mở rộng.
*   **Validation Toàn diện:** Lớp `ValidationUtils` đảm bảo mọi dữ liệu nhập từ bàn phím (số thực, số nguyên, định dạng chuỗi) đều được kiểm soát chặt chẽ, loại bỏ hoàn toàn các lỗi gây treo chương trình.
*   **Persistence Layer:** Sử dụng `DataManager` để xử lý I/O tệp tin hiệu quả, hỗ trợ đọc/ghi dữ liệu theo định dạng cấu trúc, giúp dữ liệu luôn tồn tại bền vững giữa các lần chạy.
*   **Hiển thị Tiếng Việt:** Hỗ trợ đầy đủ bảng mã UTF-8 cho tên sinh viên và môn học, mang lại trải nghiệm người dùng tốt nhất.

## 📂 Cấu Trúc Mã Nguồn

```text
qly_diem_sv/
├── data/               # Lưu trữ cơ sở dữ liệu dưới dạng tệp văn bản (.txt)
│   ├── students.txt    # Danh sách sinh viên
│   ├── subjects.txt    # Danh mục môn học
│   ├── courses.txt     # Danh sách khóa học
│   └── scores.txt      # Bảng điểm tổng hợp
├── src/
│   ├── models/         # Các lớp thực thể: Student, Subject, Course, Score
│   ├── managers/       # Xử lý logic: StudentManager, DataManager
│   ├── utils/          # Công cụ hỗ trợ: ValidationUtils
│   └── main/           # Lớp khởi chạy: Main.java
├── bin/                # Thư mục chứa tệp thực thi (.class)
└── README.md           # Tài liệu hướng dẫn (Tệp tin hiện tại)
```

## 💻 Hướng Dẫn Cài Đặt & Sử Dụng

### 1. Chuẩn bị
*   Đảm bảo máy tính đã cài đặt **JDK 8** hoặc mới hơn.
*   Sử dụng Terminal hoặc Command Prompt có hỗ trợ hiển thị ký tự UTF-8.

### 2. Biên dịch (Compile)
Mở cửa sổ dòng lệnh tại thư mục gốc của dự án và chạy lệnh:
```powershell
mkdir bin
javac -encoding UTF-8 -d bin -sourcepath src src/main/Main.java
```

### 3. Khởi chạy (Run)
Sử dụng lệnh sau để bắt đầu trải nghiệm ứng dụng:
```powershell
java -cp bin main.Main
```

### 4. Quy trình sử dụng đề xuất
1.  **Khởi tạo:** Thêm một vài Sinh viên, Môn học và Khóa học thông qua các phím chức năng `1, 2, 3`.
2.  **Nhập liệu:** Sử dụng phím `4` để nhập điểm. Hệ thống sẽ yêu cầu MSSV và các mã môn học/khóa học tương ứng.
3.  **Tra cứu:** Sử dụng phím `6` để xem bảng điểm cá nhân và GPA hiện tại của sinh viên.
4.  **Tổng kết:** Sử dụng phím `7` để xem báo cáo thống kê xếp loại cho toàn bộ danh sách.

## 📝 Lưu ý quan trọng
*   Dữ liệu được lưu trữ tự động trong thư mục `data/`. Không nên chỉnh sửa thủ công các tệp tin này trừ khi bạn hiểu rõ cấu trúc dữ liệu bên trong.
*   Khi nhập năm học, hãy tuân thủ định dạng gợi ý (VD: `2024-2025`) để báo cáo được hiển thị đẹp mắt nhất.

---

## 👨‍💻 Tác giả
*   **Phát triển bởi:**Hải Long (Hlng)
*   **Dự án:** Quản Lý Điểm Sinh Viên
*   **Ngôn ngữ:** Java Core

---
*Nếu bạn thấy dự án này hữu ích, hãy để lại một ⭐ trên GitHub nhé!*
