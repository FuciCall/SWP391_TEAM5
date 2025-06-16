//: Ngoại lệ khi username đã tồn tại trong hệ thống (dùng cho đăng ký).
package com.gha.gender_healthcare_api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST) // Hoặc HttpStatus.CONFLICT (409)
public class UsernameAlreadyExistsException extends RuntimeException {
    public UsernameAlreadyExistsException(String message) {
        super(message);
    }
}