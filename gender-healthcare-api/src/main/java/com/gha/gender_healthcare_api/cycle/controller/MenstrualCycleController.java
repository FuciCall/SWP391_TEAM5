package com.gha.gender_healthcare_api.cycle.controller;

import com.gha.gender_healthcare_api.cycle.dto.CycleAnalytics;
import com.gha.gender_healthcare_api.cycle.dto.MenstrualCycleRequest;
import com.gha.gender_healthcare_api.cycle.entity.MenstrualCycle;
import com.gha.gender_healthcare_api.cycle.entity.CyclePrediction;
import com.gha.gender_healthcare_api.cycle.service.MenstrualCycleService;
import com.gha.gender_healthcare_api.cycle.service.CyclePredictionService;
import com.gha.gender_healthcare_api.dto.response.ApiResponse;
import com.gha.gender_healthcare_api.security.UserPrincipal;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller xử lý các API endpoints liên quan đến chu kỳ kinh nguyệt
 * 
 * Chức năng chính:
 * - Ghi nhận chu kỳ kinh nguyệt mới
 * - Xem lịch sử chu kỳ
 * - Lấy thống kê và phân tích chu kỳ
 * - Xem và làm mới dự đoán chu kỳ
 * 
 * Bảo mật:
 * - Chỉ cho phép role CUSTOMER truy cập
 * - Mỗi user chỉ có thể truy cập dữ liệu của chính mình
 * - Sử dụng JWT authentication qua UserPrincipal
 */
@RestController
@RequestMapping("/api/menstrual-cycle")
@PreAuthorize("hasRole('CUSTOMER')")  // Chỉ customer mới được truy cập cycle data
@CrossOrigin(origins = "*")            // Cho phép CORS từ mọi origin (có thể restrict trong production)
public class MenstrualCycleController {
      /**
     * Service xử lý logic nghiệp vụ cho chu kỳ kinh nguyệt
     * Được inject thông qua Spring Dependency Injection
     */
    @Autowired
    private MenstrualCycleService menstrualCycleService;
    
    /**
     * Service xử lý dự đoán chu kỳ
     * Được inject thông qua Spring Dependency Injection
     */
    @Autowired
    private CyclePredictionService cyclePredictionService;
    
    /**
     * API Endpoint: Ghi nhận chu kỳ kinh nguyệt mới
     * 
     * Quy trình hoạt động:
     * 1. Nhận dữ liệu từ client qua HTTP POST request
     * 2. Validate dữ liệu đầu vào bằng @Valid annotation
     * 3. Lấy user ID từ JWT token thông qua Authentication object
     * 4. Gọi service để lưu chu kỳ mới vào database
     * 5. Service tự động tính toán cycle_length và period_length
     * 6. Trigger tạo dự đoán chu kỳ tiếp theo
     * 7. Lên lịch các thông báo tự động (ovulation, period reminders)
     * 8. Trả về response với dữ liệu chu kỳ đã lưu
     * 
     * @param request DTO chứa thông tin chu kỳ (start_date, end_date, flow_intensity, symptoms, notes)
     * @param auth JWT Authentication object để lấy thông tin user hiện tại
     * @return ResponseEntity<ApiResponse> với status 200 nếu thành công, 400 nếu lỗi
     * 
     * Example Request Body:
     * {
     *   "startDate": "2025-06-15",
     *   "endDate": "2025-06-20",
     *   "flowIntensity": "NORMAL",
     *   "symptoms": "cramps,bloating",
     *   "notes": "Felt tired during this cycle"
     * }
     */
    @PostMapping("/declare")
    public ResponseEntity<ApiResponse> declareCycle(@Valid @RequestBody MenstrualCycleRequest request, Authentication auth) {
        try {
            // Lấy user ID từ JWT token - đảm bảo security
            Long userId = getCurrentUserId(auth);
            
            // Gọi service để xử lý business logic
            MenstrualCycle cycle = menstrualCycleService.declareMenstrualCycle(userId, request);
            
            // Trả về response thành công với dữ liệu đã lưu
            return ResponseEntity.ok(ApiResponse.success("Menstrual cycle recorded successfully", cycle));
        } catch (Exception e) {
            // Xử lý lỗi và trả về error response
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to record cycle: " + e.getMessage()));
        }
    }
    
    /**
     * API Endpoint: Lấy lịch sử chu kỳ của user
     * 
     * Quy trình hoạt động:
     * 1. Nhận query parameter 'months' (default = 12)
     * 2. Lấy user ID từ JWT token
     * 3. Tính toán fromDate = hiện tại - months
     * 4. Query database lấy tất cả cycles từ fromDate đến hiện tại
     * 5. Sắp xếp theo start_date descending (mới nhất trước)
     * 6. Trả về danh sách cycles cho frontend hiển thị
     * 
     * @param months Số tháng muốn lấy lịch sử (default: 12 tháng)
     * @param auth JWT Authentication object
     * @return ResponseEntity chứa List<MenstrualCycle>
     * 
     * Example: GET /api/menstrual-cycle/history?months=6
     * Sẽ lấy cycles trong 6 tháng gần nhất
     */
    @GetMapping("/history")
    public ResponseEntity<ApiResponse> getCycleHistory(@RequestParam(defaultValue = "12") int months, Authentication auth) {
        try {
            Long userId = getCurrentUserId(auth);
            
            // Gọi service để lấy cycles trong khoảng thời gian chỉ định
            List<MenstrualCycle> cycles = menstrualCycleService.getUserCycles(userId, months);
            
            return ResponseEntity.ok(ApiResponse.success("Cycle history retrieved", cycles));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to get cycle history: " + e.getMessage()));
        }
    }
    
