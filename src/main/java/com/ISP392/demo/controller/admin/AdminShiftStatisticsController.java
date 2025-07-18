package com.ISP392.demo.controller.admin;

import com.ISP392.demo.entity.DoctorEntity;
import com.ISP392.demo.entity.NurseEntity;
import com.ISP392.demo.entity.ShiftEntity;
import com.ISP392.demo.repository.DoctorRepository;
import com.ISP392.demo.repository.NurseRepository;
import com.ISP392.demo.repository.ShiftRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/shift/statistics")
public class AdminShiftStatisticsController {

    @Autowired
    private ShiftRepository shiftRepository;
    
    @Autowired
    private DoctorRepository doctorRepository;
    
    @Autowired
    private NurseRepository nurseRepository;

    @GetMapping
    public String showStatistics(Model model,
                                @RequestParam(value = "search", required = false) String search,
                                @RequestParam(value = "shiftType", required = false) String shiftType,
                                @RequestParam(value = "week", required = false) String weekParam,
                                @RequestParam(value = "page", defaultValue = "0") int page,
                                @RequestParam(value = "size", defaultValue = "5") int size) {
        
        // Xác định tuần hiện tại hoặc tuần được chọn
        LocalDate currentWeek = weekParam != null && !weekParam.isEmpty() 
            ? LocalDate.parse(weekParam) 
            : LocalDate.now().with(TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY));
        
