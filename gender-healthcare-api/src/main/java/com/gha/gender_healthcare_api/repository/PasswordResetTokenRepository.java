//: Repository thao tác với bảng PasswordResetToken (tìm, xóa token đặt lại mật khẩu theo user hoặc token).
package com.gha.gender_healthcare_api.repository;

import com.gha.gender_healthcare_api.entity.PasswordResetToken;
import com.gha.gender_healthcare_api.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying; // Thêm import này
import org.springframework.data.jpa.repository.Query; // Thêm import này
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.List;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByToken(String token);

    // Phương thức để tìm tất cả các token đặt lại mật khẩu cho một người dùng cụ
    // thể
    List<PasswordResetToken> findByUser(User user);

    // Sử dụng @Modifying và @Query để đảm bảo xóa tất cả token của người dùng
    // Phương thức này sẽ xóa tất cả các bản ghi PasswordResetToken liên quan đến
    // một User cụ thể.
    @Modifying // Bắt buộc cho các query sửa đổi (INSERT, UPDATE, DELETE)
    @Transactional // Bắt buộc cho các query sửa đổi
    @Query("DELETE FROM PasswordResetToken prt WHERE prt.user = :user") // JPQL query để xóa theo đối tượng User
    void deleteAllByUser(User user); // Đổi tên phương thức để rõ ràng hơn về mục đích
}
