//: Ngoại lệ khi mật khẩu xác nhận không khớp (dùng cho đổi mật khẩu, đăng ký...).
package com.gha.gender_healthcare_api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST) // 400 Bad Request
public class PasswordMismatchException extends RuntimeException {
    public PasswordMismatchException(String message) {
        super(message);
    }
}
