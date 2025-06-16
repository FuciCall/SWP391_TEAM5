//: Repository thao tác với bảng User trong database (Spring Data JPA).
package com.gha.gender_healthcare_api.repository;

import com.gha.gender_healthcare_api.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository // Đánh dấu đây là một Spring Data JPA Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Tìm người dùng theo username
    Optional<User> findByUsername(String username);

    // Tìm người dùng theo email
    Optional<User> findByEmail(String email);

    // Kiểm tra xem username có tồn tại không
    Boolean existsByUsername(String username);

    // Kiểm tra xem email có tồn tại không
    Boolean existsByEmail(String email);

    // THÊM PHƯƠNG THỨC NÀY ĐỂ TÌM NGƯỜI DÙNG THEO NHÀ CUNG CẤP VÀ ID CỦA NHÀ CUNG
    // CẤP
    // Rất quan trọng cho OAuth2 để kiểm tra xem người dùng đã đăng nhập qua
    // provider này chưa
    Optional<User> findByProviderAndProviderId(String provider, String providerId);
}
