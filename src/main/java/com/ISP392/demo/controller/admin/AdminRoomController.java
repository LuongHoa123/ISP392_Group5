package com.ISP392.demo.controller.admin;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ISP392.demo.entity.LogsEntity;
import com.ISP392.demo.entity.RoomEntity;
import com.ISP392.demo.entity.UserEntity;
import com.ISP392.demo.repository.LogsRepository;
import com.ISP392.demo.repository.RoomRepository;
import com.ISP392.demo.repository.UserRepository;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/admin/room")
public class AdminRoomController {

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private UserRepository userRepository;

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
                                    (room.getFloor() != null && room.getFloor().toString().contains(lowerKeyword)) ||
                                    (room.getDescription() != null && room.getDescription().toLowerCase().contains(lowerKeyword))
                    )
                    .collect(Collectors.toList());
        }
        
        // Tính toán phân trang
        int totalItems = allRooms.size();
        int totalPages = (int) Math.ceil((double) totalItems / size);
        
        // Cắt dữ liệu theo trang
        int start = Math.min(page * size, totalItems);
        int end = Math.min(start + size, totalItems);

        List<RoomEntity> rooms = allRooms.subList(start, end);
        
        // Truyền dữ liệu cho View
        model.addAttribute("rooms", rooms);           
        model.addAttribute("search", keyword);        
        model.addAttribute("currentPage", page);      
        model.addAttribute("totalPages", totalPages); 

        return "admin/room/list";
    }

    @GetMapping("/add")
    public String addRoomForm(Model model) {
        model.addAttribute("room", new RoomEntity());
        return "admin/room/add";
    }

    // API endpoint to get available room numbers for a specific floor
    @GetMapping("/api/available-rooms")
    public String getAvailableRooms(@RequestParam Integer floor, Model model) {
        List<String> occupiedRooms = roomRepository.findAll().stream()
                .filter(room -> room.getFloor().equals(floor))
                .map(RoomEntity::getRoomName)
                .collect(Collectors.toList());

        List<String> availableRooms = IntStream.rangeClosed(1, 5)
                .mapToObj(i -> "P" + floor + String.format("%02d", i))
                .filter(roomName -> !occupiedRooms.contains(roomName))
                .collect(Collectors.toList());

        model.addAttribute("availableRooms", availableRooms);
        return "fragments/room-options :: roomOptions";
    }

    @PostMapping("/save")
    public String saveRoom(@ModelAttribute("room") @Valid RoomEntity room,
                           BindingResult result,
                           Model model,
                           RedirectAttributes redirectAttributes) {
        
        // Check if room name already exists
        Optional<RoomEntity> existingRoom = roomRepository.findAll().stream()
                .filter(r -> r.getRoomName().equals(room.getRoomName()))
                .findFirst();
        
        if (existingRoom.isPresent()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Tên phòng đã tồn tại!");
            return "redirect:/admin/room/add";
        }

        if (result.hasErrors()) {
            return "admin/room/add";
        }
        
        saveLog("Thêm phòng " + room.getRoomName() + " ở tầng " + room.getFloor());
        roomRepository.save(room);
        redirectAttributes.addFlashAttribute("successMessage", "Thêm phòng thành công!");
        return "redirect:/admin/room?add=true";
    }

    @GetMapping("/edit/{id}")
    public String editRoomForm(@PathVariable("id") Long id, Model model) {
        Optional<RoomEntity> optional = roomRepository.findById(id);
        if (optional.isPresent()) {
            model.addAttribute("room", optional.get());
            return "admin/room/edit";
        }
        return "redirect:/admin/room";
    }

    @PostMapping("/update/{id}")
    public String updateRoom(@PathVariable("id") Long id,
                             @ModelAttribute("room") @Valid RoomEntity room,
                             BindingResult result,
                             Model model,
                             RedirectAttributes redirectAttributes) {
        
        // Check if room name already exists for other rooms
        Optional<RoomEntity> existingRoom = roomRepository.findAll().stream()
                .filter(r -> r.getRoomName().equals(room.getRoomName()) && !r.getId().equals(id))
                .findFirst();
        
        if (existingRoom.isPresent()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Tên phòng đã tồn tại!");
            return "redirect:/admin/room/edit/" + id;
        }

        if (result.hasErrors()) {
            return "admin/room/edit";
        }

        room.setId(id);
        roomRepository.save(room);
        saveLog("Cập nhật thông tin phòng " + room.getRoomName());
        redirectAttributes.addFlashAttribute("successMessage", "Cập nhật phòng thành công!");
        return "redirect:/admin/room?edit=true";
    }

    @GetMapping("/delete/{id}")
    public String deleteRoom(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            Optional<RoomEntity> roomOptional = roomRepository.findById(id);
            if (roomOptional.isPresent()) {
                RoomEntity room = roomOptional.get();
                roomRepository.delete(room);
                saveLog("Xoá phòng " + room.getRoomName());
                redirectAttributes.addFlashAttribute("successMessage", "Xóa phòng thành công!");
                return "redirect:/admin/room?delete=true";
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không thể xóa phòng. Phòng đang được sử dụng!");
        }
        return "redirect:/admin/room";
    }
}
