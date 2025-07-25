package com.ISP392.demo.service;

import com.ISP392.demo.entity.AppointmentEntity;
import org.apache.poi.xwpf.usermodel.*;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class WordExportService {
    
    public ByteArrayInputStream exportAppointmentsToWord(List<AppointmentEntity> appointments) throws IOException {
        try (XWPFDocument document = new XWPFDocument()) {
            
            // Tạo tiêu đề
            XWPFParagraph title = document.createParagraph();
            title.setAlignment(ParagraphAlignment.CENTER);
            XWPFRun titleRun = title.createRun();
            titleRun.setText("DANH SÁCH LỊCH HẸN KHÁM");
            titleRun.setBold(true);
            titleRun.setFontSize(16);
            
            // Tạo bảng
            XWPFTable table = document.createTable(appointments.size() + 1, 8);
            
            // Thiết lập header
            String[] headers = {"STT", "Họ tên", "SĐT", "Email", 
                               "Thời gian hẹn", "Phòng", "Chi tiết", "Trạng thái"};
            
            XWPFTableRow headerRow = table.getRow(0);
            for (int i = 0; i < headers.length; i++) {
                XWPFTableCell cell = headerRow.getCell(i);
                cell.setText(headers[i]);
                cell.setColor("D3D3D3"); // Màu xám nhạt cho header
            }
            
            // Thêm dữ liệu
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            for (int i = 0; i < appointments.size(); i++) {
                AppointmentEntity appointment = appointments.get(i);
                XWPFTableRow row = table.getRow(i + 1);
                
                row.getCell(0).setText(String.valueOf(i + 1));
                row.getCell(1).setText(appointment.getName() != null ? appointment.getName() : "");
                row.getCell(2).setText(appointment.getPhoneNumber() != null ? appointment.getPhoneNumber() : "");
                row.getCell(3).setText(appointment.getEmail() != null ? appointment.getEmail() : "");
                row.getCell(4).setText(appointment.getAppointmentDateTime() != null ? 
                    appointment.getAppointmentDateTime().format(formatter) : "");
                row.getCell(5).setText(appointment.getRoom() != null ? appointment.getRoom().getRoomName() : "");
                row.getCell(6).setText(String.valueOf(appointment.getId())); // Chi tiết là ID
                row.getCell(7).setText(getStatusText(appointment.getStatus()));
            }
            
            // Ghi file
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            document.write(out);
            return new ByteArrayInputStream(out.toByteArray());
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