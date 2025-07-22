package com.ISP392.demo.service;

import com.ISP392.demo.entity.AppointmentEntity;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class PdfService {
    public String uploadCertificate(MultipartFile file, Long doctorId) {
        if (file != null && !file.isEmpty()) {
            try {
                String baseDir = System.getProperty("user.dir");
                String uploadDir = baseDir + "/uploads/certificates";
                Files.createDirectories(Paths.get(uploadDir));

                String filename = "doctor_" + doctorId + "_certificate.pdf";
                String fullPath = uploadDir + "/" + filename;

                file.transferTo(new File(fullPath));

                return "/uploads/certificates/" + filename;
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException("Upload failed");
            }
        }
        return null;
    }

} 