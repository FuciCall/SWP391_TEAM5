/*
 * Entity Service dùng cho quản lý dịch vụ tư vấn (CRUD) bởi admin/manager.
 * Bao gồm các trường: id, name, description, type, price.
 * Sử dụng code-first, Hibernate sẽ tự tạo bảng 'service' trong database.
 */

package com.gha.gender_healthcare_api.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "service")
public class Service {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "type", nullable = false, length = 50)
    private String type;

    @Column(name = "price", nullable = false)
    private Double price;

    // Constructor mặc định
    public Service() {
    }

    // Constructor đầy đủ
    public Service(String name, String description, String type, Double price) {
        this.name = name;
        this.description = description;
        this.type = type;
        this.price = price;
    }

    // Getter & Setter
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