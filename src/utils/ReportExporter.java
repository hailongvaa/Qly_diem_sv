package utils;

import managers.StudentManager.StudentStatistic;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.awt.Color;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * Xuất báo cáo thống kê sinh viên ra file Excel (.xlsx) và PDF.
 */
public class ReportExporter {

    /**
     * Xuất danh sách thống kê ra file Excel (.xlsx).
     *
     * @param stats    Danh sách thống kê sinh viên
     * @param filePath Đường dẫn file đầu ra (VD: "report.xlsx")
     * @throws IOException nếu lỗi ghi file
     */
    public static void exportToExcel(List<StudentStatistic> stats, String filePath) throws IOException {
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Thống kê sinh viên");

            // Header style
            CellStyle headerStyle = workbook.createCellStyle();
            org.apache.poi.ss.usermodel.Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setFontHeightInPoints((short) 12);
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setBorderBottom(BorderStyle.THIN);
            headerStyle.setBorderTop(BorderStyle.THIN);
            headerStyle.setBorderLeft(BorderStyle.THIN);
            headerStyle.setBorderRight(BorderStyle.THIN);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);

            // Data style
            CellStyle dataStyle = workbook.createCellStyle();
            dataStyle.setBorderBottom(BorderStyle.THIN);
            dataStyle.setBorderTop(BorderStyle.THIN);
            dataStyle.setBorderLeft(BorderStyle.THIN);
            dataStyle.setBorderRight(BorderStyle.THIN);

            CellStyle numberStyle = workbook.createCellStyle();
            numberStyle.cloneStyleFrom(dataStyle);
            numberStyle.setDataFormat(workbook.createDataFormat().getFormat("0.00"));

            // Title row
            Row titleRow = sheet.createRow(0);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("BÁO CÁO THỐNG KÊ ĐIỂM SINH VIÊN");
            CellStyle titleStyle = workbook.createCellStyle();
            org.apache.poi.ss.usermodel.Font titleFont = workbook.createFont();
            titleFont.setBold(true);
            titleFont.setFontHeightInPoints((short) 16);
            titleStyle.setFont(titleFont);
            titleCell.setCellStyle(titleStyle);

            // Header
            String[] headers = {"STT", "MSSV", "Họ và Tên", "ĐTB (10)", "GPA (4)", "Xếp loại"};
            Row headerRow = sheet.createRow(2);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Data rows
            int rowIdx = 3;
            int[] rankCounts = new int[5]; // xuatSac, gioi, kha, tb, yeu

            for (int i = 0; i < stats.size(); i++) {
                StudentStatistic ss = stats.get(i);
                Row row = sheet.createRow(rowIdx++);

                Cell c0 = row.createCell(0); c0.setCellValue(i + 1); c0.setCellStyle(dataStyle);
                Cell c1 = row.createCell(1); c1.setCellValue(ss.getStudent().getMssv()); c1.setCellStyle(dataStyle);
                Cell c2 = row.createCell(2); c2.setCellValue(ss.getStudent().getName()); c2.setCellStyle(dataStyle);
                Cell c3 = row.createCell(3); c3.setCellValue(ss.getAvg10()); c3.setCellStyle(numberStyle);
                Cell c4 = row.createCell(4); c4.setCellValue(ss.getGpa4()); c4.setCellStyle(numberStyle);
                Cell c5 = row.createCell(5); c5.setCellValue(ss.getRank()); c5.setCellStyle(dataStyle);

                switch (ss.getRank()) {
                    case "Xuất sắc": rankCounts[0]++; break;
                    case "Giỏi": rankCounts[1]++; break;
                    case "Khá": rankCounts[2]++; break;
                    case "Trung bình": rankCounts[3]++; break;
                    case "Yếu": rankCounts[4]++; break;
                }
            }

            // Summary
            rowIdx++;
            Row sumTitle = sheet.createRow(rowIdx++);
            sumTitle.createCell(0).setCellValue("TỔNG KẾT");
            String[] ranks = {"Xuất sắc", "Giỏi", "Khá", "Trung bình", "Yếu"};
            for (int i = 0; i < ranks.length; i++) {
                Row r = sheet.createRow(rowIdx++);
                r.createCell(1).setCellValue(ranks[i]);
                r.createCell(2).setCellValue(rankCounts[i]);
                r.createCell(3).setCellValue(String.format("%.1f%%", stats.isEmpty() ? 0 : rankCounts[i] * 100.0 / stats.size()));
            }

