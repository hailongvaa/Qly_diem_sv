package main;

import com.formdev.flatlaf.FlatDarkLaf;
import managers.StudentManager;
import managers.StudentManager.StudentStatistic;
import models.Course;
import models.Score;
import models.Student;
import models.Subject;
import utils.ReportExporter;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.List;

/**
 * Giao diện đồ họa hiện đại với FlatLaf Dark theme.
 */
public class MainGUI extends JFrame {
    private StudentManager manager;
    private JTabbedPane tabbedPane;

    public MainGUI(StudentManager manager) {
        this.manager = manager;
        setTitle("🎓 Hệ Thống Quản Lý Điểm Sinh Viên");
        setSize(1050, 700);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int confirm = JOptionPane.showConfirmDialog(MainGUI.this,
                    "Bạn có muốn thoát?", "Xác nhận", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) { dispose(); System.exit(0); }
            }
        });

        // Main layout
        JPanel mainPanel = new JPanel(new BorderLayout());

        // Toolbar
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        JButton btnSave = new JButton("💾 Lưu");
        JButton btnExportExcel = new JButton("📊 Xuất Excel");
        JButton btnExportPdf = new JButton("📄 Xuất PDF");
        btnSave.addActionListener(e -> { manager.saveData(); showInfo("Đã lưu dữ liệu!"); });
        btnExportExcel.addActionListener(e -> exportExcel());
        btnExportPdf.addActionListener(e -> exportPdf());
        toolBar.add(btnSave); toolBar.addSeparator();
        toolBar.add(btnExportExcel); toolBar.add(btnExportPdf);

        tabbedPane = new JTabbedPane();
        tabbedPane.addTab("👨‍🎓 Sinh viên", createStudentPanel());
        tabbedPane.addTab("📚 Môn học", createSubjectPanel());
        tabbedPane.addTab("📅 Khóa học", createCoursePanel());
        tabbedPane.addTab("📝 Điểm số", createScorePanel());
        tabbedPane.addTab("📊 Thống kê", createStatisticsPanel());

        mainPanel.add(toolBar, BorderLayout.NORTH);
        mainPanel.add(tabbedPane, BorderLayout.CENTER);

        // Status bar
        JLabel statusBar = new JLabel("  Sẵn sàng | SQLite Database | " + manager.getStudents().size() + " sinh viên");
        statusBar.setBorder(BorderFactory.createEtchedBorder());
        mainPanel.add(statusBar, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void showInfo(String msg) { JOptionPane.showMessageDialog(this, msg); }
    private void showError(String msg) { JOptionPane.showMessageDialog(this, msg, "Lỗi", JOptionPane.ERROR_MESSAGE); }

    // ==================== EXPORT ====================
    private void exportExcel() {
        JFileChooser fc = new JFileChooser();
        fc.setSelectedFile(new File("thong_ke_sinh_vien.xlsx"));
        if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                ReportExporter.exportToExcel(manager.getStudentStatistics(), fc.getSelectedFile().getAbsolutePath());
                showInfo("Đã xuất Excel: " + fc.getSelectedFile().getName());
            } catch (Exception ex) { showError("Lỗi xuất Excel: " + ex.getMessage()); }
        }
    }

    private void exportPdf() {
        JFileChooser fc = new JFileChooser();
        fc.setSelectedFile(new File("thong_ke_sinh_vien.pdf"));
        if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                ReportExporter.exportToPdf(manager.getStudentStatistics(), fc.getSelectedFile().getAbsolutePath());
                showInfo("Đã xuất PDF: " + fc.getSelectedFile().getName());
            } catch (Exception ex) { showError("Lỗi xuất PDF: " + ex.getMessage()); }
        }
    }

    // ==================== STUDENT ====================
    private JPanel createStudentPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        String[] columns = {"MSSV", "Tên", "Lớp"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(model);
        table.setRowHeight(28);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        refreshStudentTable(model);

        // Search bar
        JPanel searchPanel = new JPanel(new BorderLayout(5, 0));
        JTextField searchField = new JTextField();
        JButton btnSearch = new JButton("🔍 Tìm");
        JButton btnReset = new JButton("↻");
        searchPanel.add(new JLabel(" Tìm kiếm: "), BorderLayout.WEST);
        searchPanel.add(searchField, BorderLayout.CENTER);
        JPanel searchBtns = new JPanel(); searchBtns.add(btnSearch); searchBtns.add(btnReset);
        searchPanel.add(searchBtns, BorderLayout.EAST);

        btnSearch.addActionListener(e -> {
            String kw = searchField.getText().trim();
            if (kw.isEmpty()) { refreshStudentTable(model); return; }
            model.setRowCount(0);
            for (Student s : manager.searchStudents(kw))
                model.addRow(new Object[]{s.getMssv(), s.getName(), s.getClassName()});
        });
        btnReset.addActionListener(e -> { searchField.setText(""); refreshStudentTable(model); });

        JPanel btnPanel = new JPanel();
        JButton btnAdd = new JButton("➕ Thêm");
        JButton btnEdit = new JButton("✏️ Sửa");
        JButton btnDelete = new JButton("🗑️ Xóa");

        btnAdd.addActionListener(e -> {
            JTextField f1 = new JTextField(), f2 = new JTextField(), f3 = new JTextField();
            Object[] msg = {"MSSV:", f1, "Tên:", f2, "Lớp:", f3};
            if (JOptionPane.showConfirmDialog(this, msg, "Thêm SV", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
                if (f1.getText().trim().isEmpty() || f2.getText().trim().isEmpty() || f3.getText().trim().isEmpty()) { showError("Nhập đầy đủ!"); return; }
                try { manager.addStudent(new Student(f1.getText().trim(), f2.getText().trim(), f3.getText().trim())); refreshStudentTable(model); }
                catch (Exception ex) { showError(ex.getMessage()); }
            }
        });

        btnEdit.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) { showError("Chọn sinh viên cần sửa."); return; }
            String mssv = (String) table.getValueAt(row, 0);
            JTextField f1 = new JTextField((String) table.getValueAt(row, 1));
            JTextField f2 = new JTextField((String) table.getValueAt(row, 2));
            Object[] msg = {"Tên:", f1, "Lớp:", f2};
            if (JOptionPane.showConfirmDialog(this, msg, "Sửa: " + mssv, JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
                try { manager.updateStudent(mssv, f1.getText().trim(), f2.getText().trim()); refreshStudentTable(model); }
                catch (Exception ex) { showError(ex.getMessage()); }
            }
        });

        btnDelete.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) { showError("Chọn sinh viên cần xóa."); return; }
            String mssv = (String) table.getValueAt(row, 0);
            if (JOptionPane.showConfirmDialog(this, "Xóa " + mssv + "?", "Xác nhận", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                manager.removeStudent(mssv); refreshStudentTable(model);
            }
        });

        btnPanel.add(btnAdd); btnPanel.add(btnEdit); btnPanel.add(btnDelete);
        panel.add(searchPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        panel.add(btnPanel, BorderLayout.SOUTH);
        return panel;
    }

    private void refreshStudentTable(DefaultTableModel model) {
        model.setRowCount(0);
        for (Student s : manager.getStudents())
            model.addRow(new Object[]{s.getMssv(), s.getName(), s.getClassName()});
    }

    // ==================== SUBJECT ====================
    private JPanel createSubjectPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        String[] columns = {"Mã MH", "Tên môn", "Số TC"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) { public boolean isCellEditable(int r, int c) { return false; } };
        JTable table = new JTable(model); table.setRowHeight(28);
        refreshSubjectTable(model);

        JPanel btnPanel = new JPanel();
        JButton btnAdd = new JButton("➕ Thêm");
        JButton btnEdit = new JButton("✏️ Sửa");
        JButton btnDel = new JButton("🗑️ Xóa");

        btnAdd.addActionListener(e -> {
            JTextField f1 = new JTextField(), f2 = new JTextField(), f3 = new JTextField();
            Object[] msg = {"Mã MH:", f1, "Tên:", f2, "Số TC (1-10):", f3};
            if (JOptionPane.showConfirmDialog(this, msg, "Thêm MH", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
                try {
                    int c = Integer.parseInt(f3.getText().trim());
                    if (c < 1 || c > 10) { showError("Số TC phải 1-10."); return; }
                    manager.addSubject(new Subject(f1.getText().trim(), f2.getText().trim(), c));
                    refreshSubjectTable(model);
                } catch (NumberFormatException ex) { showError("Số TC phải là số."); }
                catch (Exception ex) { showError(ex.getMessage()); }
            }
        });

        btnEdit.addActionListener(e -> {
            int row = table.getSelectedRow(); if (row == -1) { showError("Chọn môn học."); return; }
            String id = (String) table.getValueAt(row, 0);
            JTextField f1 = new JTextField((String) table.getValueAt(row, 1));
            JTextField f2 = new JTextField(table.getValueAt(row, 2).toString());
            if (JOptionPane.showConfirmDialog(this, new Object[]{"Tên:", f1, "Số TC:", f2}, "Sửa: " + id, JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
                try { manager.updateSubject(id, f1.getText().trim(), Integer.parseInt(f2.getText().trim())); refreshSubjectTable(model); }
                catch (Exception ex) { showError(ex.getMessage()); }
            }
        });

        btnDel.addActionListener(e -> {
            int row = table.getSelectedRow(); if (row == -1) { showError("Chọn môn học."); return; }
            if (JOptionPane.showConfirmDialog(this, "Xóa?", "Xác nhận", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                manager.removeSubject((String) table.getValueAt(row, 0)); refreshSubjectTable(model);
            }
        });

        btnPanel.add(btnAdd); btnPanel.add(btnEdit); btnPanel.add(btnDel);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        panel.add(btnPanel, BorderLayout.SOUTH);
        return panel;
    }

    private void refreshSubjectTable(DefaultTableModel model) {
        model.setRowCount(0);
        for (Subject s : manager.getSubjects()) model.addRow(new Object[]{s.getSubjectId(), s.getSubjectName(), s.getCredit()});
    }

    // ==================== COURSE ====================
    private JPanel createCoursePanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        String[] columns = {"Mã KH", "Năm học", "Học kỳ"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) { public boolean isCellEditable(int r, int c) { return false; } };
        JTable table = new JTable(model); table.setRowHeight(28);
        refreshCourseTable(model);

        JPanel btnPanel = new JPanel();
        JButton btnAdd = new JButton("➕ Thêm");
        JButton btnEdit = new JButton("✏️ Sửa");
        JButton btnDel = new JButton("🗑️ Xóa");

        btnAdd.addActionListener(e -> {
            JTextField f1 = new JTextField(), f2 = new JTextField(), f3 = new JTextField();
            Object[] msg = {"Mã KH:", f1, "Năm học:", f2, "Học kỳ (1-8):", f3};
            if (JOptionPane.showConfirmDialog(this, msg, "Thêm KH", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
                try {
                    int sem = Integer.parseInt(f3.getText().trim());
                    if (sem < 1 || sem > 8) { showError("Học kỳ phải 1-8."); return; }
                    manager.addCourse(new Course(f1.getText().trim(), f2.getText().trim(), sem));
                    refreshCourseTable(model);
                } catch (NumberFormatException ex) { showError("Học kỳ phải là số."); }
                catch (Exception ex) { showError(ex.getMessage()); }
            }
        });

        btnEdit.addActionListener(e -> {
            int row = table.getSelectedRow(); if (row == -1) { showError("Chọn khóa học."); return; }
            String id = (String) table.getValueAt(row, 0);
            JTextField f1 = new JTextField((String) table.getValueAt(row, 1));
            JTextField f2 = new JTextField(table.getValueAt(row, 2).toString());
            if (JOptionPane.showConfirmDialog(this, new Object[]{"Năm:", f1, "Học kỳ:", f2}, "Sửa: " + id, JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
                try { manager.updateCourse(id, f1.getText().trim(), Integer.parseInt(f2.getText().trim())); refreshCourseTable(model); }
                catch (Exception ex) { showError(ex.getMessage()); }
            }
        });

        btnDel.addActionListener(e -> {
            int row = table.getSelectedRow(); if (row == -1) { showError("Chọn khóa học."); return; }
            if (JOptionPane.showConfirmDialog(this, "Xóa?", "Xác nhận", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                manager.removeCourse((String) table.getValueAt(row, 0)); refreshCourseTable(model);
            }
        });

        btnPanel.add(btnAdd); btnPanel.add(btnEdit); btnPanel.add(btnDel);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        panel.add(btnPanel, BorderLayout.SOUTH);
        return panel;
    }

    private void refreshCourseTable(DefaultTableModel model) {
        model.setRowCount(0);
        for (Course c : manager.getCourses()) model.addRow(new Object[]{c.getCourseId(), c.getYear(), c.getSemester()});
    }

    // ==================== SCORE ====================
    private JPanel createScorePanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        String[] columns = {"MSSV", "Mã MH", "Mã KH", "Điểm"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) { public boolean isCellEditable(int r, int c) { return false; } };
        JTable table = new JTable(model); table.setRowHeight(28);
        refreshScoreTable(model);

        JPanel formPanel = new JPanel(new GridLayout(3, 4, 5, 5));
        formPanel.setBorder(BorderFactory.createTitledBorder("Nhập/Sửa điểm"));
        JTextField txtMssv = new JTextField(), txtSub = new JTextField(), txtCou = new JTextField(), txtVal = new JTextField();
        JButton btnSave = new JButton("💾 Lưu Điểm");
        JButton btnDel = new JButton("🗑️ Xóa Điểm");

        formPanel.add(new JLabel(" MSSV:")); formPanel.add(txtMssv);
        formPanel.add(new JLabel(" Mã MH:")); formPanel.add(txtSub);
        formPanel.add(new JLabel(" Mã KH:")); formPanel.add(txtCou);
        formPanel.add(new JLabel(" Điểm (0-10):")); formPanel.add(txtVal);
        formPanel.add(btnSave); formPanel.add(btnDel);

        btnSave.addActionListener(e -> {
            try {
                double val = Double.parseDouble(txtVal.getText().trim());
                if (val < 0 || val > 10) { showError("Điểm phải 0-10."); return; }
                boolean updated = manager.addScore(txtMssv.getText().trim(), txtSub.getText().trim(), txtCou.getText().trim(), val);
                showInfo(updated ? "Cập nhật điểm!" : "Thêm điểm mới!");
                refreshScoreTable(model);
            } catch (NumberFormatException ex) { showError("Điểm phải là số."); }
            catch (Exception ex) { showError(ex.getMessage()); }
        });

        btnDel.addActionListener(e -> {
            if (manager.removeScore(txtMssv.getText().trim(), txtSub.getText().trim(), txtCou.getText().trim())) {
                showInfo("Đã xóa."); refreshScoreTable(model);
            } else showError("Không tìm thấy.");
        });

        table.getSelectionModel().addListSelectionListener(e -> {
            int row = table.getSelectedRow();
            if (row != -1) {
                txtMssv.setText((String) table.getValueAt(row, 0));
                txtSub.setText((String) table.getValueAt(row, 1));
                txtCou.setText((String) table.getValueAt(row, 2));
                txtVal.setText(table.getValueAt(row, 3).toString());
            }
        });

        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        panel.add(formPanel, BorderLayout.SOUTH);
        return panel;
    }

    private void refreshScoreTable(DefaultTableModel model) {
        model.setRowCount(0);
        for (Score s : manager.getScores()) model.addRow(new Object[]{s.getMssv(), s.getSubjectId(), s.getCourseId(), s.getValue()});
    }

    // ==================== STATISTICS ====================
    private JPanel createStatisticsPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        String[] columns = {"#", "MSSV", "Họ tên", "ĐTB (10)", "GPA (4)", "Xếp loại"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) { public boolean isCellEditable(int r, int c) { return false; } };
        JTable table = new JTable(model); table.setRowHeight(28);

        // Color-code xếp loại column
        table.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value, boolean sel, boolean focus, int row, int col) {
                Component c = super.getTableCellRendererComponent(t, value, sel, focus, row, col);
                if (!sel && value != null) {
                    switch (value.toString()) {
                        case "Xuất sắc": c.setForeground(new Color(0, 200, 80)); break;
                        case "Giỏi": c.setForeground(new Color(0, 150, 255)); break;
                        case "Khá": c.setForeground(new Color(255, 200, 0)); break;
                        case "Trung bình": c.setForeground(new Color(255, 140, 0)); break;
                        case "Yếu": c.setForeground(new Color(255, 60, 60)); break;
                        default: c.setForeground(table.getForeground());
                    }
                }
                setHorizontalAlignment(CENTER);
                return c;
            }
        });

        JPanel bottomPanel = new JPanel(new BorderLayout());
        JButton btnRefresh = new JButton("🔄 Cập nhật thống kê");
        JLabel lblSummary = new JLabel(" ");

        btnRefresh.addActionListener(e -> {
            model.setRowCount(0);
            List<StudentStatistic> stats = manager.getStudentStatistics();
            int idx = 1;
            int[] counts = new int[5];
            for (StudentStatistic ss : stats) {
                model.addRow(new Object[]{idx++, ss.getStudent().getMssv(), ss.getStudent().getName(),
                    String.format("%.2f", ss.getAvg10()), String.format("%.2f", ss.getGpa4()), ss.getRank()});
                switch (ss.getRank()) {
                    case "Xuất sắc": counts[0]++; break; case "Giỏi": counts[1]++; break;
                    case "Khá": counts[2]++; break; case "Trung bình": counts[3]++; break;
                    case "Yếu": counts[4]++; break;
                }
            }
            lblSummary.setText(String.format(" Tổng: %d SV | XS: %d | G: %d | K: %d | TB: %d | Y: %d",
                stats.size(), counts[0], counts[1], counts[2], counts[3], counts[4]));
        });

        bottomPanel.add(btnRefresh, BorderLayout.WEST);
        bottomPanel.add(lblSummary, BorderLayout.CENTER);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        panel.add(bottomPanel, BorderLayout.SOUTH);
        return panel;
    }

    // ==================== MAIN ====================
    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(new FlatDarkLaf()); }
        catch (Exception e) { e.printStackTrace(); }

        StudentManager manager = new StudentManager();
        SwingUtilities.invokeLater(() -> new MainGUI(manager).setVisible(true));
    }
}
