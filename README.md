# Fintech Spring Boot Microservices Platform

A comprehensive fintech platform built with Spring Boot microservices architecture, featuring authentication, user profiles, and lending engine capabilities.

## 🏗️ Architecture Overview

This platform implements a microservices architecture with service discovery, API gateway, and event-driven communication.

### Core Services
- **Eureka Server** - Service Discovery & Registration
- **API Gateway** - Single entry point with routing & load balancing
- **Authentication Service** - JWT-based authentication & authorization
- **Profile Service** - User profile management
- **Lending Engine** - Loan processing & credit scoring

### Infrastructure Components
- **MySQL 8.0** - Primary database
- **RabbitMQ** - Message broker for async communication
- **Docker & Docker Compose** - Containerization & orchestration

## 🚀 Key Features

### Authentication & Security
- JWT token-based authentication
- Role-based access control (RBAC)
- Secure API endpoints with Spring Security
- Password encryption with BCrypt

### User Management
- Complete user profile CRUD operations
- Profile validation and verification
- User preference management
- Account status tracking

### Lending Engine
- Credit score calculation algorithms
- Loan application processing
- Risk assessment models
- Automated decision workflows

### Microservices Patterns
- **Service Discovery** - Eureka-based service registration
- **API Gateway** - Centralized routing with Spring Cloud Gateway
- **Event-Driven Architecture** - RabbitMQ messaging
- **Database per Service** - Independent data stores

## 🛠️ Technology Stack

### Backend
- **Java 11** - Core programming language
- **Spring Boot 2.4.5** - Application framework
- **Spring Cloud 2020.0.5** - Microservices toolkit
- **Spring Security** - Authentication & authorization
- **Spring Data JPA** - Data persistence
- **MySQL Connector** - Database connectivity
- **RabbitMQ** - Message queuing

### DevOps & Infrastructure
- **Docker** - Containerization
- **Docker Compose** - Multi-container orchestration
- **Maven** - Build automation
- **Git** - Version control

### Monitoring & Observability
- **Spring Boot Actuator** - Health checks & metrics
- **Eureka Dashboard** - Service monitoring
- **RabbitMQ Management** - Queue monitoring

## 📊 System Architecture

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Client Apps   │    │   Mobile Apps   │    │  Third Party    │
└─────────┬───────┘    └─────────┬───────┘    └─────────┬───────┘
          │                      │                      │
          └──────────────────────┼──────────────────────┘
                                 │
                    ┌─────────────▼─────────────┐
                    │       API Gateway         │
                    │    (Load Balancing)       │
                    └─────────────┬─────────────┘
                                 │
                    ┌─────────────▼─────────────┐
                    │     Eureka Server         │
                    │   (Service Discovery)     │
                    └─────────────┬─────────────┘
                                 │
        ┌────────────────────────┼────────────────────────┐
        │                       │                        │
┌───────▼───────┐    ┌──────────▼──────────┐    ┌────────▼────────┐
│ Auth Service  │    │  Profile Service    │    │ Lending Engine  │
│   (Port 8081) │    │    (Port 8082)      │    │   (Port 8083)   │
└───────┬───────┘    └──────────┬──────────┘    └────────┬────────┘
        │                       │                        │
        └───────────────────────┼────────────────────────┘
                               │
                    ┌──────────▼──────────┐
                    │      RabbitMQ       │
                    │  (Message Broker)   │
                    └─────────────────────┘
                               │
                    ┌──────────▼──────────┐
                    │       MySQL         │
                    │     (Database)      │
                    └─────────────────────┘
```

## 🚦 Quick Start

### Prerequisites
- Java 11+
- Maven 3.6+
- Docker & Docker Compose
- Git

### Installation & Deployment

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd fintech-spring-microservice
   ```

2. **Deploy all services**
   ```bash
   bash deploy.sh
   ```

3. **Verify deployment**
   ```bash
   docker ps
   ```

### Service URLs
- **API Gateway**: http://localhost:8080
- **Eureka Dashboard**: http://localhost:8761
- **RabbitMQ Management**: http://localhost:15673 (admin/admin123)
- **Auth Service**: http://localhost:8081
- **Profile Service**: http://localhost:8082
- **Lending Engine**: http://localhost:8083