            // Auto-size columns
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            try (FileOutputStream fos = new FileOutputStream(filePath)) {
                workbook.write(fos);
            }
        }
    }

    /**
     * Xuất danh sách thống kê ra file PDF.
     *
     * @param stats    Danh sách thống kê sinh viên
     * @param filePath Đường dẫn file đầu ra (VD: "report.pdf")
     * @throws Exception nếu lỗi ghi file
     */
    public static void exportToPdf(List<StudentStatistic> stats, String filePath) throws Exception {
        Document document = new Document(PageSize.A4.rotate());
        PdfWriter.getInstance(document, new FileOutputStream(filePath));
        document.open();

        // Title
        Font titleFont = new Font(Font.HELVETICA, 18, Font.BOLD);
        Paragraph title = new Paragraph("BAO CAO THONG KE DIEM SINH VIEN", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(20);
        document.add(title);

        // Table
        PdfPTable table = new PdfPTable(6);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{1, 2, 4, 1.5f, 1.5f, 2});

        // Header
        Font headerFont = new Font(Font.HELVETICA, 10, Font.BOLD);
        String[] headers = {"STT", "MSSV", "Ho va Ten", "DTB (10)", "GPA (4)", "Xep loai"};
        for (String h : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(h, headerFont));
            cell.setBackgroundColor(new Color(70, 130, 180));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setPadding(5);
            table.addCell(cell);
        }

        // Data
        Font dataFont = new Font(Font.HELVETICA, 9);
        int[] rankCounts = new int[5];

        for (int i = 0; i < stats.size(); i++) {
            StudentStatistic ss = stats.get(i);
            Color bgColor = (i % 2 == 0) ? Color.WHITE : new Color(240, 240, 240);

            addPdfCell(table, String.valueOf(i + 1), dataFont, bgColor, Element.ALIGN_CENTER);
            addPdfCell(table, ss.getStudent().getMssv(), dataFont, bgColor, Element.ALIGN_LEFT);
            addPdfCell(table, ss.getStudent().getName(), dataFont, bgColor, Element.ALIGN_LEFT);
            addPdfCell(table, String.format("%.2f", ss.getAvg10()), dataFont, bgColor, Element.ALIGN_CENTER);
            addPdfCell(table, String.format("%.2f", ss.getGpa4()), dataFont, bgColor, Element.ALIGN_CENTER);
            addPdfCell(table, ss.getRank(), dataFont, bgColor, Element.ALIGN_CENTER);

            switch (ss.getRank()) {
                case "Xuất sắc": rankCounts[0]++; break;
                case "Giỏi": rankCounts[1]++; break;
                case "Khá": rankCounts[2]++; break;
                case "Trung bình": rankCounts[3]++; break;
                case "Yếu": rankCounts[4]++; break;
            }
        }

        document.add(table);

        // Summary
        document.add(new Paragraph("\n"));
        Font sumFont = new Font(Font.HELVETICA, 12, Font.BOLD);
        document.add(new Paragraph("TONG KET", sumFont));

        String[] ranks = {"Xuat sac", "Gioi", "Kha", "Trung binh", "Yeu"};
        Font normalFont = new Font(Font.HELVETICA, 10);
        for (int i = 0; i < ranks.length; i++) {
            double pct = stats.isEmpty() ? 0 : rankCounts[i] * 100.0 / stats.size();
            document.add(new Paragraph(String.format("  %s: %d (%.1f%%)", ranks[i], rankCounts[i], pct), normalFont));
        }
        document.add(new Paragraph(String.format("  Tong: %d sinh vien", stats.size()), normalFont));

        document.close();
    }

    private static void addPdfCell(PdfPTable table, String text, Font font, Color bgColor, int alignment) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBackgroundColor(bgColor);
        cell.setHorizontalAlignment(alignment);
        cell.setPadding(4);
        table.addCell(cell);
    }
}
