package com.ISP392.demo.service;

import com.ISP392.demo.entity.AppointmentEntity;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class PdfExportService {
    public ByteArrayInputStream exportAppointmentsToPdf(List<AppointmentEntity> appointments) throws IOException {
        String[] columns = {
                "STT", "Họ tên bệnh nhân", "Số điện thoại", "Email",
                "Thời gian hẹn", "Lý do khám", "Phòng khám", "Trạng thái"
        };
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(out);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        float[] columnWidths = {30F, 100F, 80F, 120F, 90F, 100F, 80F, 80F};
        Table table = new Table(columnWidths);

        // Header
        for (String col : columns) {
            table.addHeaderCell(new Cell().add(new Paragraph(col)));
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        int rowNum = 1;
        for (AppointmentEntity appointment : appointments) {
            table.addCell(String.valueOf(rowNum));
            table.addCell(appointment.getName() != null ? appointment.getName() : "");
            table.addCell(appointment.getPhoneNumber() != null ? appointment.getPhoneNumber() : "");
            table.addCell(appointment.getEmail() != null ? appointment.getEmail() : "");
            table.addCell(appointment.getAppointmentDateTime() != null ? appointment.getAppointmentDateTime().format(formatter) : "");
            table.addCell(appointment.getReason() != null ? appointment.getReason() : "");
            table.addCell(appointment.getRoom() != null ? appointment.getRoom().getRoomName() : "");
            table.addCell(getStatusText(appointment.getStatus()));
            rowNum++;
        }

        document.add(table);
        document.close();
        return new ByteArrayInputStream(out.toByteArray());
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