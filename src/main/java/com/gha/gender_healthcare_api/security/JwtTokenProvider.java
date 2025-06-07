//: Tạo, xác thực và trích xuất thông tin từ JWT token cho ứng dụng.
package com.gha.gender_healthcare_api.security;

import com.gha.gender_healthcare_api.entity.User;
import com.gha.gender_healthcare_api.repository.UserRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Optional;
import java.util.stream.Collectors;

@Component // Đánh dấu lớp này là một Spring Component
public class JwtTokenProvider {

    private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);

    @Value("${jwt.secret}") // Lấy giá trị secret key từ application.properties
    private String jwtSecret;

    @Value("${jwt.expiration}") // Lấy thời gian hết hạn token từ application.properties
    private int jwtExpirationInMs;

    @Autowired
    private UserRepository userRepository; // Cần thiết để tìm User nếu principal không phải CustomUserDetails

    // Phương thức để tạo khóa bí mật từ chuỗi jwtSecret
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    // Phương thức tạo JWT token
    public String generateToken(Authentication authentication) {
        Object principal = authentication.getPrincipal();
        logger.info("DEBUG: Loại Principal nhận được trong JwtTokenProvider.generateToken: {}",
                principal.getClass().getName());

        String username;
        String role;
        Long userId = null;

        // Ưu tiên xử lý CustomUserDetails vì nó chứa userId trực tiếp
        if (principal instanceof CustomUserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) principal;
            username = userDetails.getUsername();
            role = userDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.joining(","));
            userId = userDetails.getId(); // Lấy ID trực tiếp từ CustomUserDetails
            logger.info("DEBUG: Principal là CustomUserDetails. Username: {}, UserId: {}", username, userId);
        } else if (principal instanceof OAuth2User) {
            // Nếu principal là OAuth2User nhưng không phải CustomUserDetails (trường hợp
            // hiếm nếu CustomOAuth2UserService hoạt động đúng)
            OAuth2User oauth2User = (OAuth2User) principal;
            username = oauth2User.getAttribute("email"); // Lấy email làm username

            if (username == null || username.trim().isEmpty()) {
                logger.error("OAuth2User principal không chứa thuộc tính 'email' hợp lệ cho username.");
                throw new IllegalArgumentException(
                        "Không thể tạo JWT: Thông tin email của người dùng Google không hợp lệ.");
            }

            role = oauth2User.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.joining(","));

            logger.warn(
                    "CẢNH BÁO: OAuth2User không phải là một instance của CustomUserDetails. Đang cố gắng lấy userId từ database bằng email.");
            Optional<User> userOptional = userRepository.findByEmail(username);
            if (userOptional.isPresent()) {
                userId = userOptional.get().getUserId();
                logger.info("DEBUG: Đã lấy thành công userId {} từ DB cho email {}.", userId, username);
            } else {
                logger.error("LỖI: Không tìm thấy người dùng trong database với email {} để lấy userId cho JWT.",
                        username);
                // Tùy chọn: ném ngoại lệ nếu userId là bắt buộc
                throw new IllegalStateException("Người dùng không tìm thấy trong database sau xác thực OAuth2.");
            }
        } else {
            throw new IllegalArgumentException("Loại principal không được hỗ trợ: " + principal.getClass().getName());
        }

        Date now = new Date(); // Thời gian hiện tại
        Date expiryDate = new Date(now.getTime() + jwtExpirationInMs); // Thời gian hết hạn

        return Jwts.builder()
                .setSubject(username) // Đặt subject của token là username
                .claim("roles", role) // Thêm vai trò vào claims
                .claim("userId", userId) // Thêm userId vào claims của JWT
                .setIssuedAt(new Date()) // Thời gian phát hành token
                .setExpiration(expiryDate) // Thời gian hết hạn token
                .signWith(getSigningKey(), SignatureAlgorithm.HS512) // Ký token với thuật toán HS512
                .compact(); // Nén token thành chuỗi
    }

    // Phương thức lấy username từ JWT token
    public String getUsernameFromJWT(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey()) // Sử dụng khóa để phân tích
                .build()
                .parseClaimsJws(token) // Phân tích token
                .getBody(); // Lấy body chứa các claims

        return claims.getSubject(); // Trả về subject (username)
    }

    // Phương thức kiểm tra tính hợp lệ của JWT token
    public boolean validateToken(String authToken) {
        try {
            Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(authToken);
            return true; // Token hợp lệ
        } catch (MalformedJwtException ex) {
            logger.error("Invalid JWT token: Token JWT không đúng định dạng", ex);
        } catch (ExpiredJwtException ex) {
            logger.error("Expired JWT token: Token JWT đã hết hạn", ex);
        } catch (UnsupportedJwtException ex) {
            logger.error("Unsupported JWT token: Token JWT không được hỗ trợ", ex);
        } catch (IllegalArgumentException ex) {
            logger.error("JWT claims string is empty: Chuỗi JWT rỗng", ex);
        } catch (io.jsonwebtoken.security.SignatureException ex) { // Sử dụng đúng loại SignatureException
            logger.error("Invalid JWT signature: Chữ ký JWT không hợp lệ", ex);
        }
        return false; // Token không hợp lệ
    }
}
