package com.gha.gender_healthcare_api.entity; // Đảm bảo đúng package của bạn

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.List;

@Entity // Đánh dấu đây là một Entity JPA
@Table(name = "service") // Chỉ định tên bảng trong database
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Service {

    @Id // Khóa chính
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Tự động tăng (cho MySQL)
    Long id;

    @Column(name = "name", nullable = false, length = 255) // Cột 'name', không null
    String name;

    @Column(name = "description", columnDefinition = "TEXT") // Cột 'description', kiểu TEXT
    String description;

    // !!! ĐÂY LÀ CỘT 'type' CẦN THIẾT !!!
    @Column(name = "type", nullable = false, length = 50) // Cột 'type', không null, độ dài 50
    String type; // Kiểu dữ liệu String, nếu bạn muốn dùng Enum thì sẽ là ServiceType type;

    @Column(name = "price", nullable = false) // Cột 'price', không null
    Double price;


    @OneToMany(mappedBy = "service")
    List<TestBooking> tests = new ArrayList<>();

    @OneToMany(mappedBy = "service")
    List<Feedback> feedbacks = new ArrayList<>();

    @OneToMany(mappedBy = "service")
    List<DashboardReport> dashboardReports = new ArrayList<>();

}