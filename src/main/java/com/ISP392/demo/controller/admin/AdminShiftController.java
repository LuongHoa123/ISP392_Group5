package com.ISP392.demo.controller.admin;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ISP392.demo.entity.ShiftEntity;
import com.ISP392.demo.repository.DoctorRepository;
import com.ISP392.demo.repository.NurseRepository;
import com.ISP392.demo.repository.RoomRepository;
import com.ISP392.demo.repository.ShiftRepository;

@Controller
// Map với HTTP GET request
@RequestMapping("/admin/shift")
public class AdminShiftController {

    @Autowired
    private ShiftRepository shiftRepository;
    @Autowired
    private DoctorRepository doctorRepository;
    @Autowired
    private NurseRepository nurseRepository;
    @Autowired
    private RoomRepository roomRepository;
    
    @GetMapping("/api")
    @ResponseBody
    public List<Map<String, Object>> getShifts() {
        return shiftRepository.findAll().stream().map(s -> {
            Map<String, Object> ev = new HashMap<>();
            ev.put("id", s.getId());
            
            // Create title with person and room info
            String personInfo = "";
            if (s.getDoctor() != null) {
                personInfo = s.getDoctor().getFirstName() + " " + s.getDoctor().getLastName() + " (BS)";
            } else if (s.getNurse() != null) {
                personInfo = s.getNurse().getFirstName() + " " + s.getNurse().getLastName() + " (YT)";
            }
            
            String roomInfo = s.getRoom() != null ? " - " + s.getRoom().getRoomName() : "";
            ev.put("title", personInfo + roomInfo);
            
            ev.put("start", s.getStartTime().toString());
            ev.put("end", s.getEndTime().toString());
            ev.put("fixedTime", getFixedTimeFromStartTime(s.getStartTime()));
            ev.put("doctorId", s.getDoctor() != null ? s.getDoctor().getId() : null);
            ev.put("nurseId", s.getNurse() != null ? s.getNurse().getId() : null);
            ev.put("roomId", s.getRoom() != null ? s.getRoom().getId() : null);
            return ev;
        }).collect(Collectors.toList());
    }

    @PostMapping("/api/save")
    public ResponseEntity<?> saveShift(@RequestParam Map<String, String> formData,
                            RedirectAttributes redirectAttributes) {

        Long id = formData.get("id") != null && !formData.get("id").isEmpty()
                ? Long.valueOf(formData.get("id")) : null;

        String rawDate = formData.get("date");
        String onlyDate = rawDate.split("T")[0];
        LocalDate date = LocalDate.parse(onlyDate);

        String fixedTime = formData.get("fixedTime");
        LocalTime startTime = fixedTime.equals("MORNING") ? LocalTime.of(7, 0) : LocalTime.of(13, 0);
        LocalTime endTime = fixedTime.equals("MORNING") ? LocalTime.of(11, 0) : LocalTime.of(17, 0);

        LocalDateTime shiftStart = LocalDateTime.of(date, startTime);
        LocalDateTime shiftEnd = LocalDateTime.of(date, endTime);

        Long doctorId = formData.get("doctorId") != null && !formData.get("doctorId").isEmpty()
                ? Long.valueOf(formData.get("doctorId")) : null;
        Long nurseId = formData.get("nurseId") != null && !formData.get("nurseId").isEmpty()
                ? Long.valueOf(formData.get("nurseId")) : null;
        Long roomId = formData.get("roomId") != null && !formData.get("roomId").isEmpty()
                ? Long.valueOf(formData.get("roomId")) : null;

        // Check for conflicts
        List<ShiftEntity> conflicts = shiftRepository.findConflictingShifts(doctorId, nurseId, shiftStart, shiftEnd, id);
        if (!conflicts.isEmpty()) {
            return ResponseEntity.badRequest().body("Đã tồn tại ca làm trùng thời gian cho bác sĩ hoặc y tá.");
        }

        // Check room conflicts
        if (roomId != null) {
            List<ShiftEntity> roomConflicts = shiftRepository.findRoomConflictingShifts(roomId, shiftStart, shiftEnd, id);
            if (!roomConflicts.isEmpty()) {
                return ResponseEntity.badRequest().body("Phòng đã được sử dụng trong thời gian này.");
            }
        }

        ShiftEntity shift = id != null ? shiftRepository.findById(id).orElse(new ShiftEntity()) : new ShiftEntity();
        shift.setStartTime(shiftStart);
        shift.setEndTime(shiftEnd);

        // Set person
        if (doctorId != null) {
            shift.setDoctor(doctorRepository.findById(doctorId).orElse(null));
            shift.setNurse(null);
        } else if (nurseId != null) {
            shift.setNurse(nurseRepository.findById(nurseId).orElse(null));
            shift.setDoctor(null);
        } else {
            shift.setDoctor(null);
            shift.setNurse(null);
        }

        // Set room
        if (roomId != null) {
            shift.setRoom(roomRepository.findById(roomId).orElse(null));
        } else {
            shift.setRoom(null);
        }

        shiftRepository.save(shift);
        return ResponseEntity.ok("Lưu thành công");
    }


    @PostMapping("/api/delete/{id}")
    @ResponseBody
    public void deleteShift(@PathVariable Long id) {
        shiftRepository.deleteById(id);
    }

    @GetMapping
    public String listShifts(Model model,
                             @RequestParam(required = false) Long doctorId,
                             @RequestParam(required = false) Long nurseId,
                             @RequestParam(required = false) String date,
                             @RequestParam(defaultValue = "0") int page) {

        Pageable pageable = PageRequest.of(page, 10, Sort.by("startTime").descending());
        Page<ShiftEntity> shifts;

        if (doctorId != null) {
            shifts = shiftRepository.findByDoctorId(doctorId, pageable);
        } else if (nurseId != null) {
            shifts = shiftRepository.findByNurseId(nurseId, pageable);
        } else if (date != null && !date.isEmpty()) {
            LocalDate localDate = LocalDate.parse(date);
            LocalDateTime startOfDay = localDate.atStartOfDay();
            LocalDateTime endOfDay = localDate.atTime(23, 59, 59);
            shifts = shiftRepository.findByStartTimeBetween(startOfDay, endOfDay, pageable);
        } else {
            shifts = shiftRepository.findAll(pageable);
        }

        model.addAttribute("shifts", shifts.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", shifts.getTotalPages());
        model.addAttribute("doctors", doctorRepository.findAll());
        model.addAttribute("nurses", nurseRepository.findAll());
        model.addAttribute("rooms", roomRepository.findAll());
        model.addAttribute("selectedDoctorId", doctorId);
        model.addAttribute("selectedNurseId", nurseId);
        model.addAttribute("selectedDate", date);

        return "admin/shift/list";
    }



    private String getFixedTimeFromStartTime(LocalDateTime startTime) {
        if (startTime.toLocalTime().equals(LocalTime.of(7, 0))) {
            return "MORNING";
        } else if (startTime.toLocalTime().equals(LocalTime.of(13, 0))) {
            return "AFTERNOON";
        }
        return "UNKNOWN";
    }
}