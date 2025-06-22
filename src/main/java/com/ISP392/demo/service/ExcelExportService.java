package com.ISP392.demo.service;

import com.ISP392.demo.entity.AppointmentEntity;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ExcelExportService {

    public ByteArrayInputStream exportAppointmentsToExcel(List<AppointmentEntity> appointments) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Lịch hẹn khám");

            // Create header style
            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);

            // Create header row
            Row headerRow = sheet.createRow(0);
            String[] columns = {
                    "STT", "Họ tên bệnh nhân", "Số điện thoại", "Email",
                    "Thời gian hẹn", "Lý do khám", "Phòng khám", "Trạng thái"
            };

            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
                cell.setCellStyle(headerStyle);
            }

            // Create data rows
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            int rowNum = 1;
            for (AppointmentEntity appointment : appointments) {
                Row row = sheet.createRow(rowNum++);
                
                row.createCell(0).setCellValue(rowNum - 1);
                row.createCell(1).setCellValue(appointment.getName());
                row.createCell(2).setCellValue(appointment.getPhoneNumber());
                row.createCell(3).setCellValue(appointment.getEmail());
                row.createCell(4).setCellValue(
                    appointment.getAppointmentDateTime() != null ? 
                    appointment.getAppointmentDateTime().format(formatter) : ""
                );
                row.createCell(5).setCellValue(appointment.getReason());
                row.createCell(6).setCellValue(
                    appointment.getRoom() != null ? appointment.getRoom().getRoomName() : ""
                );
                row.createCell(7).setCellValue(getStatusText(appointment.getStatus()));
            }

            // Autosize columns
            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // Write to ByteArrayOutputStream
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return new ByteArrayInputStream(outputStream.toByteArray());
        }
    }

    private String getStatusText(Integer status) {
        if (status == null) return "Không xác định";
        return switch (status) {
            case -1 -> "Đang chờ xác nhận";
            case 0 -> "Đã huỷ";
            case 1 -> "Đã khám";
            case 2 -> "Đang chờ khám";
            default -> "Không xác định";
        };
    }
} 