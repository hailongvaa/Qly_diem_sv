# 🎓 Hệ Thống Quản Lý Điểm Sinh Viên

![Java](https://img.shields.io/badge/Java-17+-orange?style=for-the-badge&logo=openjdk)
![SQLite](https://img.shields.io/badge/Database-SQLite-blue?style=for-the-badge&logo=sqlite)
![FlatLaf](https://img.shields.io/badge/UI-FlatLaf_Dark-purple?style=for-the-badge)
![JUnit5](https://img.shields.io/badge/Tests-JUnit_5-green?style=for-the-badge)
![Status](https://img.shields.io/badge/Status-Production_Ready-success?style=for-the-badge)
![License](https://img.shields.io/badge/License-MIT-blue?style=for-the-badge)

Hệ thống quản lý học tập toàn diện với **giao diện đồ họa hiện đại (FlatLaf Dark)**, **cơ sở dữ liệu SQLite**, và khả năng **xuất báo cáo Excel/PDF**. Hỗ trợ đầy đủ CRUD cho sinh viên, môn học, khóa học, điểm số cùng tính năng tính GPA tự động và thống kê xếp loại.

---

## 📑 Mục Lục

1. [Tính Năng](#-tính-năng)
2. [Ảnh Chụp Màn Hình](#-ảnh-chụp-màn-hình)
3. [Kiến Trúc Hệ Thống](#-kiến-trúc-hệ-thống)
4. [Cấu Trúc Dự Án](#-cấu-trúc-dự-án)
5. [Yêu Cầu Hệ Thống](#-yêu-cầu-hệ-thống)
6. [Cài Đặt & Khởi Chạy](#-cài-đặt--khởi-chạy)
7. [Hướng Dẫn Sử Dụng](#-hướng-dẫn-sử-dụng)
8. [Chi Tiết Kỹ Thuật](#-chi-tiết-kỹ-thuật)
9. [Bảng Quy Đổi Điểm](#-bảng-quy-đổi-điểm)
10. [Testing](#-testing)
11. [Thư Viện Sử Dụng](#-thư-viện-sử-dụng)
12. [Tác Giả](#-tác-giả)

---

## ✨ Tính Năng

### Quản Lý Dữ Liệu (CRUD)
| Chức năng | Mô tả |
|:---|:---|
| **Sinh viên** | Thêm, sửa, xóa, tìm kiếm theo MSSV hoặc tên (partial match) |
| **Môn học** | Quản lý mã môn, tên môn, số tín chỉ (1-10) |
| **Khóa học** | Quản lý mã khóa, năm học, học kỳ (1-8) |
| **Điểm số** | Nhập/sửa/xóa điểm theo bộ ba (MSSV, Mã MH, Mã KH) |

### Tính Toán & Thống Kê
- **Điểm trung bình hệ 10** — có trọng số tín chỉ, theo khóa hoặc toàn bộ
- **GPA hệ 4** — quy đổi tự động từ hệ 10 theo bảng chuẩn
- **Xếp loại học lực** — Xuất sắc / Giỏi / Khá / Trung bình / Yếu
- **Bảng xếp hạng** — sắp xếp giảm dần theo GPA, hiển thị phần trăm từng loại

### Giao Diện
- **CLI** — Giao diện dòng lệnh đầy đủ chức năng
- **GUI** — FlatLaf Dark theme, tabbed interface, search bar, toolbar, color-coded statistics

### Xuất Báo Cáo
- **Excel (.xlsx)** — Bảng thống kê có format, header, tổng kết (Apache POI)
- **PDF** — Báo cáo chuyên nghiệp với bảng biểu và tổng kết (OpenPDF)

### Lưu Trữ Dữ Liệu
- **SQLite Database** — ACID compliant, tự động tạo bảng, foreign keys
- **Auto-migration** — Tự động chuyển dữ liệu từ file `.txt` cũ sang SQLite
- **Backup** — Tạo file `.bak` trước mỗi lần ghi (DataManager legacy)

---

## 🏗 Kiến Trúc Hệ Thống

```
┌─────────────────────────────────────────────────┐
│              PRESENTATION LAYER                  │
│  ┌──────────────┐    ┌────────────────────────┐ │
│  │  Main.java   │    │    MainGUI.java         │ │
│  │   (CLI)      │    │  (Swing + FlatLaf Dark) │ │
│  └──────┬───────┘    └───────────┬─────────────┘ │
├─────────┴────────────────────────┴───────────────┤
│                 FACADE LAYER                      │
│  ┌───────────────────────────────────────────┐   │
│  │         StudentManager (Facade)           │   │
│  │  - Điều phối tất cả services              │   │
│  │  - Validation cross-entity               │   │
│  └──────────────────┬────────────────────────┘   │
├─────────────────────┴────────────────────────────┤
│                SERVICE LAYER                      │
│  ┌────────────┐ ┌────────────┐ ┌──────────────┐ │
│  │ Student    │ │ Subject    │ │ Course       │ │
│  │ Service    │ │ Service    │ │ Service      │ │
│  └─────┬──────┘ └─────┬──────┘ └──────┬───────┘ │
│  ┌─────┴──────┐ ┌─────┴──────┐        │         │
│  │ Score      │ │ Gpa        │        │         │
│  │ Service    │ │ Calculator │        │         │
│  └─────┬──────┘ └────────────┘        │         │
├────────┴──────────────────────────────┴──────────┤
│              PERSISTENCE LAYER                    │
│  ┌──────────────────┐  ┌──────────────────────┐  │
│  │ DatabaseManager  │  │   DataManager        │  │
│  │ (SQLite JDBC)    │  │ (Legacy .txt backup) │  │
│  └────────┬─────────┘  └──────────────────────┘  │
│           │                                       │
│  ┌────────▼─────────┐                            │
│  │  student_mgmt.db │                            │
│  │  (SQLite file)   │                            │
│  └──────────────────┘                            │
├──────────────────────────────────────────────────┤
│                MODEL LAYER                        │
│  Student │ Subject │ Course │ Score              │
│  (equals/hashCode + Javadoc)                     │
├──────────────────────────────────────────────────┤
│              UTILITIES                            │
│  ValidationUtils │ GpaCalculator │ ReportExporter│
└──────────────────────────────────────────────────┘
```

### Design Patterns Sử Dụng
| Pattern | Áp dụng |
|:---|:---|
| **Facade** | `StudentManager` — điều phối 4 services |
| **Service Layer** | `StudentService`, `SubjectService`, `CourseService`, `ScoreService` |
| **Repository** | `DatabaseManager` — abstract hóa data access |
| **Strategy** | `DataManager` (txt) vs `DatabaseManager` (SQLite) |
| **Separation of Concerns** | UI ↔ Business Logic ↔ Data Access hoàn toàn tách biệt |

---

## 📂 Cấu Trúc Dự Án

```
qly_diem_sv/
├── data/                          # Dữ liệu
│   ├── student_management.db      # SQLite database (chính)
│   ├── students.txt               # Legacy data (62 sinh viên)
│   ├── subjects.txt               # Legacy data
│   ├── courses.txt                # Legacy data
│   └── scores.txt                 # Legacy data
├── lib/                           # Thư viện bên ngoài (~35MB)
│   ├── sqlite-jdbc-3.45.3.0.jar   # SQLite JDBC driver
│   ├── flatlaf-3.4.1.jar          # FlatLaf Look-and-Feel
│   ├── junit-platform-*.jar       # JUnit 5 test runner
│   ├── openpdf-1.3.35.jar         # PDF export
│   ├── poi-5.2.5.jar              # Excel export (Apache POI)
│   ├── poi-ooxml-5.2.5.jar        # Excel .xlsx support
│   └── ... (16 JARs total)
├── src/
│   ├── main/
│   │   ├── Main.java              # CLI entry point
│   │   └── MainGUI.java           # GUI entry point (FlatLaf)
│   ├── managers/
│   │   ├── StudentManager.java    # Facade pattern
│   │   ├── DatabaseManager.java   # SQLite JDBC (primary)
│   │   └── DataManager.java       # File I/O (legacy + migration)
│   ├── services/
│   │   ├── StudentService.java    # CRUD sinh viên
│   │   ├── SubjectService.java    # CRUD môn học
│   │   ├── CourseService.java     # CRUD khóa học
│   │   └── ScoreService.java      # CRUD điểm + GPA
│   ├── models/
│   │   ├── Student.java           # MSSV, tên, lớp
│   │   ├── Subject.java           # Mã MH, tên, tín chỉ
│   │   ├── Course.java            # Mã KH, năm học, học kỳ
│   │   └── Score.java             # MSSV+MH+KH → điểm
│   ├── utils/
│   │   ├── GpaCalculator.java     # Tính GPA, quy đổi, xếp loại
│   │   ├── ReportExporter.java    # Xuất Excel + PDF
│   │   └── ValidationUtils.java   # Validate input CLI
│   ├── exceptions/
│   │   ├── DuplicateEntityException.java
│   │   └── EntityNotFoundException.java
│   └── test/
│       ├── TestRunner.java        # 55 tests (standalone)
│       └── JUnit5Tests.java       # 20 tests (JUnit 5)
├── bin/                           # Compiled .class files
└── README.md                      # Tài liệu này
```

---

## 💻 Yêu Cầu Hệ Thống

| Yêu cầu | Chi tiết |
|:---|:---|
| **JDK** | Java 8 trở lên (khuyến nghị JDK 17+) |
| **OS** | Windows / macOS / Linux |
| **RAM** | Tối thiểu 256MB |
| **Disk** | ~50MB (bao gồm thư viện) |
| **Terminal** | Hỗ trợ UTF-8 (cho hiển thị tiếng Việt) |

---

## 🚀 Cài Đặt & Khởi Chạy

### 1. Clone dự án
```bash
git clone https://github.com/hailongvaa/Qly_diem_sv.git
cd Qly_diem_sv
```

### 2. Biên dịch
```powershell
javac -encoding UTF-8 -cp "lib/*" -d bin -sourcepath src src/main/Main.java src/main/MainGUI.java src/test/TestRunner.java src/test/JUnit5Tests.java
```

### 3. Khởi chạy

**Giao diện đồ họa (GUI)** — khuyến nghị:
```powershell
java -cp "bin;lib/*" main.MainGUI
```

**Giao diện dòng lệnh (CLI)**:
```powershell
java -cp "bin;lib/*" main.Main
```

### 4. Chạy test
```powershell
# Custom test runner (55 tests)
java -cp "bin;lib/*" test.TestRunner

# JUnit 5 (20 tests)
$jars = (Get-ChildItem lib\*.jar | % { $_.FullName }) -join ";"
java -jar lib/junit-platform-console-standalone-1.10.2.jar execute -cp "bin;$jars" -c test.JUnit5Tests
```

---

## 📖 Hướng Dẫn Sử Dụng

### CLI — Menu Chính
```
╔═══════════════════════════════════╗
║   QUẢN LÝ ĐIỂM SINH VIÊN (v2)   ║
╠═══════════════════════════════════╣
║ 1. Quản lý sinh viên              ║  → Thêm / Sửa / Xóa
║ 2. Quản lý môn học                ║  → Thêm / Sửa / Xóa
║ 3. Quản lý khóa học               ║  → Thêm / Sửa / Xóa
║ 4. Quản lý điểm                   ║  → Nhập / Sửa / Xóa
║ 5. Hiển thị danh sách              ║  → SV / MH / KH / Điểm
║ 6. Tìm kiếm sinh viên             ║  → Theo MSSV hoặc tên
║ 7. Thống kê xếp loại              ║  → Bảng xếp hạng GPA
║ 8. Xuất báo cáo (Excel/PDF)       ║  → File .xlsx hoặc .pdf
║ 9. Thoát                           ║
╚═══════════════════════════════════╝
```

### GUI — Các Tab
| Tab | Chức năng |
|:---|:---|
| 👨‍🎓 **Sinh viên** | Bảng danh sách + search bar + nút Thêm/Sửa/Xóa |
| 📚 **Môn học** | CRUD môn học với validation số tín chỉ |
| 📅 **Khóa học** | CRUD khóa học với validation học kỳ |
| 📝 **Điểm số** | Form nhập điểm, click bảng để auto-fill, lưu/xóa |
| 📊 **Thống kê** | Bảng xếp hạng color-coded, tổng kết theo loại |

### Toolbar (GUI)
- 💾 **Lưu** — Lưu dữ liệu (SQLite tự động lưu, nút này để đảm bảo)
- 📊 **Xuất Excel** — Chọn nơi lưu file .xlsx
- 📄 **Xuất PDF** — Chọn nơi lưu file .pdf

### Quy Trình Sử Dụng Đề Xuất
1. **Thêm sinh viên** → Tab "Sinh viên" → nút ➕ Thêm
2. **Thêm môn học** → Tab "Môn học" → nhập mã, tên, số tín chỉ
3. **Thêm khóa học** → Tab "Khóa học" → nhập mã, năm, học kỳ
4. **Nhập điểm** → Tab "Điểm số" → nhập MSSV + Mã MH + Mã KH + Điểm
5. **Xem thống kê** → Tab "Thống kê" → nút 🔄 Cập nhật
6. **Xuất báo cáo** → Toolbar → 📊 Excel hoặc 📄 PDF

---

## 🔧 Chi Tiết Kỹ Thuật

### Database Schema (SQLite)

```sql
CREATE TABLE students (
    mssv TEXT PRIMARY KEY COLLATE NOCASE,
    name TEXT NOT NULL,
    class_name TEXT NOT NULL
);

CREATE TABLE subjects (
    subject_id TEXT PRIMARY KEY COLLATE NOCASE,
    subject_name TEXT NOT NULL,
    credit INTEGER NOT NULL
);

CREATE TABLE courses (
    course_id TEXT PRIMARY KEY COLLATE NOCASE,
    year TEXT NOT NULL,
    semester INTEGER NOT NULL
);

CREATE TABLE scores (
    mssv TEXT NOT NULL COLLATE NOCASE,
    subject_id TEXT NOT NULL COLLATE NOCASE,
    course_id TEXT NOT NULL COLLATE NOCASE,
    value REAL NOT NULL,
    PRIMARY KEY (mssv, subject_id, course_id),
    FOREIGN KEY (mssv) REFERENCES students(mssv) ON DELETE CASCADE,
    FOREIGN KEY (subject_id) REFERENCES subjects(subject_id) ON DELETE CASCADE,
    FOREIGN KEY (course_id) REFERENCES courses(course_id) ON DELETE CASCADE
);
```

### Auto-Migration
Khi khởi chạy lần đầu với SQLite, hệ thống tự động:
1. Tạo database `data/student_management.db`
2. Tạo 4 bảng nếu chưa tồn tại
3. Đọc dữ liệu từ `students.txt`, `subjects.txt`, `courses.txt`, `scores.txt`
4. Import vào SQLite bằng batch insert
5. File `.txt` cũ được giữ nguyên (không xóa)

### Xóa Cascade
- Xóa **sinh viên** → tự động xóa tất cả điểm của sinh viên đó
- Xóa **môn học** → tự động xóa tất cả điểm thuộc môn đó
- Xóa **khóa học** → tự động xóa tất cả điểm thuộc khóa đó

### Custom Exceptions
| Exception | Khi nào |
|:---|:---|
| `DuplicateEntityException` | Thêm entity có mã đã tồn tại |
| `EntityNotFoundException` | Thao tác trên entity không tìm thấy |

### Validation
- **MSSV**: Không được rỗng
- **Tên**: Không được rỗng, hỗ trợ UTF-8 (tiếng Việt)
- **Số tín chỉ**: Integer trong [1, 10]
- **Học kỳ**: Integer trong [1, 8]
- **Điểm**: Double trong [0.0, 10.0]

---

## 📊 Bảng Quy Đổi Điểm

### Hệ 10 → Hệ 4
| Điểm hệ 10 | Điểm hệ 4 | Xếp loại |
|:---:|:---:|:---|
| 8.5 - 10.0 | 4.0 | Xuất sắc (≥ 3.6) |
| 8.0 - 8.4 | 3.5 | Giỏi (≥ 3.2) |
| 7.0 - 7.9 | 3.0 | Khá (≥ 2.5) |
| 6.5 - 6.9 | 2.5 | Khá |
| 5.5 - 6.4 | 2.0 | Trung bình (≥ 2.0) |
| 5.0 - 5.4 | 1.5 | Yếu |
| 4.0 - 4.9 | 1.0 | Yếu |
| 0.0 - 3.9 | 0.0 | Yếu |

### Công Thức GPA
```
GPA = Σ(Điểm_hệ_4_i × Tín_chỉ_i) / Σ(Tín_chỉ_i)
```

---

## 🧪 Testing

### Test Suite 1: Custom TestRunner (55 tests)
| Nhóm | Số test | Nội dung |
|:---|:---:|:---|
| GpaCalculator | 14 | Quy đổi điểm, xếp loại |
| Student CRUD | 9 | Thêm, sửa, xóa, tìm, trùng mã |
| Subject CRUD | 7 | Thêm, sửa, xóa, tìm |
| Course CRUD | 5 | Thêm, sửa, xóa, tìm |
| Score CRUD | 5 | Thêm, cập nhật, xóa, validation |
| Search | 4 | Tìm theo tên, MSSV, không kết quả |
| Statistics | 4 | Sắp xếp, tính GPA |
| equals/hashCode | 7 | Case insensitive, composite key |

### Test Suite 2: JUnit 5 (20 tests)
| Nested Class | Số test | Nội dung |
|:---|:---:|:---|
| GPA Calculator Tests | 7 | Quy đổi + xếp loại |
| Student CRUD Tests | 5 | Add, duplicate, update, remove, search |
| Score & GPA Integration | 4 | Add, update, nonexist, GPA calc |
| Model equals/hashCode | 3 | Student, Score, not equal |
| Statistics Tests | 1 | Sorted descending |

### Kết Quả
```
Custom TestRunner:  55 passed, 0 failed ✅
JUnit 5:            20 passed, 0 failed ✅
Total:              75 tests, ALL PASSED ✅
```

---

## 📦 Thư Viện Sử Dụng

| Thư viện | Version | Mục đích |
|:---|:---|:---|
| **SQLite JDBC** | 3.45.3.0 | Kết nối SQLite database |
| **SLF4J** | 2.0.12 | Logging framework (dependency của SQLite) |
| **FlatLaf** | 3.4.1 | Modern Look-and-Feel cho Swing |
| **Apache POI** | 5.2.5 | Xuất file Excel (.xlsx) |
| **OpenPDF** | 1.3.35 | Xuất file PDF |
| **JUnit 5** | 1.10.2 | Unit testing framework |
| **Commons IO/Compress/Collections/Codec/Math3** | various | Dependencies của Apache POI |
| **XMLBeans** | 5.2.0 | XML processing (POI dependency) |
| **Log4j API** | 2.22.1 | Logging (POI dependency) |

---

## 👨‍💻 Tác Giả

| | |
|:---|:---|
| **Phát triển bởi** | Hải Long (Hlng) |
| **Dự án** | Quản Lý Điểm Sinh Viên |
| **Ngôn ngữ** | Java Core + Swing |
| **Database** | SQLite |
| **Version** | 2.0 |

---

*Nếu bạn thấy dự án này hữu ích, hãy để lại một ⭐ trên GitHub nhé!*
