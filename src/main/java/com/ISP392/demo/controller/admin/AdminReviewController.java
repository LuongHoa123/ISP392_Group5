package com.ISP392.demo.controller.admin;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ISP392.demo.entity.AppointmentEntity;
import com.ISP392.demo.entity.LogsEntity;
import com.ISP392.demo.entity.ReviewEntity;
import com.ISP392.demo.entity.UserEntity;
import com.ISP392.demo.repository.AppointmentRepository;
import com.ISP392.demo.repository.LogsRepository;
import com.ISP392.demo.repository.ReviewRepository;
import com.ISP392.demo.repository.UserRepository;

@Controller // Đánh dấu đây là Spring Controller
@RequestMapping("/admin/review") // Base URL cho tất cả endpoint trong class này
public class AdminReviewController {

   // Inject các repository để thao tác với database
   @Autowired
   private ReviewRepository reviewRepository; // Thao tác với bảng reviews
   
   @Autowired
   private UserRepository userRepository; // Lấy thông tin user đang đăng nhập
   
   @Autowired
   private LogsRepository logsRepository; // Ghi log hoạt động admin
   
   @Autowired
   private AppointmentRepository appointmentRepository; // Cập nhật liên kết appointment-review

   // Method ghi log hoạt động của admin
   private void saveLog(String content) {
       // Lấy email của user đang đăng nhập từ Spring Security
       String email = SecurityContextHolder.getContext().getAuthentication().getName();
       
       // Tìm user entity theo email
       UserEntity user = userRepository.findByEmail(email).orElse(null);
       
       if (user != null) {
           // Tạo log entry mới
           LogsEntity log = new LogsEntity();
           log.setContent(content);           // Nội dung hành động (VD: "Xoá đánh giá có id 5")
           log.setUser(user);                 // User thực hiện hành động
           log.setCreatedAt(LocalDateTime.now());  // Thời gian thực hiện
           logsRepository.save(log);          // Lưu vào database
       }
   }

