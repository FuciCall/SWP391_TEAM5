package com.gha.gender_healthcare_api.entity; // Đảm bảo đúng package của bạn

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity // Đánh dấu đây là một Entity JPA
@Table(name = "service") // Chỉ định tên bảng trong database
public class Service {

    @Id // Khóa chính
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Tự động tăng (cho MySQL)
    private Long id;

    @Column(name = "name", nullable = false, length = 255) // Cột 'name', không null
    private String name;

    @Column(name = "description", columnDefinition = "TEXT") // Cột 'description', kiểu TEXT
    private String description;

    // !!! ĐÂY LÀ CỘT 'type' CẦN THIẾT !!!
    @Column(name = "type", nullable = false, length = 50) // Cột 'type', không null, độ dài 50
    private String type; // Kiểu dữ liệu String, nếu bạn muốn dùng Enum thì sẽ là ServiceType type;

    @Column(name = "price", nullable = false) // Cột 'price', không null
    private Double price;

    // Constructors (cần có constructor mặc định không tham số)
    public Service() {
    }

    public Service(String name, String description, String type, Double price) {
        this.name = name;
        this.description = description;
        this.type = type;
        this.price = price;
    }

    // Getters và Setters cho tất cả các trường
    // Đây là phần rất quan trọng để Hibernate có thể truy cập dữ liệu
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }
}