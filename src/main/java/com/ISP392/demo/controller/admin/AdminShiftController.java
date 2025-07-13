package com.ISP392.demo.controller.admin;

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
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/shift")
public class AdminShiftController {

    @Autowired
    private ShiftRepository shiftRepository;
    @Autowired
    private DoctorRepository doctorRepository;
    @Autowired
    private NurseRepository nurseRepository;
    @GetMapping("/api")
    @ResponseBody
    public List<Map<String, Object>> getShifts() {
        return shiftRepository.findAll().stream().map(s -> {
            Map<String, Object> ev = new HashMap<>();
            ev.put("id", s.getId());
            ev.put("title", s.getDoctor() != null ? s.getDoctor().getFirstName() + " " +  s.getDoctor().getLastName() : s.getNurse().getFirstName() + " " +  s.getNurse().getLastName());
            ev.put("start", s.getStartTime().toString());
            ev.put("end", s.getEndTime().toString());
            ev.put("fixedTime", getFixedTimeFromStartTime(s.getStartTime()));
            ev.put("doctorId", s.getDoctor() != null ? s.getDoctor().getId() : null);
            ev.put("nurseId", s.getNurse() != null ? s.getNurse().getId() : null);
            return ev;
        }).collect(Collectors.toList());
    }

    @PostMapping("/api/save")
    public String saveShift(@RequestParam Map<String, String> formData) {
        Long id = formData.get("id") != null && !formData.get("id").isEmpty()
                ? Long.valueOf(formData.get("id")) : null;

        String rawDate = formData.get("date");
        String onlyDate = rawDate.split("T")[0];

        LocalDate date = LocalDate.parse(onlyDate);

        String fixedTime = formData.get("fixedTime");

        LocalTime startTime = fixedTime.equals("MORNING") ? LocalTime.of(7, 0) : LocalTime.of(13, 0);
        LocalTime endTime = fixedTime.equals("MORNING") ? LocalTime.of(11, 0) : LocalTime.of(17, 0);

        ShiftEntity shift = id != null ? shiftRepository.findById(id).orElse(new ShiftEntity()) : new ShiftEntity();
        shift.setStartTime(LocalDateTime.of(date, startTime));
        shift.setEndTime(LocalDateTime.of(date, endTime));

        if (formData.get("doctorId") != null && !formData.get("doctorId").isEmpty()) {
            shift.setDoctor(doctorRepository.findById(Long.valueOf(formData.get("doctorId"))).orElse(null));
            shift.setNurse(null);
        } else if (formData.get("nurseId") != null && !formData.get("nurseId").isEmpty()) {
            shift.setNurse(nurseRepository.findById(Long.valueOf(formData.get("nurseId"))).orElse(null));
            shift.setDoctor(null);
        } else {
            shift.setDoctor(null);
            shift.setNurse(null);
        }

        shiftRepository.save(shift);
        return "redirect:/admin/shift";
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
        model.addAttribute("selectedDoctorId", doctorId);
        model.addAttribute("selectedNurseId", nurseId);
        model.addAttribute("selectedDate", date);

        return "admin/shift/list";
    }


    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("shift", new ShiftEntity());
        model.addAttribute("doctors", doctorRepository.findAll());
        model.addAttribute("nurses", nurseRepository.findAll());
        return "admin/shift/shift-form";
    }

    @PostMapping("/add")
    public String saveNew(@RequestParam("shiftDate") String dateStr,
                          @RequestParam("fixedTime") String fixedTime,
                          @ModelAttribute ShiftEntity shift, Model model) {

        try {
            LocalDate date = LocalDate.parse(dateStr);
            LocalDateTime start, end;

            if ("MORNING".equals(fixedTime)) {
                start = date.atTime(7, 0);
                end = date.atTime(11, 0);
            } else if ("AFTERNOON".equals(fixedTime)) {
                start = date.atTime(13, 0);
                end = date.atTime(17, 0);
            } else {
                throw new IllegalArgumentException("Khung giờ không hợp lệ");
            }
            if (shift.getDoctor() != null && shift.getNurse() != null ||
                    shift.getDoctor() == null && shift.getNurse() == null) {
                model.addAttribute("error", "Chỉ được chọn bác sĩ hoặc y tá.");
                model.addAttribute("shift", shift);
                model.addAttribute("doctors", doctorRepository.findAll());
                model.addAttribute("nurses", nurseRepository.findAll());
                return "admin/shift/shift-form";
            }

            shift.setStartTime(start);
            shift.setEndTime(end);
            shiftRepository.save(shift);
            return "redirect:/admin/shift?add";
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi xử lý ca trực: " + e.getMessage());
            model.addAttribute("shift", shift);
            model.addAttribute("doctors", doctorRepository.findAll());
            model.addAttribute("nurses", nurseRepository.findAll());
            return "admin/shift-form";
        }
    }


    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        ShiftEntity shift = shiftRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid shift Id:" + id));
        model.addAttribute("shift", shift);
        model.addAttribute("doctors", doctorRepository.findAll());
        model.addAttribute("nurses", nurseRepository.findAll());
        return "admin/shift/shift-form";
    }


    @PostMapping("/edit/{id}")
    public String update(@PathVariable Long id,
                         @RequestParam("shiftDate") String dateStr,
                         @RequestParam("fixedTime") String fixedTime,
                         @ModelAttribute ShiftEntity shift,
                         BindingResult br, Model model) {

        try {
            LocalDate date = LocalDate.parse(dateStr);
            LocalDateTime start, end;

            if ("MORNING".equals(fixedTime)) {
                start = date.atTime(7, 0);
                end = date.atTime(11, 0);
            } else if ("AFTERNOON".equals(fixedTime)) {
                start = date.atTime(13, 0);
                end = date.atTime(17, 0);
            } else {
                throw new IllegalArgumentException("Khung giờ không hợp lệ");
            }

            if (shift.getDoctor() != null && shift.getNurse() != null ||
                    shift.getDoctor() == null && shift.getNurse() == null) {
                model.addAttribute("error", "Chỉ được chọn bác sĩ hoặc y tá.");
                model.addAttribute("shift", shift);
                model.addAttribute("doctors", doctorRepository.findAll());
                model.addAttribute("nurses", nurseRepository.findAll());
                return "admin/shift/shift-form";
            }

            ShiftEntity existingShift = shiftRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy"));

            existingShift.setStartTime(start);
            existingShift.setEndTime(end);
            existingShift.setDescription(shift.getDescription());
            existingShift.setDoctor(shift.getDoctor());
            existingShift.setNurse(shift.getNurse());

            shiftRepository.save(existingShift);
            return "redirect:/admin/shift?edit";
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi cập nhật: " + e.getMessage());
            model.addAttribute("shift", shift);
            model.addAttribute("doctors", doctorRepository.findAll());
            model.addAttribute("nurses", nurseRepository.findAll());
            return "admin/shift-form";
        }
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        shiftRepository.deleteById(id);
        return "redirect:/admin/shift?delete";
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
