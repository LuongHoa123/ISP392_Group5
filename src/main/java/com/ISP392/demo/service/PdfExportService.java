package com.ISP392.demo.service;

import com.ISP392.demo.entity.*;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.borders.SolidBorder;
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
    private static final String FONT_PATH = "src/main/resources/static/fonts/NotoSans-Regular.ttf";

    public ByteArrayInputStream exportAppointmentsToPdf(List<AppointmentEntity> appointments) throws IOException {
        PdfFont customFont = PdfFontFactory.createFont(FONT_PATH, PdfFontFactory.EmbeddingStrategy.PREFER_EMBEDDED);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(out);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        // Title
        document.add(new Paragraph("Danh Sách Lịch Hẹn")
                .setFont(customFont)
                .setFontSize(16)
                .setBold()
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(20));

        String[] columns = {
                "STT", "Họ tên bệnh nhân", "Số điện thoại", "Email",
                "Thời gian hẹn", "Lý do khám", "Phòng khám", "Trạng thái"
        };
        float[] columnWidths = {5F, 20F, 15F, 25F, 15F, 20F, 15F, 15F};
        Table table = new Table(UnitValue.createPercentArray(columnWidths))
                .useAllAvailableWidth()
                .setBorder(new SolidBorder(ColorConstants.LIGHT_GRAY, 1));

        // Header styling
        for (String col : columns) {
            table.addHeaderCell(new Cell()
                    .add(new Paragraph(col)
                            .setFont(customFont)
                            .setFontSize(10)
                            .setBold()
                            .setTextAlignment(TextAlignment.CENTER))
                    .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                    .setBorder(new SolidBorder(ColorConstants.BLACK, 0.5f))
                    .setPadding(8));
        }

        // Table data
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        int rowNum = 1;
        for (AppointmentEntity appointment : appointments) {
            table.addCell(new Cell()
                    .add(new Paragraph(String.valueOf(rowNum))
                            .setFont(customFont)
                            .setFontSize(9)
                            .setTextAlignment(TextAlignment.CENTER))
                    .setBorder(new SolidBorder(ColorConstants.LIGHT_GRAY, 0.5f))
                    .setPadding(6));
            table.addCell(new Cell()
                    .add(new Paragraph(appointment.getName() != null ? appointment.getName() : "")
                            .setFont(customFont)
                            .setFontSize(9))
                    .setBorder(new SolidBorder(ColorConstants.LIGHT_GRAY, 0.5f))
                    .setPadding(6));
            table.addCell(new Cell()
                    .add(new Paragraph(appointment.getPhoneNumber() != null ? appointment.getPhoneNumber() : "")
                            .setFont(customFont)
                            .setFontSize(9))
                    .setBorder(new SolidBorder(ColorConstants.LIGHT_GRAY, 0.5f))
                    .setPadding(6));
            table.addCell(new Cell()
                    .add(new Paragraph(appointment.getEmail() != null ? appointment.getEmail() : "")
                            .setFont(customFont)
                            .setFontSize(9))
                    .setBorder(new SolidBorder(ColorConstants.LIGHT_GRAY, 0.5f))
                    .setPadding(6));
            table.addCell(new Cell()
                    .add(new Paragraph(appointment.getAppointmentDateTime() != null ? appointment.getAppointmentDateTime().format(formatter) : "")
                            .setFont(customFont)
                            .setFontSize(9)
                            .setTextAlignment(TextAlignment.CENTER))
                    .setBorder(new SolidBorder(ColorConstants.LIGHT_GRAY, 0.5f))
                    .setPadding(6));
            table.addCell(new Cell()
                    .add(new Paragraph(appointment.getReason() != null ? appointment.getReason() : "")
                            .setFont(customFont)
                            .setFontSize(9))
                    .setBorder(new SolidBorder(ColorConstants.LIGHT_GRAY, 0.5f))
                    .setPadding(6));
            table.addCell(new Cell()
                    .add(new Paragraph(appointment.getRoom() != null ? appointment.getRoom().getRoomName() : "")
                            .setFont(customFont)
                            .setFontSize(9))
                    .setBorder(new SolidBorder(ColorConstants.LIGHT_GRAY, 0.5f))
                    .setPadding(6));
            table.addCell(new Cell()
                    .add(new Paragraph(getStatusText(appointment.getStatus()))
                            .setFont(customFont)
                            .setFontSize(9)
                            .setTextAlignment(TextAlignment.CENTER))
                    .setBorder(new SolidBorder(ColorConstants.LIGHT_GRAY, 0.5f))
                    .setPadding(6));
            rowNum++;
        }

        document.add(table);
        document.close();
        return new ByteArrayInputStream(out.toByteArray());
    }

    public ByteArrayInputStream exportAppointmentToPdf(AppointmentEntity appointment) throws IOException {
        PdfFont customFont = PdfFontFactory.createFont(FONT_PATH, PdfFontFactory.EmbeddingStrategy.PREFER_EMBEDDED);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(out);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        // Header
        document.add(new Paragraph("Phiếu Khám Bệnh")
                .setFont(customFont)
                .setFontSize(18)
                .setBold()
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(15));
        document.add(new Paragraph("Cơ sở y tế: Veritas ENT")
                .setFont(customFont)
                .setFontSize(12)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(20));

        // Patient Info
        document.add(new Paragraph("Thông Tin Bệnh Nhân")
                .setFont(customFont)
                .setFontSize(14)
                .setBold()
                .setMarginTop(10)
                .setMarginBottom(10));
        document.add(new Paragraph("Họ tên: " + (appointment.getName() != null ? appointment.getName() : ""))
                .setFont(customFont)
                .setFontSize(11)
                .setMarginLeft(10));
        document.add(new Paragraph("Số điện thoại: " + (appointment.getPhoneNumber() != null ? appointment.getPhoneNumber() : ""))
                .setFont(customFont)
                .setFontSize(11)
                .setMarginLeft(10));
        document.add(new Paragraph("Email: " + (appointment.getEmail() != null ? appointment.getEmail() : ""))
                .setFont(customFont)
                .setFontSize(11)
                .setMarginLeft(10));
        document.add(new Paragraph("Thời gian hẹn: " + (appointment.getAppointmentDateTime() != null ? appointment.getAppointmentDateTime().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) : ""))
                .setFont(customFont)
                .setFontSize(11)
                .setMarginLeft(10));

        // Doctor Info
        if (appointment.getDoctor() != null) {
            document.add(new Paragraph("Bác sĩ: " + appointment.getDoctor().getFirstName() + " " + appointment.getDoctor().getLastName())
                    .setFont(customFont)
                    .setFontSize(11)
                    .setMarginLeft(10)
                    .setMarginTop(10));
        }

        // Services/Diagnosis
        Set<AppointmentServiceEntity> entities = appointment.getAppointmentServiceEntities();
        BigDecimal totalPrice = BigDecimal.ZERO;

        document.add(new Paragraph("Dịch vụ/Chẩn đoán")
                .setFont(customFont)
                .setFontSize(14)
                .setBold()
                .setMarginTop(20)
                .setMarginBottom(10));

        if (entities != null && !entities.isEmpty()) {
            float[] serviceColumnWidths = {50F, 20F, 30F};
            Table serviceTable = new Table(UnitValue.createPercentArray(serviceColumnWidths))
                    .useAllAvailableWidth()
                    .setBorder(new SolidBorder(ColorConstants.LIGHT_GRAY, 1));

            // Service table headers
            serviceTable.addHeaderCell(new Cell()
                    .add(new Paragraph("Dịch vụ sử dụng")
                            .setFont(customFont)
                            .setFontSize(10)
                            .setBold()
                            .setTextAlignment(TextAlignment.CENTER))
                    .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                    .setBorder(new SolidBorder(ColorConstants.BLACK, 0.5f))
                    .setPadding(8));
            serviceTable.addHeaderCell(new Cell()
                    .add(new Paragraph("Giá")
                            .setFont(customFont)
                            .setFontSize(10)
                            .setBold()
                            .setTextAlignment(TextAlignment.CENTER))
                    .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                    .setBorder(new SolidBorder(ColorConstants.BLACK, 0.5f))
                    .setPadding(8));
            serviceTable.addHeaderCell(new Cell()
                    .add(new Paragraph("Đánh giá")
                            .setFont(customFont)
                            .setFontSize(10)
                            .setBold()
                            .setTextAlignment(TextAlignment.CENTER))
                    .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                    .setBorder(new SolidBorder(ColorConstants.BLACK, 0.5f))
                    .setPadding(8));

            // Service table data
            for (AppointmentServiceEntity one : entities) {
                serviceTable.addCell(new Cell()
                        .add(new Paragraph(one.getService().getContent() != null ? one.getService().getContent() : "")
                                .setFont(customFont)
                                .setFontSize(9))
                        .setBorder(new SolidBorder(ColorConstants.LIGHT_GRAY, 0.5f))
                        .setPadding(6));
                serviceTable.addCell(new Cell()
                        .add(new Paragraph(one.getService().getPrice() != null ? one.getService().getPrice() + " VND" : "")
                                .setFont(customFont)
                                .setFontSize(9)
                                .setTextAlignment(TextAlignment.RIGHT))
                        .setBorder(new SolidBorder(ColorConstants.LIGHT_GRAY, 0.5f))
                        .setPadding(6));
                serviceTable.addCell(new Cell()
                        .add(new Paragraph(one.getContent() != null ? one.getContent() : "")
                                .setFont(customFont)
                                .setFontSize(9))
                        .setBorder(new SolidBorder(ColorConstants.LIGHT_GRAY, 0.5f))
                        .setPadding(6));

                if (one.getService().getPrice() != null) {
                    totalPrice = totalPrice.add(one.getService().getPrice());
                }
            }
            document.add(serviceTable);
        } else {
            document.add(new Paragraph("Không có dịch vụ/Chẩn đoán nào.")
                    .setFont(customFont)
                    .setFontSize(11)
                    .setMarginLeft(10));
        }

        // Conclusion
        ConclusionEntity conclusion = appointment.getConclusionEntity();
        document.add(new Paragraph("Kết luận và Đơn thuốc")
                .setFont(customFont)
                .setFontSize(14)
                .setBold()
                .setMarginTop(20)
                .setMarginBottom(10));

        if (conclusion != null) {
            document.add(new Paragraph("Kết luận: " + (conclusion.getContent() != null ? conclusion.getContent() : ""))
                    .setFont(customFont)
                    .setFontSize(11)
                    .setMarginLeft(10));
            document.add(new Paragraph("Đơn thuốc:")
                    .setFont(customFont)
                    .setFontSize(11)
                    .setBold()
                    .setMarginLeft(10)
                    .setMarginTop(10));

            String[] prescriptions = conclusion.getPrescription() != null ? conclusion.getPrescription().split("\n") : new String[]{};
            for (String prescription : prescriptions) {
                document.add(new Paragraph("- " + prescription)
                        .setFont(customFont)
                        .setFontSize(11)
                        .setMarginLeft(20));
            }
        } else {
            document.add(new Paragraph("Kết luận và đơn thuốc chưa có.")
                    .setFont(customFont)
                    .setFontSize(11)
                    .setMarginLeft(10));
        }

        // Total Price
        document.add(new Paragraph("Tổng tiền: " + totalPrice + " VND")
                .setFont(customFont)
                .setFontSize(12)
                .setBold()
                .setTextAlignment(TextAlignment.RIGHT)
                .setMarginTop(20));

        // Footer Note
        document.add(new Paragraph("Lưu ý: Phiếu khám bệnh này là một tài liệu quan trọng, vui lòng giữ gìn cẩn thận.")
                .setFont(customFont)
                .setFontSize(10)
                .setItalic()
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginTop(20));

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

        // Header
        doc.add(new Paragraph("Cơ quan chủ quản: Veritas ENT")
                .setFont(customFont)
                .setFontSize(12)
                .setBold()
                .setTextAlignment(TextAlignment.LEFT));
        doc.add(new Paragraph("CỘNG HÒA XÃ HỘI CHỦ NGHĨA VIỆT NAM")
                .setFont(customFont)
                .setFontSize(14)
                .setBold()
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginTop(10));
        doc.add(new Paragraph("Độc lập - Tự do - Hạnh phúc")
                .setFont(customFont)
                .setFontSize(12)
                .setTextAlignment(TextAlignment.CENTER));
        doc.add(new Paragraph("BẢN TÓM TẮT HỒ SƠ BỆNH ÁN")
                .setFont(customFont)
                .setFontSize(16)
                .setBold()
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginTop(10));
        doc.add(new Paragraph("Mẫu số: 52/BV2")
                .setFont(customFont)
                .setFontSize(10)
                .setItalic()
                .setTextAlignment(TextAlignment.RIGHT));

        // Section I: Administrative Info
        doc.add(new Paragraph("\nI. HÀNH CHÍNH")
                .setFont(customFont)
                .setFontSize(12)
                .setBold()
                .setMarginTop(20));
        doc.add(new Paragraph("Họ và tên (In hoa): " + (patient.getFirstName() + " " + patient.getLastName()).toUpperCase())
                .setFont(customFont)
                .setFontSize(11)
                .setMarginLeft(10));
        doc.add(new Paragraph("Giới tính: " + (patient.getGender() != null ? (patient.getGender().toString().equals("MALE") ? "Nam" : "Nữ") : ""))
                .setFont(customFont)
                .setFontSize(11)
                .setMarginLeft(10));
        doc.add(new Paragraph("Địa chỉ cư trú: " + (patient.getAddress() != null ? patient.getAddress() : ""))
                .setFont(customFont)
                .setFontSize(11)
                .setMarginLeft(10));
        doc.add(new Paragraph("Số CCCD/Hộ chiếu: " + (patient.getIdentification() != null ? patient.getIdentification() : ""))
                .setFont(customFont)
                .setFontSize(11)
                .setMarginLeft(10));
        doc.add(new Paragraph("Ngày sinh: " + (patient.getDateOfBirth() != null ? DateTimeFormatter.ofPattern("dd/MM/yyyy").format(patient.getDateOfBirth()) : ""))
                .setFont(customFont)
                .setFontSize(11)
                .setMarginLeft(10));
        doc.add(new Paragraph("Dân tộc: " + (patient.getNation() != null ? patient.getNation() : ""))
                .setFont(customFont)
                .setFontSize(11)
                .setMarginLeft(10));
        doc.add(new Paragraph("Ra viện ngày: " + (appointment.getAppointmentDateTime() != null ?
                DateTimeFormatter.ofPattern("dd/MM/yyyy").format(appointment.getAppointmentDateTime()) : ""))
                .setFont(customFont)
                .setFontSize(11)
                .setMarginLeft(10));

        // Section II: Diagnosis
        doc.add(new Paragraph("\nII. CHẨN ĐOÁN (Tên bệnh và mã ICD đính kèm):")
                .setFont(customFont)
                .setFontSize(12)
                .setBold()
                .setMarginTop(20));
        doc.add(new Paragraph("Vào viện ngày: " + (appointment.getAppointmentDateTime() != null ?
                DateTimeFormatter.ofPattern("dd/MM/yyyy").format(appointment.getAppointmentDateTime()) : ""))
                .setFont(customFont)
                .setFontSize(11)
                .setMarginLeft(10));
        doc.add(new Paragraph("Chẩn đoán vào viện: " + (appointment.getDiagnosis() != null ? appointment.getDiagnosis().getContent() : ""))
                .setFont(customFont)
                .setFontSize(11)
                .setMarginLeft(10));
        doc.add(new Paragraph("Chẩn đoán ra viện: " + (appointment.getConclusionEntity() != null ? appointment.getConclusionEntity().getContent() : ""))
                .setFont(customFont)
                .setFontSize(11)
                .setMarginLeft(10));

        // Section III: Treatment Summary
        doc.add(new Paragraph("\nIII. TÓM TẮT QUÁ TRÌNH ĐIỀU TRỊ")
                .setFont(customFont)
                .setFontSize(12)
                .setBold()
                .setMarginTop(20));
        doc.add(new Paragraph("Tuổi: " + (appointment.getAge() != null ? appointment.getAge() : ""))
                .setFont(customFont)
                .setFontSize(11)
                .setMarginLeft(10));
        doc.add(new Paragraph("Tóm tắt quá trình bệnh lý và diễn biến lâm sàng:")
                .setFont(customFont)
                .setFontSize(11)
                .setBold()
                .setMarginLeft(10)
                .setMarginTop(10));
        doc.add(new Paragraph((appointment.getReason() != null ? appointment.getReason() : "Không có mô tả"))
                .setFont(customFont)
                .setFontSize(11)
                .setMarginLeft(20));

        // Prescription
        if (appointment.getConclusionEntity() != null && appointment.getConclusionEntity().getPrescription() != null) {
            doc.add(new Paragraph("\nĐơn thuốc:")
                    .setFont(customFont)
                    .setFontSize(11)
                    .setBold()
                    .setMarginLeft(10)
                    .setMarginTop(10));
            String[] meds = appointment.getConclusionEntity().getPrescription().split("\n");
            for (String med : meds) {
                doc.add(new Paragraph("- " + med)
                        .setFont(customFont)
                        .setFontSize(11)
                        .setMarginLeft(20));
            }
        }

        // Signature
        doc.add(new Paragraph("\n(Ký tên, đóng dấu)")
                .setFont(customFont)
                .setFontSize(10)
                .setItalic()
                .setTextAlignment(TextAlignment.RIGHT)
                .setMarginTop(20));

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

    public ByteArrayInputStream exportPatientsToPdf(List<PatientEntity> patients) throws IOException {
        PdfFont font = PdfFontFactory.createFont(FONT_PATH, PdfFontFactory.EmbeddingStrategy.PREFER_EMBEDDED);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(out);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        // Title
        document.add(new Paragraph("Danh Sách Bệnh Nhân Khám Trong Ngày")
                .setFont(font)
                .setFontSize(16)
                .setBold()
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(20));

        String[] columns = {"STT", "Họ", "Tên", "Ngày sinh", "Giới tính", "Số điện thoại"};
        float[] columnWidths = {5F, 15F, 15F, 15F, 10F, 20F};
        Table table = new Table(UnitValue.createPercentArray(columnWidths))
                .useAllAvailableWidth()
                .setBorder(new SolidBorder(ColorConstants.LIGHT_GRAY, 1));

        // Header
        for (String col : columns) {
            table.addHeaderCell(new Cell()
                    .add(new Paragraph(col).setFont(font).setFontSize(10).setBold().setTextAlignment(TextAlignment.CENTER))
                    .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                    .setBorder(new SolidBorder(ColorConstants.BLACK, 0.5f))
                    .setPadding(8));
        }

        int index = 1;
        for (PatientEntity p : patients) {
            table.addCell(new Cell().add(new Paragraph(String.valueOf(index))).setFont(font).setFontSize(9).setTextAlignment(TextAlignment.CENTER).setPadding(6));
            table.addCell(new Cell().add(new Paragraph(p.getFirstName() != null ? p.getFirstName() : "")).setFont(font).setFontSize(9).setPadding(6));
            table.addCell(new Cell().add(new Paragraph(p.getLastName() != null ? p.getLastName() : "")).setFont(font).setFontSize(9).setPadding(6));
            String dob = p.getDateOfBirth() != null ? p.getDateOfBirth().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "";
            table.addCell(new Cell().add(new Paragraph(dob)).setFont(font).setFontSize(9).setTextAlignment(TextAlignment.CENTER).setPadding(6));
            String gender = p.getGender() != null ? (p.getGender().name().equals("MALE") ? "Nam" : "Nữ") : "";
            table.addCell(new Cell().add(new Paragraph(gender)).setFont(font).setFontSize(9).setTextAlignment(TextAlignment.CENTER).setPadding(6));
            table.addCell(new Cell().add(new Paragraph(p.getPhone() != null ? p.getPhone() : "")).setFont(font).setFontSize(9).setPadding(6));

            index++;
        }

        document.add(table);
        document.close();

        return new ByteArrayInputStream(out.toByteArray());
    }
}