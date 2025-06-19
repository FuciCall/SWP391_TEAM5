# Track Reproductive Cycle Module

## 📋 Overview
This module provides comprehensive menstrual cycle tracking and reproductive health features including:

- **Menstrual Cycle Tracking**: Record period start/end dates, flow intensity, symptoms
- **Ovulation Predictions**: AI-powered predictions based on cycle history  
- **Fertility Window Tracking**: Calculate fertile days and pregnancy likelihood
- **Contraceptive Pill Reminders**: Daily pill reminders with customizable schedules
- **Smart Notifications**: Email alerts for ovulation, periods, and pill reminders

## 🏗️ Architecture

```
cycle/
├── entity/              # JPA Entities
│   ├── MenstrualCycle.java
│   ├── ContraceptiveReminder.java
│   ├── CyclePrediction.java
│   └── CycleNotification.java
├── repository/          # Data Access Layer
│   ├── MenstrualCycleRepository.java
│   ├── ContraceptiveReminderRepository.java
│   ├── CyclePredictionRepository.java
│   └── CycleNotificationRepository.java
├── service/            # Business Logic
│   ├── MenstrualCycleService.java
│   ├── CyclePredictionService.java
│   ├── ContraceptiveReminderService.java
│   └── CycleNotificationService.java
├── controller/         # REST API Controllers
│   ├── MenstrualCycleController.java
│   ├── ContraceptiveController.java
│   └── CycleNotificationController.java
├── dto/               # Data Transfer Objects
│   ├── MenstrualCycleRequest.java
│   ├── ContraceptiveReminderRequest.java
│   └── CycleAnalytics.java
└── config/            # Configuration
    └── CycleSchedulingConfig.java
```

## 🔧 Database Tables

The module creates 4 new tables:

1. **menstrual_cycles** - Store cycle data (start/end dates, flow, symptoms)
2. **contraceptive_reminders** - Pill reminder settings 
3. **cycle_predictions** - AI predictions for ovulation and fertile windows
4. **cycle_notifications** - Scheduled notifications and reminders

## 🚀 API Endpoints

### Menstrual Cycle Tracking
```bash
# Record a new cycle
POST /api/menstrual-cycle/declare
Content-Type: application/json
Authorization: Bearer {jwt_token}

{
  "startDate": "2025-06-15",
  "endDate": "2025-06-20", 
  "flowIntensity": "NORMAL",
  "symptoms": "cramps,bloating",
  "notes": "Felt tired during this cycle"
}

# Get cycle history (last 12 months)
GET /api/menstrual-cycle/history?months=12
Authorization: Bearer {jwt_token}

# Get cycle analytics and insights
GET /api/menstrual-cycle/analytics
Authorization: Bearer {jwt_token}

# Get predictions (ovulation, fertile window)
GET /api/menstrual-cycle/predictions
Authorization: Bearer {jwt_token}

# Refresh predictions
POST /api/menstrual-cycle/predictions/refresh
Authorization: Bearer {jwt_token}
```

### Contraceptive Pill Reminders
```bash
# Setup daily pill reminder
POST /api/contraceptive/pill-reminder/setup
Content-Type: application/json
Authorization: Bearer {jwt_token}

{
  "pillName": "Yasmin",
  "reminderTime": "08:00",
  "packStartDate": "2025-06-01",
  "packDuration": 21,
  "breakDuration": 7,
  "timezone": "Asia/Ho_Chi_Minh"
}

# Get active reminder
GET /api/contraceptive/pill-reminder
Authorization: Bearer {jwt_token}

# Deactivate reminder
DELETE /api/contraceptive/pill-reminder/{reminderId}
Authorization: Bearer {jwt_token}
```

### Notifications
```bash
# Get unread notifications
GET /api/cycle-notifications
Authorization: Bearer {jwt_token}

# Mark notification as read
PUT /api/cycle-notifications/{notificationId}/read
Authorization: Bearer {jwt_token}
```

## 🔮 Features

### 1. Smart Cycle Predictions
- Uses last 6 cycles for accuracy
- Predicts ovulation 14 days before next period
- Calculates 6-day fertile window
- Real-time pregnancy likelihood based on current date

### 2. Cycle Analytics
- Average cycle and period length
- Cycle regularity assessment (Very Regular → Irregular)
- Personalized health insights
- Long-term pattern analysis

### 3. Automated Notifications
- **Ovulation Alert**: 1 day before predicted ovulation
- **Fertile Window**: When fertile window starts
- **Period Reminder**: 2 days before expected period
- **Pill Reminders**: Daily at user-specified time

### 4. Pill Management
- Support for 21/28-day packs with break periods
- Automatically skips break days
- Timezone-aware reminders
- Easy activation/deactivation

## 💡 Usage Examples

### Recording First Cycle
```javascript
// When user records their first period
const firstCycle = {
  startDate: "2025-06-01",
  endDate: "2025-06-05",
  flowIntensity: "NORMAL",
  symptoms: "mild cramps",
  notes: "First time tracking"
};

// System response includes tips for new users
// No predictions yet (need 2+ cycles)
```

### After Multiple Cycles
```javascript
// After user has 3+ cycles recorded
const analytics = {
  totalCycles: 3,
  averageCycleLength: 29.5,
  averagePeriodLength: 5.0,
  cycleRegularity: "REGULAR", 
  lastPeriodDate: "2025-06-15",
  healthInsights: [
    "Your cycle length is within the normal range (21-35 days)",
    "Your cycles are quite regular with minor variations"
  ]
};

// Predictions become available
const predictions = {
  predictedOvulationDate: "2025-06-29",
  fertileWindowStart: "2025-06-24", 
  fertileWindowEnd: "2025-06-30",
  nextPeriodDate: "2025-07-14",
  pregnancyLikelihood: 15.0 // if today is in fertile window
};
```

### Setting Up Pill Reminders
```javascript
// User wants daily reminders at 8 AM for Yasmin pills
const reminderSetup = {
  pillName: "Yasmin",
  reminderTime: "08:00",
  packStartDate: "2025-06-01", 
  packDuration: 21, // 21 active pills
  breakDuration: 7,  // 7-day break
  timezone: "Asia/Ho_Chi_Minh"
};

// System will schedule:
// - Days 1-21: Daily pill reminders at 8 AM
// - Days 22-28: No reminders (break period)  
// - Day 29: Resume reminders for next pack
```

## 🔒 Security & Privacy

- **Role-based Access**: Only CUSTOMER role can access cycle data
- **User Isolation**: Each user can only access their own data
- **Data Validation**: Input validation on all cycle data
- **Error Handling**: Graceful error handling with user-friendly messages

## 🎯 Integration Notes

This module is designed to work seamlessly with the existing Gender Healthcare API:

- **No Breaking Changes**: Existing functionality remains unchanged
- **User Entity Integration**: Links to existing User entity via userId
- **EmailService Extension**: Extends existing email service with cycle notifications
- **Security Integration**: Uses existing JWT authentication and UserPrincipal
- **Database Friendly**: New tables with proper foreign key relationships

## 🚀 Getting Started

1. The module is already integrated and ready to use
2. Database tables will be auto-created when you run the application
3. Users can immediately start tracking cycles via the API endpoints
4. Email notifications will be sent automatically (configure SMTP settings)

## 📊 Sample Data Flow

1. **User Records Cycle** → Triggers predictions calculation → Schedules notifications
2. **Background Job** (every minute) → Checks for pending notifications → Sends emails
3. **User Gets Reminder** → Marks as read → Analytics updated for insights

This module provides a complete reproductive health tracking solution that integrates naturally with your existing healthcare platform! 🌸
