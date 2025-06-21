# Settings & Configuration Module

Module quản lý cấu hình hệ thống và tùy chọn cá nhân người dùng cho Gender Healthcare API.

## Tổng quan

Module này cung cấp hai chức năng chính:

1. **User Preferences**: Quản lý tùy chọn cá nhân của người dùng (ngôn ngữ, múi giờ, thông báo, giao diện, v.v.)
2. **System Configuration**: Quản lý cấu hình hệ thống toàn cục (chỉ admin có quyền truy cập)

## Cấu trúc Module

```
settings/
├── config/
│   └── SettingsConfig.java           # Cấu hình module (cache, constants)
├── controller/
│   ├── UserPreferenceController.java # REST API cho user preferences
│   └── SystemConfigurationController.java # REST API cho system config
├── dto/
│   ├── UserPreferenceUpdateRequest.java
│   ├── UserPreferenceResponse.java
│   ├── SystemConfigurationRequest.java
│   └── SystemConfigurationResponse.java
├── entity/
│   ├── UserPreference.java          # Entity cho tùy chọn người dùng
│   ├── SystemConfiguration.java     # Entity cho cấu hình hệ thống
│   └── ConfigurationAuditLog.java   # Entity cho audit log
├── repository/
│   ├── UserPreferenceRepository.java
│   ├── SystemConfigurationRepository.java
│   └── ConfigurationAuditLogRepository.java
├── service/
│   ├── UserPreferenceService.java   # Business logic cho user preferences
│   └── SystemConfigurationService.java # Business logic cho system config
├── settings_tables.sql              # Script tạo database tables
└── README.md                        # Tài liệu này
```

## Chức năng User Preferences

### Các tùy chọn có sẵn:
- **language**: Ngôn ngữ giao diện (vi, en)
- **timezone**: Múi giờ (Asia/Ho_Chi_Minh, UTC, v.v.)
- **email_notifications**: Bật/tắt thông báo email
- **sms_notifications**: Bật/tắt thông báo SMS
- **push_notifications**: Bật/tắt thông báo push
- **theme**: Giao diện (light, dark)
- **date_format**: Định dạng ngày (dd/MM/yyyy, MM/dd/yyyy, v.v.)
- **time_format**: Định dạng giờ (HH:mm, hh:mm a)
- **cycle_reminder_enabled**: Bật/tắt nhắc nhở chu kỳ
- **contraceptive_reminder_enabled**: Bật/tắt nhắc nhở thuốc tránh thai
- **privacy_mode**: Chế độ riêng tư

### API Endpoints:

#### Người dùng thông thường:
- `GET /api/v1/user-preferences/me` - Lấy tùy chọn của mình
- `PUT /api/v1/user-preferences/me` - Cập nhật tùy chọn
- `POST /api/v1/user-preferences/me/reset` - Khôi phục về mặc định
- `GET /api/v1/user-preferences/me/{key}` - Lấy giá trị một tùy chọn cụ thể
- `PUT /api/v1/user-preferences/me/{key}` - Cập nhật một tùy chọn cụ thể

#### Admin:
- `GET /api/v1/user-preferences/users/{userId}` - Xem tùy chọn của user khác
- `POST /api/v1/user-preferences/users/{userId}/reset` - Reset tùy chọn user khác
- `GET /api/v1/user-preferences/statistics` - Thống kê sử dụng tùy chọn

### Ví dụ Request/Response:

**Cập nhật tùy chọn:**
```json
PUT /api/v1/user-preferences/me
{
  "language": "vi",
  "timezone": "Asia/Ho_Chi_Minh",
  "emailNotifications": true,
  "theme": "dark",
  "dateFormat": "dd/MM/yyyy"
}
```

**Response:**
```json
{
  "userId": 123,
  "language": "vi",
  "timezone": "Asia/Ho_Chi_Minh",
  "emailNotifications": true,
  "smsNotifications": false,
  "pushNotifications": true,
  "theme": "dark",
  "dateFormat": "dd/MM/yyyy",
  "timeFormat": "HH:mm",
  "cycleReminderEnabled": true,
  "contraceptiveReminderEnabled": true,
  "privacyMode": false,
  "createdAt": "2025-06-21T10:00:00",
  "updatedAt": "2025-06-21T10:30:00"
}
```

## Chức năng System Configuration

