package com.ISP392.demo.controller.admin;

import com.ISP392.demo.entity.LogsEntity;
import com.ISP392.demo.entity.RoomEntity;
import com.ISP392.demo.entity.UserEntity;
import com.ISP392.demo.repository.LogsRepository;
import com.ISP392.demo.repository.RoomRepository;
import com.ISP392.demo.repository.UserRepository;
import com.ISP392.demo.repository.DoctorRepository;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/room")
public class AdminRoomController {

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private LogsRepository logsRepository;


    private void saveLog(String content) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity user = userRepository.findByEmail(email).orElse(null);
        if (user != null) {
            LogsEntity log = new LogsEntity();
            log.setContent(content);
            log.setUser(user);
            log.setCreatedAt(LocalDateTime.now());
            logsRepository.save(log);
        }
    }

    @GetMapping("")
    public String listRooms(Model model,
                            @RequestParam(value = "search", required = false) String keyword,
                            @RequestParam(value = "page", defaultValue = "0") int page,
                            @RequestParam(value = "size", defaultValue = "5") int size) {

        List<RoomEntity> allRooms = roomRepository.findAll();

        if (keyword != null && !keyword.trim().isEmpty()) {
            String lowerKeyword = keyword.toLowerCase();
            allRooms = allRooms.stream()
                    .filter(room ->
                            (room.getRoomName() != null && room.getRoomName().toLowerCase().contains(lowerKeyword)) ||
                                    (room.getRoomType() != null && room.getRoomType().toLowerCase().contains(lowerKeyword)) ||
                                    (room.getLocation() != null && room.getLocation().toLowerCase().contains(lowerKeyword)) ||
                                    (room.getDescription() != null && room.getDescription().toLowerCase().contains(lowerKeyword))
                    )
                    .collect(Collectors.toList());
        }

        int totalItems = allRooms.size();
        int totalPages = (int) Math.ceil((double) totalItems / size);

        int start = Math.min(page * size, totalItems);
        int end = Math.min(start + size, totalItems);

        List<RoomEntity> rooms = allRooms.subList(start, end);

        model.addAttribute("rooms", rooms);
        model.addAttribute("search", keyword);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);

        return "admin/room/list";
    }

    @GetMapping("/add")
    public String addRoomForm(Model model) {
        model.addAttribute("room", new RoomEntity());
        model.addAttribute("doctors", doctorRepository.findAll());
        return "admin/room/add";
    }

    @PostMapping("/save")
    public String saveRoom(@ModelAttribute("room") @Valid RoomEntity room,
                           BindingResult result,
                           Model model) {
        if (result.hasErrors()) {
            model.addAttribute("doctors", doctorRepository.findAll());
            return "admin/room/add";
        }
        saveLog("Thêm phòng " + room.getRoomName() + " ở vị trí " + room.getLocation());
        roomRepository.save(room);
        return "redirect:/admin/room?add=true";
    }

    @GetMapping("/edit/{id}")
    public String editRoomForm(@PathVariable("id") Long id, Model model) {
        Optional<RoomEntity> optional = roomRepository.findById(id);
        if (optional.isPresent()) {
            model.addAttribute("room", optional.get());
            model.addAttribute("doctors", doctorRepository.findAll());
            return "admin/room/edit";
        }
        return "redirect:/admin/room";
    }

    @PostMapping("/update/{id}")
    public String updateRoom(@PathVariable("id") Long id,
                             @ModelAttribute("room") @Valid RoomEntity room,
                             BindingResult result,
                             Model model) {
        if (result.hasErrors()) {
            model.addAttribute("doctors", doctorRepository.findAll());
            return "admin/room/edit";
        }

        room.setId(id);
        roomRepository.save(room);
        saveLog("Cập nhật thông tin phòng có id " + room.getId());

        return "redirect:/admin/room?edit=true";
    }

    @GetMapping("/delete/{id}")
    public String deleteRoom(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            Optional<RoomEntity> roomOptional = roomRepository.findById(id);
            if (roomOptional.isPresent()) {
                RoomEntity room = roomOptional.get();
                // Xóa liên kết với bác sĩ trước khi xóa phòng
                room.setPrimaryDoctor(null);
                room.setPhoneNumber(null);
                roomRepository.save(room);
                // Sau đó xóa phòng
                roomRepository.delete(room);
                saveLog("Xoá phòng có id " + room.getId());

                redirectAttributes.addFlashAttribute("successMessage", "Xóa phòng thành công!");
                return "redirect:/admin/room?delete=true";
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không thể xóa phòng. Phòng đang được sử dụng!");
        }
        return "redirect:/admin/room";
    }
}
