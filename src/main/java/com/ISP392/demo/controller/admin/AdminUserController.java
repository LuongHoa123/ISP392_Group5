package com.ISP392.demo.controller.admin;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.ISP392.demo.entity.LogsEntity;
import com.ISP392.demo.repository.DoctorRepository;
import com.ISP392.demo.repository.LogsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.ISP392.demo.dto.UserDto;
import com.ISP392.demo.entity.RoleEntity;
import com.ISP392.demo.entity.UserEntity;
import com.ISP392.demo.enums.RoleEnum;
import com.ISP392.demo.repository.RoleRepository;
import com.ISP392.demo.repository.UserRepository;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/admin/user")
public class AdminUserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

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
    public String userListPage(Model model,
                               @RequestParam(value = "email", required = false) String emailParam,
                               @RequestParam(value = "role", required = false) String roleParam,
                               @RequestParam(value = "page", defaultValue = "0") int page,
                               @RequestParam(value = "size", defaultValue = "5") int size) {

        List<UserEntity> allUsers = userRepository.findAll();

        if (emailParam != null && !emailParam.isEmpty()) {
            allUsers = allUsers.stream()
                    .filter(u -> u.getEmail() != null && u.getEmail().toLowerCase().contains(emailParam.toLowerCase()))
                    .collect(Collectors.toList());
        }

        if (roleParam != null && !roleParam.isEmpty()) {
            allUsers = allUsers.stream()
                    .filter(u -> u.getRole() != null &&
                            u.getRole().getName().name().equalsIgnoreCase(roleParam))
                    .collect(Collectors.toList());
        }

        int totalItems = allUsers.size();
        int totalPages = (int) Math.ceil((double) totalItems / size);

        int start = Math.min(page * size, totalItems);
        int end = Math.min(start + size, totalItems);

        List<UserEntity> users = allUsers.subList(start, end);

        model.addAttribute("users", users);
        model.addAttribute("roles", roleRepository.findAll());
        model.addAttribute("selectedRole", roleParam);
        model.addAttribute("email", emailParam);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);

        return "admin/user/list";
    }

    // Form thêm mới
    @GetMapping("/add")
    public String addUserForm(Model model) {
        model.addAttribute("userDto", new UserDto());
        model.addAttribute("roles", roleRepository.findAll());
        return "admin/user/add";
    }

    @PostMapping("/save")
    public String saveUser(@ModelAttribute("userDto") @Valid UserDto userDto,
                           BindingResult result,
                           Model model) {
        if (result.hasErrors()) {
            model.addAttribute("roles", roleRepository.findAll());
            return "admin/user/add";
        }

        if (userRepository.findByEmail(userDto.getEmail()).isPresent()) {
            model.addAttribute("roles", roleRepository.findAll());
            model.addAttribute("emailError");
            return "admin/user/add";
        }

        RoleEntity selectedRole = roleRepository.findByName(RoleEnum.valueOf(userDto.getRoleName()));
        UserEntity user = new UserEntity();
        user.setEmail(userDto.getEmail());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user.setRole(selectedRole);
        user.setStatus(userDto.getStatus());
        userRepository.save(user);
        saveLog("Thêm người dùng có email " + user.getEmail() + ", quyền " + user.getRole().getName());

        return "redirect:/admin/user?add=true";
    }


    @GetMapping("/edit/{id}")
    public String editUserForm(@PathVariable("id") Long id, Model model) {
        Optional<UserEntity> optional = userRepository.findById(id);
        if (optional.isPresent()) {
            UserEntity user = optional.get();
            UserDto dto = new UserDto();
            dto.setId(user.getId());
            dto.setEmail(user.getEmail());
            dto.setStatus(user.getStatus());
            dto.setRoleName(user.getRole().getName().name());

            model.addAttribute("userDto", dto);
            model.addAttribute("roles", roleRepository.findAll());
            return "admin/user/edit";
        }
        return "redirect:/admin/user";
    }

    @PostMapping("/update/{id}")
    public String updateUser(@PathVariable("id") Long id,
                             @ModelAttribute("userDto") @Valid UserDto userDto,
                             BindingResult result,
                             Model model) {
        if (result.hasErrors()) {
            model.addAttribute("roles", roleRepository.findAll());
            return "admin/user/edit";
        }

        Optional<UserEntity> emailUser = userRepository.findByEmail(userDto.getEmail());
        if (emailUser.isPresent() && !emailUser.get().getId().equals(id)) {
            model.addAttribute("roles", roleRepository.findAll());
            model.addAttribute("emailError", "Email is already registered.");
            return "admin/user/edit";
        }

        Optional<UserEntity> optional = userRepository.findById(id);
        if (optional.isPresent()) {
            UserEntity user = optional.get();
            user.setEmail(userDto.getEmail());
            user.setStatus(userDto.getStatus());

            if (userDto.getPassword() != null && !userDto.getPassword().isBlank()) {
                user.setPassword(passwordEncoder.encode(userDto.getPassword()));
            }

            RoleEntity selectedRole = roleRepository.findByName(RoleEnum.valueOf(userDto.getRoleName()));
            user.setRole(selectedRole);

            userRepository.save(user);
            saveLog("Cập nhật người dùng có id " + user.getId());

        }

        return "redirect:/admin/user?edit=true";
    }



    @PostMapping("/update-status/{id}")
    public String updateUserStatus(@PathVariable Long id,
                                   @RequestParam("status") Integer status) {
        Optional<UserEntity> optional = userRepository.findById(id);
        if (optional.isPresent()) {
            UserEntity user = optional.get();
            user.setStatus(status);
            userRepository.save(user);
            saveLog("Cập nhật trạng thái của người dùng thành " + (status == 1 ? "Mở khoá" : "Khoá") + " có id " + user.getId());

        }
        return "redirect:/admin/user?update=true";
    }

    @GetMapping("/delete/{id}")
    public String deleteUser(@PathVariable("id") Long id) {
        userRepository.deleteById(id);
        saveLog("Xoá người dùng có id " + id);

        return "redirect:/admin/user?delete=true";
    }
}
