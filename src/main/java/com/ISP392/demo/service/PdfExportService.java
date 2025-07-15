package com.ISP392.demo.service;

import com.ISP392.demo.entity.*;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;

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

    private static final String FONT_PATH = "src/main/resources/static/fonts/NotoSans-Regular.ttf";

    public ByteArrayInputStream exportAppointmentToPdf(AppointmentEntity appointment) throws IOException {
        PdfFont customFont = PdfFontFactory.createFont(FONT_PATH, PdfFontFactory.EmbeddingStrategy.PREFER_EMBEDDED);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(out);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        document.add(new Paragraph("Phiếu Khám Bệnh")
                .setFont(customFont)
                .setFontSize(18)
                .setBold());
        document.add(new Paragraph("\n"));

        document.add(new Paragraph("Bệnh nhân: " + appointment.getName())
                .setFont(customFont)
                .setFontSize(12));
        document.add(new Paragraph("Số điện thoại: " + appointment.getPhoneNumber())
                .setFont(customFont)
                .setFontSize(12));
        document.add(new Paragraph("Email: " + appointment.getEmail())
                .setFont(customFont)
                .setFontSize(12));
        document.add(new Paragraph("Thời gian hẹn: " + appointment.getAppointmentDateTime().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")))
                .setFont(customFont)
                .setFontSize(12));

        document.add(new Paragraph("\n"));

        if (appointment.getDoctor() != null) {
            document.add(new Paragraph("Bác sĩ: " + appointment.getDoctor().getFirstName() + " " + appointment.getDoctor().getLastName())
                    .setFont(customFont)
                    .setFontSize(12));
        }

        document.add(new Paragraph("\n"));

        Set<AppointmentServiceEntity> entities = appointment.getAppointmentServiceEntities();
        BigDecimal totalPrice = BigDecimal.ZERO;

        if (entities != null && !entities.isEmpty()) {
            document.add(new Paragraph("Dịch vụ/Chẩn đoán:")
                    .setFont(customFont)
                    .setFontSize(12)
                    .setBold());

            for (AppointmentServiceEntity one : entities) {
                document.add(new Paragraph("Dịch vụ sử dụng: " + one.getService().getContent() + " - Giá: " + one.getService().getPrice())
                        .setFont(customFont)
                        .setFontSize(12));
                document.add(new Paragraph("Đánh giá: " + one.getContent())
                        .setFont(customFont)
                        .setFontSize(12));

                document.add(new Paragraph("\n"));

                if (one.getService().getPrice() != null) {
                    totalPrice = totalPrice.add(one.getService().getPrice());
                }
            }
        } else {
            document.add(new Paragraph("Không có dịch vụ/Chẩn đoán nào.")
                    .setFont(customFont)
                    .setFontSize(12));
        }

        document.add(new Paragraph("\n"));

        ConclusionEntity conclusion = appointment.getConclusionEntity();
        if (conclusion != null) {
            document.add(new Paragraph("Kết luận: " + conclusion.getContent())
                    .setFont(customFont)
                    .setFontSize(12));
            document.add(new Paragraph("Đơn thuốc: ")
                    .setFont(customFont)
                    .setFontSize(12));

            String[] prescriptions = conclusion.getPrescription().split("\n");
            for (String prescription : prescriptions) {
                document.add(new Paragraph(prescription)
                        .setFont(customFont)
                        .setFontSize(12));
            }
        } else {
            document.add(new Paragraph("Kết luận và đơn thuốc chưa có.")
                    .setFont(customFont)
                    .setFontSize(12));
        }

        document.add(new Paragraph("\n"));
        document.add(new Paragraph("Tổng tiền: " + totalPrice + " VND")
                .setFont(customFont)
                .setFontSize(12)
                .setBold());

        document.add(new Paragraph("\n"));
        document.add(new Paragraph("Lưu ý: Phiếu khám bệnh này là một tài liệu quan trọng, vui lòng giữ gìn cẩn thận.")
                .setFont(customFont)
                .setFontSize(10));

        document.close();
        return new ByteArrayInputStream(out.toByteArray());
    }

    public ByteArrayInputStream exportMedicalSummaryPdf(AppointmentEntity appointment) throws IOException {
        PdfFont customFont = PdfFontFactory.createFont(FONT_PATH, PdfFontFactory.EmbeddingStrategy.PREFER_EMBEDDED);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(out);
        PdfDocument pdf = new PdfDocument(writer);
        Document doc = new Document(pdf);

        PatientEntity patient = appointment.getPatient();

        doc.add(new Paragraph("Cơ quan chủ quản. Cơ sở KB, CB.")
                .setFont(customFont).setBold());
        doc.add(new Paragraph("CỘNG HÒA XÃ HỘI CHỦ NGHĨA VIỆT NAM")
                .setFont(customFont).setBold().setTextAlignment(TextAlignment.CENTER));
        doc.add(new Paragraph("Độc lập - Tự do - Hạnh phúc")
                .setFont(customFont).setTextAlignment(TextAlignment.CENTER));
        doc.add(new Paragraph("\nBẢN TÓM TẮT HỒ SƠ BỆNH ÁN")
                .setFont(customFont).setBold().setTextAlignment(TextAlignment.CENTER));
        doc.add(new Paragraph("Mẫu số: 52/BV2").setFont(customFont).setItalic());

        doc.add(new Paragraph("\nI. HÀNH CHÍNH").setFont(customFont).setBold());
        doc.add(new Paragraph("Họ và tên (In hoa): " + (patient.getFirstName() + " " + patient.getLastName()).toUpperCase())
                .setFont(customFont));
        doc.add(new Paragraph("Giới tính: " + (patient.getGender() != null ? (patient.getGender().toString().equals("MALE") ? "Nam" : "Nữ") : ""))
                .setFont(customFont));
        doc.add(new Paragraph("Địa chỉ cư trú: " + (patient.getAddress() != null ? patient.getAddress() : ""))
                .setFont(customFont));
        doc.add(new Paragraph("Số CCCD/Hộ chiếu: " + (patient.getIdentification() != null ? patient.getIdentification() : ""))
                .setFont(customFont));
        doc.add(new Paragraph("Ngày sinh: " + (patient.getDateOfBirth() != null ? DateTimeFormatter.ofPattern("dd/MM/yyyy").format(patient.getDateOfBirth()) : ""))
                .setFont(customFont));
        doc.add(new Paragraph("Dân tộc: " + (patient.getNation() != null ? patient.getNation() : ""))
                .setFont(customFont));
        doc.add(new Paragraph("Ra viện ngày: " + (appointment.getAppointmentDateTime() != null ?
                DateTimeFormatter.ofPattern("dd/MM/yyyy").format(appointment.getAppointmentDateTime()) : ""))
                .setFont(customFont));

        doc.add(new Paragraph("\nII. CHẨN ĐOÁN (Tên bệnh và mã ICD đính kèm):").setFont(customFont).setBold());
        doc.add(new Paragraph("Vào viện ngày: " + (appointment.getAppointmentDateTime() != null ?
                DateTimeFormatter.ofPattern("dd/MM/yyyy").format(appointment.getAppointmentDateTime()) : ""))
                .setFont(customFont));
        doc.add(new Paragraph("Chẩn đoán vào viện: " + (appointment.getDiagnosis() != null ? appointment.getDiagnosis().getContent() : ""))
                .setFont(customFont));
        doc.add(new Paragraph("Chẩn đoán ra viện: " + (appointment.getConclusionEntity() != null ? appointment.getConclusionEntity().getContent() : ""))
                .setFont(customFont));

        doc.add(new Paragraph("\nIII. TÓM TẮT QUÁ TRÌNH ĐIỀU TRỊ").setFont(customFont).setBold());
        doc.add(new Paragraph("Tuổi: " + (appointment.getAge() != null ? appointment.getAge() : ""))
                .setFont(customFont));
        doc.add(new Paragraph("Tóm tắt quá trình bệnh lý và diễn biến lâm sàng:").setFont(customFont));

        doc.add(new Paragraph((appointment.getReason() != null ? appointment.getReason() : "Không có mô tả"))
                .setFont(customFont));

        // Đơn thuốc nếu có
        if (appointment.getConclusionEntity() != null && appointment.getConclusionEntity().getPrescription() != null) {
            doc.add(new Paragraph("\nĐơn thuốc:").setFont(customFont).setBold());
            String[] meds = appointment.getConclusionEntity().getPrescription().split("\n");
            for (String med : meds) {
                doc.add(new Paragraph("- " + med).setFont(customFont));
            }
        }

        doc.add(new Paragraph("\n(Ký tên, đóng dấu)").setTextAlignment(TextAlignment.RIGHT)
                .setFont(customFont).setItalic());

        doc.close();
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