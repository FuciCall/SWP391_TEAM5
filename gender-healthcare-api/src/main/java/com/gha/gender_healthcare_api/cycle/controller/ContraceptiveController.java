package com.gha.gender_healthcare_api.cycle.controller;

import com.gha.gender_healthcare_api.cycle.dto.ContraceptiveReminderRequest;
import com.gha.gender_healthcare_api.cycle.entity.ContraceptiveReminder;
import com.gha.gender_healthcare_api.cycle.service.ContraceptiveReminderService;
import com.gha.gender_healthcare_api.dto.response.ApiResponse;
import com.gha.gender_healthcare_api.security.UserPrincipal;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/contraceptive")
@PreAuthorize("hasRole('CUSTOMER')")
@CrossOrigin(origins = "*")
public class ContraceptiveController {
    
    @Autowired
    private ContraceptiveReminderService reminderService;    /**
     * Thiết lập lời nhắc thuốc tránh thai mới cho người dùng đã xác thực.
     * 
     * Endpoint này cho phép người dùng tạo lịch nhắc thuốc cá nhân với:
     * - Thời gian thông báo tùy chỉnh (ví dụ: hàng ngày lúc 8:00 sáng)
     * - Thông tin loại thuốc và liều lượng
     * - Thời gian của chu kỳ tránh thai
     * - Tùy chọn thông báo email
     * 
     * Hệ thống xác thực dữ liệu yêu cầu và tạo bản ghi nhắc nhở mới
     * với lịch trình tự động cho các thông báo tương lai.
     * 
     * @param request Đối tượng yêu cầu đã được xác thực chứa chi tiết lời nhắc thuốc
     * @param auth Đối tượng xác thực chứa thông tin đăng nhập và quyền của người dùng
     * @return ResponseEntity với thông báo thành công/lỗi và dữ liệu reminder đã tạo
     */
    @PostMapping("/pill-reminder/setup")
    public ResponseEntity<ApiResponse> setupPillReminder(@Valid @RequestBody ContraceptiveReminderRequest request, Authentication auth) {
        try {
            // Trích xuất ID người dùng từ token xác thực để đảm bảo bảo mật
            Long userId = getCurrentUserId(auth);
            
            // Ủy thác cho tầng service để xử lý logic nghiệp vụ và lưu trữ dữ liệu
            ContraceptiveReminder reminder = reminderService.setupPillReminder(userId, request);
            
            // Trả về phản hồi thành công với đối tượng reminder đã tạo
            return ResponseEntity.ok(ApiResponse.success("Pill reminder set up successfully", reminder));
        } catch (Exception e) {
            // Xử lý bất kỳ lỗi xác thực hoặc logic nghiệp vụ nào một cách khéo léo
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to setup reminder: " + e.getMessage()));
        }
    }    /**
     * Lấy lời nhắc thuốc tránh thai đang hoạt động hiện tại cho người dùng đã xác thực.
     * 
     * Endpoint này cho phép người dùng kiểm tra cài đặt lời nhắc thuốc hiện có:
     * - Trả về lời nhắc đang hoạt động nếu tồn tại
     * - Cung cấp phản hồi null nếu không tìm thấy lời nhắc hoạt động nào
     * - Đảm bảo người dùng chỉ có thể truy cập dữ liệu lời nhắc của riêng họ
     * 
     * Hệ thống tìm kiếm các lời nhắc hoạt động liên kết với ID người dùng
     * và trả về cấu hình lời nhắc gần nhất hoặc hiện tại.
     * 
     * @param auth Đối tượng xác thực để xác định người dùng đang yêu cầu
     * @return ResponseEntity với dữ liệu lời nhắc hoạt động hoặc null nếu không tồn tại
     */
    @GetMapping("/pill-reminder")
    public ResponseEntity<ApiResponse> getActiveReminder(Authentication auth) {
        try {
            // Trích xuất ID người dùng để đảm bảo quyền riêng tư và bảo mật dữ liệu
            Long userId = getCurrentUserId(auth);
            
            // Truy vấn tầng service để lấy reminder đang hoạt động của người dùng
            ContraceptiveReminder reminder = reminderService.getUserActiveReminder(userId);
            
            // Xử lý trường hợp không có reminder hoạt động nào
            if (reminder == null) {
                return ResponseEntity.ok(ApiResponse.success("No active reminder found", null));
            }
            
            // Trả về reminder đang hoạt động đã tìm thấy
            return ResponseEntity.ok(ApiResponse.success("Active reminder retrieved", reminder));
        } catch (Exception e) {
            // Xử lý bất kỳ lỗi truy cập dữ liệu hoặc xử lý nào
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to get reminder: " + e.getMessage()));
        }
    }    /**
     * Vô hiệu hóa lời nhắc thuốc tránh thai cụ thể cho người dùng đã xác thực.
     * 
     * Endpoint này cho phép người dùng dừng hoặc hủy lời nhắc thuốc:
     * - Xác thực rằng lời nhắc thuộc về người dùng đang yêu cầu
     * - Đánh dấu lời nhắc là không hoạt động để dừng thông báo tương lai
     * - Duy trì lịch sử lời nhắc để có thể tham khảo trong tương lai
     * - Đảm bảo truy cập an toàn chỉ với lời nhắc thuộc sở hữu của người dùng
     * 
     * Hệ thống thực hiện kiểm tra ủy quyền để ngăn người dùng
     * vô hiệu hóa lời nhắc không thuộc về họ.
     * 
     * @param reminderId ID duy nhất của lời nhắc cần vô hiệu hóa
     * @param auth Đối tượng xác thực để xác minh quyền sở hữu của người dùng
     * @return ResponseEntity với xác nhận thành công hoặc thông báo lỗi
     */
    @DeleteMapping("/pill-reminder/{reminderId}")
    public ResponseEntity<ApiResponse> deactivateReminder(@PathVariable Long reminderId, Authentication auth) {
        try {
            // Trích xuất ID người dùng để xác thực quyền sở hữu
            Long userId = getCurrentUserId(auth);
            
            // Ủy thác cho tầng service để xử lý logic vô hiệu hóa an toàn
            reminderService.deactivateReminder(userId, reminderId);
            
            // Xác nhận vô hiệu hóa thành công
            return ResponseEntity.ok(ApiResponse.success("Reminder deactivated successfully", null));
        } catch (Exception e) {
            // Xử lý lỗi ủy quyền hoặc vấn đề truy cập dữ liệu
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to deactivate reminder: " + e.getMessage()));
        }
    }    /**
     * Trích xuất ID người dùng từ ngữ cảnh xác thực Spring Security.
     * 
     * Phương thức tiện ích này cung cấp truy cập an toàn đến ID của người dùng đã xác thực:
     * - Ép kiểu authentication principal thành kiểu UserPrincipal
     * - Lấy ID người dùng cho các truy vấn cơ sở dữ liệu và ủy quyền
     * - Đảm bảo nhận dạng người dùng nhất quán trên tất cả endpoints
     * - Duy trì bảo mật bằng cách sử dụng ngữ cảnh xác thực của Spring Security
     * 
     * Phương thức này được sử dụng trong toàn bộ controller để liên kết các hoạt động
     * với người dùng đã xác thực cụ thể đang thực hiện yêu cầu.
     * 
     * @param auth Đối tượng Authentication của Spring Security từ ngữ cảnh yêu cầu
     * @return Long ID người dùng được trích xuất từ authentication principal
     */
    private Long getCurrentUserId(Authentication auth) {
        // Ép kiểu authentication principal thành kiểu UserPrincipal tùy chỉnh
        UserPrincipal userPrincipal = (UserPrincipal) auth.getPrincipal();
        
        // Trích xuất và trả về định danh duy nhất của người dùng
        return userPrincipal.getId();
    }
}