        // Tính toán ngày đầu và cuối tuần
        LocalDate startOfWeek = currentWeek.with(TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY));
        LocalDate endOfWeek = startOfWeek.plusDays(6);
        
        LocalDateTime startDateTime = startOfWeek.atStartOfDay();
        LocalDateTime endDateTime = endOfWeek.atTime(LocalTime.MAX);
        
        // Lấy tất cả ca trực trong tuần
        List<ShiftEntity> weeklyShifts = shiftRepository.findByStartTimeBetween(startDateTime, endDateTime, 
            PageRequest.of(0, Integer.MAX_VALUE)).getContent();
        
        // Lấy danh sách tất cả bác sĩ và y tá
        List<DoctorEntity> allDoctors = doctorRepository.findAll();
        List<NurseEntity> allNurses = nurseRepository.findAll();
        
        // Tạo dữ liệu thống kê
        List<Map<String, Object>> statisticsData = new ArrayList<>();
        
        // Thống kê cho bác sĩ (lấy hết, không filter)
        for (DoctorEntity doctor : allDoctors) {
            Map<String, Object> doctorStats = createPersonStatistics(doctor, null, weeklyShifts, startOfWeek);
            statisticsData.add(doctorStats);
        }
        
        // Thống kê cho y tá (lấy hết, không filter)
        for (NurseEntity nurse : allNurses) {
            Map<String, Object> nurseStats = createPersonStatistics(null, nurse, weeklyShifts, startOfWeek);
            statisticsData.add(nurseStats);
        }
        
        // Áp dụng filter theo tìm kiếm
        if (search != null && !search.trim().isEmpty()) {
            String searchLower = search.toLowerCase();
            statisticsData = statisticsData.stream()
                .filter(stats -> {
                    String name = (String) stats.get("name");
                    return name.toLowerCase().contains(searchLower);
                })
                .collect(Collectors.toList());
        }
        
        // Áp dụng filter theo loại ca
        if (shiftType != null && !shiftType.trim().isEmpty()) {
            if ("MORNING".equals(shiftType)) {
                statisticsData = statisticsData.stream()
                    .filter(stats -> (Integer) stats.get("morningShifts") > 0)
                    .collect(Collectors.toList());
            } else if ("AFTERNOON".equals(shiftType)) {
                statisticsData = statisticsData.stream()
                    .filter(stats -> (Integer) stats.get("afternoonShifts") > 0)
                    .collect(Collectors.toList());
            }
        }
        
        // Sắp xếp theo tổng số ca (descending)
        statisticsData.sort((a, b) -> Integer.compare((Integer) b.get("totalShifts"), (Integer) a.get("totalShifts")));
        
        // Phân trang
        int totalItems = statisticsData.size();
        int totalPages = (int) Math.ceil((double) totalItems / size);
        int start = Math.min(page * size, totalItems);
        int end = Math.min(start + size, totalItems);
        
        List<Map<String, Object>> pagedData = statisticsData.subList(start, end);
        
        // Tạo dữ liệu cho biểu đồ (lấy tất cả dữ liệu đã filter)
        List<Map<String, Object>> chartData = new ArrayList<>(statisticsData);
        
        // Tính toán tuần trước và tuần sau
        LocalDate previousWeek = startOfWeek.minusWeeks(1);
        LocalDate nextWeek = startOfWeek.plusWeeks(1);
        
        // Thêm dữ liệu vào model
        model.addAttribute("statistics", pagedData);
        model.addAttribute("chartData", chartData);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("totalItems", totalItems);
        model.addAttribute("size", size);
        model.addAttribute("search", search);
        model.addAttribute("shiftType", shiftType);
        model.addAttribute("currentWeek", startOfWeek);
        model.addAttribute("weekTitle", formatWeekTitle(startOfWeek, endOfWeek));
        model.addAttribute("previousWeek", previousWeek);
        model.addAttribute("nextWeek", nextWeek);
        
        return "admin/shift/statistics";
    }
    
    private Map<String, Object> createPersonStatistics(DoctorEntity doctor, NurseEntity nurse, 
                                                      List<ShiftEntity> weeklyShifts, LocalDate startOfWeek) {
        
        String name = doctor != null ? doctor.getFirstName() + " " + doctor.getLastName() : 
                     nurse != null ? nurse.getFirstName() + " " + nurse.getLastName() : "";
        String position = doctor != null ? "Bác sĩ" : "Y tá";
        String department = doctor != null ? doctor.getSpecialization() : ""; // Y tá để trống
        
        // Lọc ca trực của người này
        List<ShiftEntity> personShifts = weeklyShifts.stream()
            .filter(shift -> doctor != null ? 
                (shift.getDoctor() != null && shift.getDoctor().getId().equals(doctor.getId())) :
                (shift.getNurse() != null && shift.getNurse().getId().equals(nurse.getId())))
            .collect(Collectors.toList());
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("name", name);
        stats.put("position", position);
        stats.put("department", department);
        
        // Tính toán ca trực theo từng ngày trong tuần
        String[] weekDays = {"monday", "tuesday", "wednesday", "thursday", "friday", "saturday", "sunday"};
        int totalShifts = 0;
        int morningShifts = 0;
        int afternoonShifts = 0;
        
        for (int i = 0; i < 7; i++) {
            LocalDate currentDay = startOfWeek.plusDays(i);
            List<ShiftEntity> dayShifts = personShifts.stream()
                .filter(shift -> shift.getStartTime().toLocalDate().equals(currentDay))
                .collect(Collectors.toList());
            
            if (dayShifts.isEmpty()) {
                stats.put(weekDays[i], "Nghỉ");
            } else {
                // Sửa logic để chỉ lấy 1 shift cho mỗi ca (sáng/chiều)
                String morningShiftInfo = "";
                String afternoonShiftInfo = "";
                
                // Lấy ca sáng duy nhất
                Optional<ShiftEntity> morningShift = dayShifts.stream()
                    .filter(shift -> shift.getStartTime().toLocalTime().equals(LocalTime.of(7, 0)))
                    .findFirst();
                
                if (morningShift.isPresent()) {
                    String roomInfo = morningShift.get().getRoom() != null ? 
                        " (" + morningShift.get().getRoom().getRoomName() + ")" : "";
                    morningShiftInfo = "Sáng" + roomInfo;
                    morningShifts++;
                    totalShifts++;
                }
                
                // Lấy ca chiều duy nhất
                Optional<ShiftEntity> afternoonShift = dayShifts.stream()
                    .filter(shift -> shift.getStartTime().toLocalTime().equals(LocalTime.of(13, 0)))
                    .findFirst();
                
                if (afternoonShift.isPresent()) {
                    String roomInfo = afternoonShift.get().getRoom() != null ? 
                        " (" + afternoonShift.get().getRoom().getRoomName() + ")" : "";
                    afternoonShiftInfo = "Chiều" + roomInfo;
                    afternoonShifts++;
                    totalShifts++;
                }
                
                // Kết hợp ca sáng và ca chiều
                List<String> shiftParts = new ArrayList<>();
                if (!morningShiftInfo.isEmpty()) {
                    shiftParts.add(morningShiftInfo);
                }
                if (!afternoonShiftInfo.isEmpty()) {
                    shiftParts.add(afternoonShiftInfo);
                }
                
                stats.put(weekDays[i], shiftParts.isEmpty() ? "Nghỉ" : String.join(", ", shiftParts));
            }
        }
        
        stats.put("totalShifts", totalShifts);
        stats.put("morningShifts", morningShifts);
        stats.put("afternoonShifts", afternoonShifts);
        stats.put("notes", "");
        
        return stats;
    }
    
    private String formatWeekTitle(LocalDate startOfWeek, LocalDate endOfWeek) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return String.format("Tuần %d, Tháng %d/%d", 
            startOfWeek.get(java.time.temporal.WeekFields.ISO.weekOfYear()),
            startOfWeek.getMonthValue(), 
            startOfWeek.getYear());
    }
} 