   // Endpoint hiển thị danh sách đánh giá với tìm kiếm và lọc
   @GetMapping("") // Map với GET /admin/review
   public String listRooms(Model model,
                           @RequestParam(value = "search", required = false) String keyword, // Từ khóa tìm kiếm (tùy chọn)
                           @RequestParam(value = "filterType", required = false) String filterType, // Loại filter (negative/normal/good)
                           @RequestParam(value = "page", defaultValue = "0") int page, // Trang hiện tại (mặc định 0)
                           @RequestParam(value = "size", defaultValue = "5") int size) { // Số item per page (mặc định 5)

       // Lấy tất cả đánh giá từ database
       List<ReviewEntity> allReviews = reviewRepository.findAll();
       
       // Tính số đánh giá tiêu cực (1-2 sao)
       long totalNegativeReviews = allReviews.stream()
               .filter(review -> review.getStar() != null && review.getStar() <= 2) // Lọc review có sao <= 2
               .count(); // Đếm số lượng
       
       // Tính số đánh giá bình thường (3 sao)
       long totalNormalReviews = allReviews.stream()
               .filter(review -> review.getStar() != null && review.getStar() == 3) // Lọc review có sao = 3
               .count();
       
       // Tính số đánh giá tốt (4-5 sao)
       long totalGoodReviews = allReviews.stream()
               .filter(review -> review.getStar() != null && review.getStar() >= 4) // Lọc review có sao >= 4
               .count();
       
       // Khởi tạo danh sách để lọc (ban đầu = tất cả)
       List<ReviewEntity> list = allReviews;

       // LOGIC LỌC THEO LOẠI ĐÁNH GIÁ
       if (filterType != null && !filterType.trim().isEmpty()) {
           if ("negative".equals(filterType)) {
               // Lọc chỉ lấy đánh giá tiêu cực
               list = list.stream()
                       .filter(review -> review.getStar() != null && review.getStar() <= 2)
                       .collect(Collectors.toList()); // Chuyển Stream về List
           } else if ("normal".equals(filterType)) {
               // Lọc chỉ lấy đánh giá bình thường
               list = list.stream()
                       .filter(review -> review.getStar() != null && review.getStar() == 3)
                       .collect(Collectors.toList());
           } else if ("good".equals(filterType)) {
               // Lọc chỉ lấy đánh giá tốt
               list = list.stream()
                       .filter(review -> review.getStar() != null && review.getStar() >= 4)
                       .collect(Collectors.toList());
           }
       }

       // LOGIC TÌM KIẾM THEO TỪ KHÓA
       if (keyword != null && !keyword.trim().isEmpty()) {
           String lowerKeyword = keyword.toLowerCase(); // Chuyển về chữ thường để so sánh
           list = list.stream()
                   .filter(reviewEntity -> {
                       boolean match = false; // Flag đánh dấu có khớp không

                       // Tìm kiếm trong tên bệnh nhân
                       if (reviewEntity.getPatient() != null) {
                           String firstName = reviewEntity.getPatient().getFirstName();
                           String lastName = reviewEntity.getPatient().getLastName();

                           // Kiểm tra tên có chứa keyword không
                           if (firstName != null && firstName.toLowerCase().contains(lowerKeyword)) {
                               match = true;
                           }
                           if (lastName != null && lastName.toLowerCase().contains(lowerKeyword)) {
                               match = true;
                           }
                       }

                       // Tìm kiếm theo số sao (nếu keyword là số)
                       try {
                           int starValue = Integer.parseInt(lowerKeyword); // Thử convert keyword thành số
                           if (reviewEntity.getStar() != null && reviewEntity.getStar() == starValue) {
                               match = true; // Khớp với số sao
                           }
                       } catch (NumberFormatException ignored) {
                           // Keyword không phải số, bỏ qua
                       }

                       return match; // Trả về true nếu khớp điều kiện
                   })
                   .collect(Collectors.toList());
       }

       // LOGIC PHÂN TRANG (Pagination)
       int totalItems = list.size(); // Tổng số items sau khi lọc
       int totalPages = (int) Math.ceil((double) totalItems / size); // Tính tổng số trang

       int start = Math.min(page * size, totalItems); // Vị trí bắt đầu
       int end = Math.min(start + size, totalItems);   // Vị trí kết thúc

       List<ReviewEntity> reviews = list.subList(start, end); // Cắt list theo trang

       // Thêm dữ liệu vào Model để gửi tới template
       model.addAttribute("reviews", reviews); // Danh sách review hiện tại
       model.addAttribute("search", keyword); // Từ khóa tìm kiếm (để giữ lại trong form)
       model.addAttribute("filterType", filterType); // Loại filter (để giữ lại trong form)
       model.addAttribute("currentPage", page); // Trang hiện tại
       model.addAttribute("totalPages", totalPages); // Tổng số trang
       model.addAttribute("totalNegativeReviews", totalNegativeReviews); // Số đánh giá tiêu cực
       model.addAttribute("totalNormalReviews", totalNormalReviews); // Số đánh giá bình thường
       model.addAttribute("totalGoodReviews", totalGoodReviews); // Số đánh giá tốt

       return "admin/review/list"; // Trả về template HTML
   }

   // Endpoint xóa đánh giá
   @GetMapping("/delete/{id}") // Map với GET /admin/review/delete/{id}
   public String deleteRoom(@PathVariable("id") Long id, // Lấy id từ URL
                           RedirectAttributes redirectAttributes) { // Để gửi message sau redirect
       try {
           // Tìm review theo ID
           Optional<ReviewEntity> reviewEntityOptional = reviewRepository.findById(id);
           if (reviewEntityOptional.isPresent()) { // Nếu tìm thấy review
               ReviewEntity reviewEntity = reviewEntityOptional.get();
               
               // Lấy appointment liên kết với review này
               AppointmentEntity appointmentEntity = reviewEntity.getAppointment();
               appointmentEntity.setReviewEntity(null); // Xóa liên kết appointment -> review
               appointmentRepository.save(appointmentEntity); // Lưu appointment đã update

               // Xóa review khỏi database
               reviewRepository.deleteById(id);
               
               // Ghi log hoạt động
               saveLog("Xoá đánh giá có id " + id);

               // Thêm message thành công
               redirectAttributes.addFlashAttribute("successMessage", "Xóa đánh giá thành công!");
               return "redirect:/admin/review?delete=true"; // Redirect với query param
           }
       } catch (Exception e) {
           // Nếu có lỗi, thêm message lỗi
           redirectAttributes.addFlashAttribute("errorMessage", "Không thể xóa đánh giá!");
       }
       return "redirect:/admin/review"; // Redirect về trang list
   }
}