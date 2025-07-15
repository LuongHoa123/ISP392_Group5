package com.ISP392.demo.controller.patient;

import com.ISP392.demo.config.VNPayConfig;
import com.ISP392.demo.entity.*;
import com.ISP392.demo.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;

@Controller
@RequestMapping("/patient/invoice")
public class PatientInvoiceController {


    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private InvoiceRepository invoiceRepository;

    private BigDecimal cost;
    private String fullName, phone, vnpTxtRef;
    private Long appointmentId = 0L;


    @PostMapping("/add")
    public String addInvoice(
                             @RequestParam String fullName,
                             @RequestParam String phone,
                             @RequestParam BigDecimal cost,
                             @RequestParam Long appointmentId,
                             RedirectAttributes redirectAttributes, Model model) {
        this.appointmentId = appointmentId;
        AppointmentEntity appointment = appointmentRepository.findById(appointmentId).orElse(null);
        if (appointment == null) {
            redirectAttributes.addFlashAttribute("addError", true);
            redirectAttributes.addFlashAttribute("addErrorMsg", "Không tìm thấy lịch hẹn.");
            return "redirect:/patient/appointment/calendar";
        }
        this.fullName = fullName;
        this.phone = phone;
        this.cost = cost;

        String vnp_TxnRef = VNPayConfig.getRandomNumber(8);
        this.vnpTxtRef = vnp_TxnRef;

        String vnp_TmnCode = VNPayConfig.vnp_TmnCode;
        String vnp_Version = "2.1.0";
        String vnp_Command = "pay";
        String vnp_IpAddr = "127.0.0.1";
        String vnp_CurrCode = "VND";
        String vnp_OrderInfo = "Thanh toán đơn mã #" + vnp_TxnRef;
        String vnp_BankCode = "NCB";

        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", vnp_Version);
        vnp_Params.put("vnp_Command", vnp_Command);
        vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
        vnp_Params.put("vnp_Amount", String.valueOf(cost.multiply(BigDecimal.valueOf(100)).longValue()));
        vnp_Params.put("vnp_CurrCode", vnp_CurrCode);
        vnp_Params.put("vnp_BankCode", vnp_BankCode);
        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
        vnp_Params.put("vnp_OrderInfo", vnp_OrderInfo);
        vnp_Params.put("vnp_OrderType", "other");
        vnp_Params.put("vnp_Locale", "vn");
        vnp_Params.put("vnp_ReturnUrl", VNPayConfig.vnp_ReturnUrl);
        vnp_Params.put("vnp_IpAddr", vnp_IpAddr);

        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        String vnp_CreateDate = formatter.format(calendar.getTime());
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

        calendar.add(Calendar.MINUTE, 15);
        String vnp_ExpireDate = formatter.format(calendar.getTime());
        vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

        List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
        Collections.sort(fieldNames);

        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();

        for (String fieldName : fieldNames) {
            String fieldValue = vnp_Params.get(fieldName);
            if (fieldValue != null && !fieldValue.isEmpty()) {
                hashData.append(fieldName).append('=')
                        .append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
                query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII)).append('=')
                        .append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
                if (fieldNames.indexOf(fieldName) < fieldNames.size() - 1) {
                    hashData.append('&');
                    query.append('&');
                }
            }
        }

        String vnp_SecureHash = VNPayConfig.hmacSHA512(VNPayConfig.secretKey, hashData.toString());
        query.append("&vnp_SecureHash=").append(vnp_SecureHash);

        String paymentUrl = VNPayConfig.vnp_PayUrl + "?" + query;
        return "redirect:" + paymentUrl;

    }

    @GetMapping("/getPaymentInfo")
    public String getPaymentInfo(@RequestParam("vnp_ResponseCode") String vnp_ResponseCode,
                                 RedirectAttributes redirectAttributes) {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity userEntity = userRepository.findByEmail(email).orElse(null);
        PatientEntity patient = patientRepository.findByUser(userEntity);

        if ("00".equals(vnp_ResponseCode)) {
            Long appointmentId = this.appointmentId;
            AppointmentEntity appointment = appointmentRepository.findById(appointmentId).orElse(null);

            InvoiceEntity invoiceEntity = new InvoiceEntity();
            invoiceEntity.setFullName(this.fullName);
            invoiceEntity.setVnpTxtRef(this.vnpTxtRef);
            invoiceEntity.setPhone(this.phone);
            invoiceEntity.setTotalCost(this.cost);
            invoiceEntity.setPatient(patient);
            invoiceEntity.setCreatedAt(LocalDateTime.now());
            invoiceEntity.setAppointmentEntity(appointment);
            invoiceRepository.save(invoiceEntity);

            List<InvoiceEntity> allInvoices = invoiceRepository.findAllByAppointmentEntity_Id(appointmentId);
            BigDecimal paidTotal = allInvoices.stream()
                    .map(InvoiceEntity::getTotalCost)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            appointment.setPayCost(paidTotal);

            if (paidTotal.compareTo(appointment.getTotalCost()) >= 0) {
                appointment.setPaymentStatus(1);
            }

            appointmentRepository.save(appointment);

            redirectAttributes.addFlashAttribute("invoice", invoiceEntity);
            redirectAttributes.addFlashAttribute("successMessage", "Thanh toán thành công!");

        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Thanh toán thất bại! Mã lỗi: " + vnp_ResponseCode);
        }

        return "redirect:/patient/appointment/detail?id=" + this.appointmentId;
    }
}
