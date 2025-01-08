# Belote Tournament Manager
## Project Overview

The Belote Tournament Manager is a web-based application developed using Spring Boot that enables organizing and managing Belote card game tournaments. This project represents a modernization effort, transforming a legacy Java application into a contemporary web solution while maintaining core functionality.

## Technical Stack

- **Framework**: Spring Boot
- **Database**: MySQL
- **Security**: Spring Security
- **Frontend**: Thymeleaf + Bootstrap
- **Build Tool**: Maven
- **Testing**: JUnit 5, Mockito

## Core Features

### User Management
- Role-based access control (ADMIN and USER roles)
- User registration and authentication
- Password encryption using BCrypt

### Tournament Management
- Tournament creation and deletion
- Status management (REGISTRATION, IN_PROGRESS, COMPLETED)
- Team registration system
- Automated match generation
- Score tracking and standings calculation

### Match System
- Automated round generation
- Score recording
- Match status tracking
- Tournament completion detection

## Architecture

### Package Structure
```
com.belote
├── config
│   ├── DataInitializer
│   └── SecurityConfig
├── controller
│   ├── web
│   │   ├── AuthController
│   │   └── WebTournamentController
│   ├── TeamController (REST)
|   └── AdminController
├── dto
│   └── TeamStanding
├── entity
│   ├── Match
│   ├── Team
│   ├── Tournament
│   └── User
├── repository
│   ├── MatchRepository
│   ├── TeamRepository
│   ├── TournamentRepository
│   └── UserRepository
└── service
    ├── MatchService
    ├── TeamService
    ├── TournamentService
    └── UserService
```

### Security Implementation
- Form-based authentication
- URL-based authorization
- CSRF protection
- Role-based method security
- Encrypted password storage

### Database Schema
- Users table (id, username, password, role)
- Tournaments table (id, name, status, number_of_teams)
- Teams table (id, tournament_id, team_number, player1_name, player2_name)
- Matches table (id, tournament_id, team1_id, team2_id, score1, score2, round_number, completed)

## Testing

### Controller Tests
```java
@WebMvcTest(WebTournamentController.class)
class WebTournamentControllerTest {
    // Test cases for web endpoints
    - Tournament creation
    - Team management
    - Match score updates
    - Tournament status changes
}
```

### Service Tests
```java
@ExtendWith(MockitoExtension.class)
class TournamentServiceTest {
    // Test cases for tournament business logic
    - Tournament creation with unique names
    - Status updates with validation
    - Match generation
    - Standings calculation
}

class TeamServiceTest {
    // Test cases for team management
    - Team addition during registration
    - Team updates
    - Team deletion with reordering
}

```

## Sprint Progress

### Sprint 1: Core System Modernization
- ✅ Spring Boot setup
- ✅ Database migration to MySQL
- ✅ Basic web interface implementation
- ✅ Entity mapping

### Sprint 2: Team Management
- ✅ Team registration system
- ✅ Team editing functionality
- ✅ Validation implementation
- ✅ Team management interface

### Sprint 3: Match System
- ✅ Match generation algorithm
- ✅ Scoring system
- ✅ Round management
- ✅ Match verification

### Sprint 4: Security Implementation
- ✅ Spring Security integration
- ✅ User authentication
- ✅ Role-based access
- ✅ Password hashing

### Sprint 5: Testing & Documentation
- ✅ Unit tests implementation
- ✅ Controller tests
- ✅ Service layer tests
- ✅ Documentation generation

## Future Improvements

1. **Additional Features**
   - Multi-language support
   - Advanced reporting
   - Tournament templates

2. **Technical Enhancements**
   - Cache implementation
   - Performance optimizations
   - Enhanced error handling

3. **Testing Expansions**
   - End-to-end testing
   - Performance testing
   - Security testing

## Setup Instructions

1. **Prerequisites**
   - Java 17 or later
   - MySQL 8.0 or later
   - Maven 3.6 or later

2. **Database Setup**
   ```sql
   CREATE DATABASE belote_tournament;
   ```

3. **Application Properties**
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/belote_tournament
   spring.datasource.username=your_username
   spring.datasource.password=your_password
   ```

4. **Build and Run**
   ```bash
   mvn clean install
   java -jar target/belote-tournament.jar
   ```

5. **Initial Access**
   - Default admin credentials:
     - Username: admin
     - Password: admin123

## Conclusion

The Belote Tournament Manager successfully modernized the legacy application while maintaining its core functionality. The new implementation provides a robust, secure, and user-friendly platform for managing Belote tournaments. The addition of comprehensive tests ensures system reliability and maintainability.
