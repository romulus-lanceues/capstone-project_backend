# Medication Reminder App - Backend

A Spring Boot backend application for managing medication schedules with automated email notifications and IoT device integration (NodeMCU buzzer).

## 📋 Features

- **User Authentication**: JWT-based authentication with secure login/logout
- **Medication Scheduling**: Create and manage medication schedules with daily/weekly frequencies
- **Smart Schedule Generation**: Automatically generates child schedules from parent templates
- **Email Notifications**: Sends email reminders 3 minutes before medication time
- **IoT Integration**: Triggers NodeMCU buzzer at exact medication time
- **History Tracking**: View medication history for the past 7 days
- **Performance Optimized**: Implements pagination, query optimization, and efficient scheduled tasks

## 🛠️ Technology Stack

- **Framework**: Spring Boot 3.5.5
- **Language**: Java 21
- **Database**: PostgreSQL (Supabase hosted)
- **Authentication**: JWT (jjwt 0.12.3)
- **Email**: Spring Mail (Gmail SMTP)
- **ORM**: Spring Data JPA with Hibernate
- **Build Tool**: Maven

## 📦 Prerequisites

- Java 21 or higher
- Maven 3.6+
- PostgreSQL database (or Supabase account)
- Gmail account with App Password (for email notifications)
- NodeMCU device (optional, for buzzer feature)

## 🚀 Getting Started

### 1. Clone the Repository

```bash
git clone <your-repo-url>
cd medication-app
```

### 2. Configure Application Properties

Copy the example properties file:

```bash
cp src/main/resources/application.properties.example src/main/resources/application.properties
```

Edit `application.properties` with your credentials:

```properties
# Database
spring.datasource.url=jdbc:postgresql://YOUR_SUPABASE_URL:5432/postgres
spring.datasource.username=YOUR_USERNAME
spring.datasource.password=YOUR_PASSWORD

# JWT Secret (minimum 64 characters)
jwt.secret=YOUR_SECRET_KEY

# Gmail SMTP
spring.mail.username=YOUR_EMAIL@gmail.com
spring.mail.password=YOUR_APP_PASSWORD

# NodeMCU IP (optional)
nodemcu.ip.address=192.168.1.10
```

### 3. Build the Project

```bash
mvn clean install
```

### 4. Run the Application

```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

## 📚 API Endpoints

### Authentication

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| POST | `/api/signup/user` | Validate signup credentials | No |
| POST | `/api/add_user` | Create new user account | No |
| POST | `/api/login/user` | Login user | No |
| POST | `/api/logout/{userId}` | Logout user | No |
| GET | `/api/users/{id}` | Get user by ID | No |
| GET | `/api/signup/user` | List all users (paginated) | No |

### Schedules

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| POST | `/api/schedule-upload` | Create new schedule | Yes |
| GET | `/api/schedule` | Get all schedules (paginated) | No |
| GET | `/api/schedule/{id}` | Get user's schedules | Yes |
| GET | `/api/schedule_today/{id}` | Get today's schedules | No |
| GET | `/api/weekly_history/{id}` | Get 7-day history | No |
| PATCH | `/api/update-schedule-to-done/{userId}/{scheduleId}` | Mark schedule as done | No |
| DELETE | `/api/drop-schedule/{scheduleId}` | Delete schedule | No |

### Notifications

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| GET | `/api/get-notifications/{id}` | Get user notifications | No |

## 📖 API Usage Examples

### Signup
```bash
curl -X POST http://localhost:8080/api/signup/user \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@example.com",
    "password": "password123",
    "name": "John Doe"
  }'
```

### Login
```bash
curl -X POST http://localhost:8080/api/login/user \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@example.com",
    "password": "password123"
  }'
```

### Create Schedule
```bash
curl -X POST http://localhost:8080/api/schedule-upload \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "name": "Aspirin",
    "timeOfIntake": "2025-10-12T09:00:00",
    "frequency": "daily",
    "duration": 7,
    "notes": "Take with food",
    "userEmail": "user@example.com"
  }'
```

### Get Schedules (with pagination)
```bash
# Default (page 0, size 20)
curl http://localhost:8080/api/schedule

# Custom pagination
curl "http://localhost:8080/api/schedule?page=0&size=10&sortBy=name&sortDirection=ASC"
```

## ⚙️ Configuration

### Scheduled Tasks

The application runs automated tasks using cron expressions:

- **Daily Schedule Generation**: Runs at midnight (`0 0 0 * * *`)
- **Email Notifications**: Runs every minute (`0 * * * * *`)
- **Buzzer Trigger**: Runs every minute (`0 * * * * *`)

### Database Schema

The application uses Hibernate auto-DDL with `update` mode. Tables are created automatically:

- `app_users` - User accounts
- `schedule` - Medication schedules
- `notification` - Notification history
- `active_user` - Active user sessions
- `intake_table` - Medication intake records

## 🔧 Development

### Enable SQL Logging

Add to `application.properties`:

```properties
spring.jpa.show-sql=true
logging.level.org.hibernate.SQL=DEBUG
logging.level.org.springframework.scheduling=DEBUG
```

### Testing Scheduled Tasks

1. Create a schedule for 3 minutes from now
2. Watch the logs for email notification (3 min before)
3. Watch the logs for buzzer trigger (at exact time)

Expected log output:
```
2025-10-12 09:27:00 INFO - Email notification sent for: Aspirin at 2025-10-12T09:27:00
2025-10-12 09:30:00 INFO - Buzzer triggered for: Aspirin at 09:30
```

## 📊 Performance Optimizations

This backend implements several performance optimizations:

1. **N+1 Query Prevention**: Uses JOIN FETCH for related entities
2. **Batch Fetching**: Loads notifications in batches of 20
3. **Pagination**: All list endpoints support pagination
4. **Cron Scheduling**: Tasks run at exact minute boundaries
5. **Query Optimization**: Custom JPQL queries for complex operations

See [PERFORMANCE_IMPROVEMENTS.md](PERFORMANCE_IMPROVEMENTS.md) for details.

## 🐛 Troubleshooting

### Email Notifications Not Sending

1. Check Gmail App Password is correct
2. Verify 2FA is enabled on Gmail account
3. Check firewall allows port 587
4. Review logs for SMTP errors

### NodeMCU Buzzer Not Triggering

1. Verify NodeMCU IP address in `application.properties`
2. Ensure NodeMCU is on same network
3. Check NodeMCU endpoint: `http://YOUR_IP/trigger-buzzer`
4. Review logs for connection errors

### Database Connection Issues

1. Verify Supabase credentials
2. Check database URL format
3. Ensure database allows connections from your IP
4. Test connection with PostgreSQL client

## 📁 Project Structure

```
src/
├── main/
│   ├── java/com/mediciationbox/capstone/medication_app/
│   │   ├── config/          # Configuration classes
│   │   ├── controller/      # REST controllers
│   │   ├── dto/             # Data Transfer Objects
│   │   ├── exception/       # Custom exceptions
│   │   ├── model/           # JPA entities
│   │   ├── repository/      # Data repositories
│   │   └── service/         # Business logic
│   └── resources/
│       └── application.properties  # Configuration
└── test/                    # Test classes
```


## 📝 License

This is a capstone project for educational purposes.

## 👥 Contributors

- Lance Jade A. Buela - Developer

## 🙏 Acknowledgments

- Spring Boot team for the excellent framework
- Supabase for database hosting
- Gmail for SMTP services

---

**Note**: This is a capstone project designed for demonstration purposes. For production deployment, additional security measures (password hashing, rate limiting, HTTPS, etc.) should be implemented.