### Các nhóm cấu hình:
- **GENERAL**: Cấu hình chung
- **NOTIFICATION**: Cấu hình thông báo
- **SECURITY**: Cấu hình bảo mật
- **INTEGRATION**: Cấu hình tích hợp
- **UI**: Cấu hình giao diện
- **BUSINESS**: Cấu hình nghiệp vụ

### Các loại dữ liệu hỗ trợ:
- **STRING**: Chuỗi văn bản
- **INTEGER**: Số nguyên
- **BOOLEAN**: True/False
- **DECIMAL**: Số thập phân
- **JSON**: Đối tượng JSON

### API Endpoints (Chỉ Admin):

- `GET /api/v1/system-configurations` - Lấy danh sách cấu hình (có phân trang)
- `GET /api/v1/system-configurations/key/{configKey}` - Lấy cấu hình theo key
- `GET /api/v1/system-configurations/{id}` - Lấy cấu hình theo ID
- `GET /api/v1/system-configurations/category/{category}` - Lấy cấu hình theo nhóm
- `GET /api/v1/system-configurations/search?keyword=xxx` - Tìm kiếm cấu hình
- `POST /api/v1/system-configurations` - Tạo cấu hình mới
- `PUT /api/v1/system-configurations/{id}` - Cập nhật cấu hình
- `DELETE /api/v1/system-configurations/{id}` - Xóa cấu hình
- `PATCH /api/v1/system-configurations/{id}/status` - Kích hoạt/vô hiệu hóa
- `GET /api/v1/system-configurations/{id}/history` - Lịch sử thay đổi
- `GET /api/v1/system-configurations/value/{configKey}` - Lấy giá trị (cho service khác)

### Ví dụ tạo cấu hình:
```json
POST /api/v1/system-configurations
{
  "configKey": "max_login_attempts",
  "configValue": "5",
  "description": "Số lần đăng nhập tối đa trước khi khóa tài khoản",
  "category": "SECURITY",
  "dataType": "INTEGER",
  "isSystem": false
}
```

## Caching

Module sử dụng Spring Cache để tối ưu hiệu suất:

- **systemConfig**: Cache cho system configuration
- **systemConfigValue**: Cache cho giá trị configuration
- **userPreferences**: Cache cho user preferences
- **preferenceStatistics**: Cache cho thống kê

## Audit Log

Tất cả thay đổi system configuration đều được ghi lại trong bảng `configuration_audit_logs` bao gồm:
- ID cấu hình
- Người thực hiện thay đổi
- Hành động (CREATE, UPDATE, DELETE, STATUS_CHANGE)
- Giá trị cũ và mới
- Thời gian thay đổi

## Security

### Phân quyền:
- **User Preferences**: Tất cả user đã đăng nhập có thể quản lý tùy chọn của mình
- **System Configuration**: Chỉ ADMIN mới có quyền truy cập
- **Configuration Value**: ADMIN, MANAGER, STAFF, CONSULTANT có thể đọc giá trị

### Validation:
- Validate định dạng config key và preference key
- Validate data type và giá trị tương ứng
- Validate quyền truy cập system configuration
- Kiểm tra độ dài tối đa của các trường

## Database Schema

Xem file `settings_tables.sql` để biết chi tiết về:
- Cấu trúc bảng `user_preferences`
- Cấu trúc bảng `system_configurations`
- Cấu trúc bảng `configuration_audit_logs`
- Dữ liệu mẫu và cấu hình mặc định

## Cách sử dụng

### 1. Khởi tạo database:
```sql
-- Chạy script tạo bảng
source settings_tables.sql;
```

### 2. Sử dụng trong code:

**Lấy user preference:**
```java
@Autowired
private UserPreferenceService userPreferenceService;

String language = userPreferenceService.getUserPreferenceValue(userId, "language");
```

**Lấy system configuration:**
```java
@Autowired
private SystemConfigurationService systemConfigService;

String maxAttempts = systemConfigService.getConfigurationValue("max_login_attempts", "3");
```

### 3. Tích hợp với frontend:

Frontend có thể gọi API để:
- Hiển thị form cài đặt tùy chọn cá nhân
- Lưu tùy chọn người dùng
- Admin panel quản lý cấu hình hệ thống

## Performance Notes

- Sử dụng caching để tăng tốc độ truy xuất
- Lazy loading cho audit logs
- Index trên các trường tìm kiếm thường xuyên
- Pagination cho danh sách lớn

## Future Enhancements

- Thêm import/export cấu hình
- Backup/restore settings
- Configuration templates
- Notification khi cấu hình thay đổi
- Multi-tenant configuration
- Configuration versioning
