# 🛡️ Connected Protection Hub (CPH)

[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://openjdk.org/projects/jdk/17/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.0-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-blue.svg)](https://www.postgresql.org/)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

A modular Spring Boot platform for comprehensive protection, insurance, and assistance management.

## 📋 About

**Connected Protection Hub** is a complete solution for companies offering protection services for electronics, vehicles, properties, and financial assets. Built with modern Java/Spring Boot architecture implementing DDD and enterprise patterns.

## 🚀 Features

- 👥 **Customer Management** - Complete registration with validations
- 💼 **Protected Assets** - Electronics, vehicles, properties, financial assets
- 📑 **Protection Plans** - Custom coverage with limits and deductibles
- 🚨 **Claims Processing** - End-to-end claim management
- 🔧 **Repair Orders** - Service tracking and technical assistance
- 💳 **Payment Processing** - Multiple payment methods
- 📊 **Technical Assessments** - Expert evaluations and reports

## 🏗️ Architecture

**Technology Stack:**
- **Backend:** Java 17, Spring Boot 3, Spring Data JPA, Hibernate
- **Database:** PostgreSQL (production), H2 (development)
- **API:** RESTful, OpenAPI 3.0, Swagger UI
- **Tools:** MapStruct, Lombok, Maven, Spring Cache, Validation

**Architecture Patterns:**
- Domain-Driven Design (DDD)
- Clean Architecture
- Repository & Service Patterns
- DTO Pattern
- Layered Architecture

## 🛠️ Quick Start

### Prerequisites
- Java 17+
- Maven 3.6+
- PostgreSQL (optional)

### Installation
```bash
# Clone repository
git clone https://github.com/your-username/connected-protection-hub.git
cd connected-protection-hub

# Run development mode
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# Access application
# API: http://localhost:8080
# Swagger: http://localhost:8080/swagger-ui.html
# H2 Console: http://localhost:8080/h2-console

📚 API Examples

Create Customer:

curl -X POST "http://localhost:8080/api/v1/customers" \
  -H "Content-Type: application/json" \
  -d '{
    "fullName": "John Doe",
    "email": "john.doe@email.com",
    "phoneNumber": "+1-555-0123",
    "documentNumber": "123.456.789-00",
    "documentType": "CPF"
  }'


Create Protection Plan:

curl -X POST "http://localhost:8080/api/v1/protection-plans" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Premium Smartphone Protection",
    "startDate": "2024-01-15T00:00:00",
    "endDate": "2025-01-15T00:00:00",
    "premiumAmount": 299.99,
    "customerId": "123e4567-e89b-12d3-a456-426614174000",
    "assetId": "223e4567-e89b-12d3-a456-426614174000"
  }'

📁 Project Structure
src/main/java/com/assurant/cph/
├── core/
│   ├── domain/          # JPA Entities
│   ├── repository/      # Spring Data Repositories
│   ├── service/         # Business Logic
│   └── mapper/          # MapStruct Mappers
├── api/
│   ├── controller/      # REST Controllers
│   └── dto/            # Data Transfer Objects
└── config/             # Configuration Classes