    /**
     * API Endpoint: Lấy thống kê và phân tích chu kỳ
     * 
     * Quy trình hoạt động:
     * 1. Lấy tất cả cycles của user từ database
     * 2. Tính toán các metrics quan trọng:
     *    - Tổng số cycles đã track
     *    - Độ dài chu kỳ trung bình (average cycle length)
     *    - Độ dài kinh nguyệt trung bình (average period length)
     *    - Tính đều đặn của chu kỳ (regularity) qua variance analysis
     * 3. Tạo health insights dựa trên medical guidelines:
     *    - Cycle length 21-35 days: normal
     *    - Period length 3-7 days: normal
     *    - Regularity levels: VERY_REGULAR, REGULAR, IRREGULAR
     * 4. Đưa ra lời khuyên cá nhân hóa cho user
     * 
     * @param auth JWT Authentication object
     * @return ResponseEntity chứa CycleAnalytics object với tất cả thống kê
     * 
     * Example Response:
     * {
     *   "totalCycles": 5,
     *   "averageCycleLength": 28.5,
     *   "averagePeriodLength": 5.2,
     *   "cycleRegularity": "REGULAR",
     *   "healthInsights": ["Your cycle length is within normal range", "Cycles are quite regular"]
     * }
     */
    @GetMapping("/analytics")
    public ResponseEntity<ApiResponse> getCycleAnalytics(Authentication auth) {
        try {
            Long userId = getCurrentUserId(auth);
            
            // Service sẽ tính toán tất cả analytics và insights
            CycleAnalytics analytics = menstrualCycleService.getCycleAnalytics(userId);
            
            return ResponseEntity.ok(ApiResponse.success("Cycle analytics retrieved", analytics));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to get analytics: " + e.getMessage()));
        }
    }
    
    /**
     * API Endpoint: Lấy dự đoán chu kỳ hiện tại
     * 
     * Quy trình hoạt động:
     * 1. Lấy prediction mới nhất của user từ database
     * 2. Prediction chứa thông tin:
     *    - Ngày rụng trứng dự đoán (predicted_ovulation_date)
     *    - Cửa sổ sinh sản (fertile_window_start/end)
     *    - Ngày kinh nguyệt tiếp theo (next_period_date)
     *    - Khả năng có thai hiện tại (pregnancy_likelihood)
     * 3. Frontend sử dụng data này để:
     *    - Hiển thị calendar với các ngày quan trọng
     *    - Show fertility status
     *    - Đưa ra khuyến nghị cho user
     * 
     * @param auth JWT Authentication object
     * @return ResponseEntity chứa CyclePrediction object
     * 
     * Example Response:
     * {
     *   "predictedOvulationDate": "2025-06-28",
     *   "fertileWindowStart": "2025-06-23",
     *   "fertileWindowEnd": "2025-06-29",
     *   "nextPeriodDate": "2025-07-12",
     *   "pregnancyLikelihood": 20.0
     * }
     */
    @GetMapping("/predictions")
    public ResponseEntity<ApiResponse> getCurrentPredictions(Authentication auth) {
        try {
            Long userId = getCurrentUserId(auth);
            
            // Lấy prediction mới nhất từ database
            CyclePrediction prediction = cyclePredictionService.getCurrentPrediction(userId);
            
            return ResponseEntity.ok(ApiResponse.success("Predictions retrieved", prediction));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to get predictions: " + e.getMessage()));
        }
    }
    
    /**
     * API Endpoint: Làm mới dự đoán chu kỳ
     * 
     * Quy trình hoạt động:
     * 1. Trigger tính toán lại dự đoán dựa trên cycle data mới nhất
     * 2. Sử dụng thuật toán prediction với 6 cycles gần nhất
     * 3. Tạo CyclePrediction record mới trong database
     * 4. Cập nhật lại tất cả thông báo đã lên lịch
     * 5. Trả về prediction mới cho frontend
     * 
     * Khi nào cần refresh:
     * - User vừa record cycle mới
     * - User muốn cập nhật prediction theo ý muốn
     * - Prediction hiện tại đã cũ (> 1 tháng)
     * 
     * @param auth JWT Authentication object
     * @return ResponseEntity chứa CyclePrediction mới
     */
    @PostMapping("/predictions/refresh")
    public ResponseEntity<ApiResponse> refreshPredictions(Authentication auth) {
        try {
            Long userId = getCurrentUserId(auth);
            
            // Tính toán lại prediction với data mới nhất
            CyclePrediction prediction = cyclePredictionService.generatePredictions(userId);
            
            return ResponseEntity.ok(ApiResponse.success("Predictions updated", prediction));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to update predictions: " + e.getMessage()));
        }
    }
    
    /**
     * Helper method: Lấy user ID từ JWT Authentication object
     * 
     * Cách hoạt động:
     * 1. Spring Security tự động parse JWT token từ Authorization header
     * 2. Tạo Authentication object chứa UserPrincipal
     * 3. UserPrincipal chứa thông tin user đã authenticated
     * 4. Extract user ID để sử dụng trong các service calls
     * 
     * Bảo mật:
     * - Đảm bảo user chỉ có thể truy cập data của chính mình
     * - Không thể fake user ID vì JWT đã được verify
     * 
     * @param auth Authentication object từ Spring Security
     * @return Long userId của user hiện tại
     */
    private Long getCurrentUserId(Authentication auth) {
        UserPrincipal userPrincipal = (UserPrincipal) auth.getPrincipal();
        return userPrincipal.getId();
    }
}