## 📋 API Documentation

### Authentication Service
```bash
# Register new user
POST /auth/register
{
  "username": "john_doe",
  "password": "securePassword",
  "role": "john_doe_role"
}

# Login
POST /auth/login
{
  "username": "john_doe",
  "password": "securePassword"
}

# Validate token
GET /auth/validate?token=<jwt_token>
```

### Profile Service
```bash
# Create profile
POST /api/profiles
{
  "userId": "john_doe_userId",
  "username": "john_doe"
  "firstname": "John",
  "lastname": "Doe",
  "age": "25",
  "occupation": "businessman",
  "role": "john_doe_role"
}

# Get profile
GET /api/profiles/{userId}

# Update profile
PUT /api/profiles/{userId}

# Delete profile
DELETE /api/profiles/{userId}
```

### Lending Engine
```bash
# Apply for loan
POST /api/loans/apply
{
  "userId": "john_doe_userId",
  "amount": 50000,
  "purpose": "home_improvement",
  "daysToRepay": 31,
  "interestRate": 6.2
}

# Check loan status
GET /api/loans/{loanId}/status

# Get credit score
GET /api/credit-score/{userId}
```

## 🧪 Testing

### Health Checks
```bash
# Check all services health
curl http://localhost:8080/actuator/health
curl http://localhost:8081/actuator/health
curl http://localhost:8082/actuator/health
curl http://localhost:8083/actuator/health
```

### Service Discovery
```bash
# View registered services
curl http://localhost:8761/eureka/apps
```

### Message Queue
```bash
# Check queue status
curl http://localhost:15673/api/queues
```

## 🔧 Configuration

### Database Configuration
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3307/fintech_db
    username: fintech_user
    password: fintech_pass
  jpa:
    hibernate:
      ddl-auto: update
```

### RabbitMQ Configuration
```yaml
spring:
  rabbitmq:
    host: localhost
    port: 5673
    username: admin
    password: admin123
```

## 🛡️ Security Features

- **JWT Authentication** - Stateless token-based auth
- **Password Encryption** - BCrypt hashing
- **CORS Configuration** - Cross-origin resource sharing
- **Rate Limiting** - API request throttling
- **Input Validation** - Request sanitization

## 📈 Performance & Scalability

- **Horizontal Scaling** - Service instances can be scaled independently
- **Load Balancing** - API Gateway distributes requests
- **Caching** - Redis integration for session management
- **Database Optimization** - Indexed queries and connection pooling

## 🐳 Docker Configuration

### Multi-stage Builds
- Optimized container images
- Minimal production footprint
- Health check implementation
- Graceful shutdown handling

### Container Orchestration
- Docker Compose for local development
- Environment-specific configurations

## 📊 Monitoring & Observability

- **Health Endpoints** - Spring Boot Actuator
- **Service Metrics** - Custom metrics collection
- **Distributed Tracing** - Request flow monitoring
- **Log Aggregation** - Centralized logging

## 🚀 Future Enhancements

- [ ] Kubernetes deployment manifests
- [ ] Redis caching implementation
- [ ] Prometheus metrics integration
- [ ] ELK stack for log analysis
- [ ] API rate limiting
- [ ] OAuth2 integration
- [ ] Notification service
- [ ] Audit logging service
- [ ] Payment gateway integration
- [ ] Machine learning credit scoring

## 👥 Contributing

1. Fork the repository
2. Create feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit changes (`git commit -m 'Add AmazingFeature'`)
4. Push to branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details.

## 🤝 Contact

**Ganeev** - ganeevkc23@gmail.com

Project Link: https://github.com/ganeevkc/fintech-microservice-app

---

## 🏆 Project Highlights

✅ **Enterprise-Grade Architecture** - Production-ready microservices design
✅ **Scalable Infrastructure** - Docker containerization with service discovery
✅ **Security Best Practices** - JWT authentication with role-based access
✅ **Event-Driven Design** - Asynchronous messaging with RabbitMQ
✅ **Database Design** - Normalized schema with proper relationships
✅ **DevOps Integration** - Automated deployment with Docker Compose
✅ **Monitoring Ready** - Health checks and metrics endpoints
✅ **Documentation** - Comprehensive API documentation and setup guides
