# 🔐 CyberTrace – Backend

### Digital Forensics Case Management System

CyberTrace Backend is the server-side component of CyberTrace, a full-stack cybersecurity application designed to organize digital forensic investigations through centralized case management, investigator management, investigation notes, findings, and audit tracking.

Built using Java and Spring Boot, the backend exposes REST APIs that connect the frontend with the MySQL database. It handles application logic, authentication, authorization, and the processing of case-related information.

---

## 📌 Table of Contents

- [Overview](#-overview)
- [Features](#-features)
- [Technology Stack](#-technology-stack)
- [Architecture](#-architecture)
- [Project Structure](#-project-structure)
- [Prerequisites](#-prerequisites)
- [Installation and Setup](#-installation-and-setup)
- [Database Configuration](#-database-configuration)
- [Running the Application](#-running-the-application)
- [API Overview](#-api-overview)
- [Authentication and Authorization](#-authentication-and-authorization)
- [Frontend Integration](#-frontend-integration)
- [Railway Deployment](#-railway-deployment)
- [Demo Access](#-demo-access)
- [Security Considerations](#-security-considerations)
- [Future Enhancements](#-future-enhancements)
- [Related Repository](#-related-repository)
- [Developer](#-developer)

---

## 🎯 Overview

Digital forensic investigations require organized case records, investigation documentation, controlled access, and the ability to track relevant activities.

CyberTrace provides backend services to support these requirements through RESTful APIs and a relational database.

The backend is responsible for:

- Processing requests from the frontend.
- Authenticating users.
- Enforcing role-based permissions.
- Managing investigation cases.
- Managing investigator accounts.
- Maintaining investigation notes and findings.
- Interacting with the MySQL database.
- Recording relevant audit activities.

The application follows a layered backend architecture to separate API handling, business logic, and database operations.

---

## ✨ Features

### 🔑 Authentication and Authorization
- User authentication.
- Password hashing using a password encoder.
- Role-based access for administrators and investigators.
- Protected endpoints for authorized operations.

### 📁 Case Management
- Create and retrieve investigation cases.
- Update case information.
- Delete cases according to application permissions and constraints.
- Associate investigation records with cases.

### 👥 User and Investigator Management
- Retrieve registered users.
- Retrieve investigator accounts.
- Create investigator accounts through administrative functionality.
- Enable or disable user accounts.

### 🔎 Investigation Findings
- Store findings associated with investigation cases.
- Retrieve and manage case-related findings.

### 📝 Investigation Notes
- Store investigation notes.
- Associate notes with relevant cases.

### 📋 Audit Tracking
- Record relevant application and administrative activities.
- Support activity monitoring.

### 🗄️ Database Integration
- MySQL database connectivity.
- Persistent storage for application records.
- Entity relationships managed through the application's persistence layer.

---

## 🛠️ Technology Stack

| Technology | Purpose |
|---|---|
| Java | Backend programming language |
| Spring Boot | Application framework |
| Spring Web | REST API development |
| Spring Security | Authentication and authorization |
| Spring Data JPA | Database operations |
| Hibernate | Object-relational mapping |
| MySQL | Relational database |
| Maven | Dependency management and build automation |
| BCrypt | Password hashing, if configured through the password encoder |

---

## 🏗️ Architecture

CyberTrace Backend uses a layered architecture.

```text
┌──────────────────────────────┐
│       React Frontend         │
└──────────────┬───────────────┘
               │
               │ HTTP / REST API
               ▼
┌──────────────────────────────┐
│       Controllers            │
│                              │
│  AuthController              │
│  AdminController             │
│  Case-related Controllers    │
└──────────────┬───────────────┘
               │
               ▼
┌──────────────────────────────┐
│        Service Layer         │
│                              │
│  Authentication Logic        │
│  User Management             │
│  Case Management             │
│  Investigation Operations    │
└──────────────┬───────────────┘
               │
               ▼
┌──────────────────────────────┐
│       Repository Layer       │
│                              │
│  Spring Data JPA             │
│  Database Queries            │
└──────────────┬───────────────┘
               │
               ▼
┌──────────────────────────────┐
│          MySQL               │
│                              │
│  Users and Roles              │
│  Cases and Findings           │
│  Investigation Notes          │
│  Audit Records                │
└──────────────────────────────┘

## 🚆 Railway Deployment

Deploy this repository to Railway using its root `Dockerfile`. Configure these variables on the Railway service:

```env
DB_URL=jdbc:mysql://<mysql-host>:3306/cybertrace_db?useSSL=true&serverTimezone=UTC
DB_USERNAME=<mysql-user>
DB_PASSWORD=<mysql-password>
JWT_SECRET=<random-secret-at-least-32-bytes>
CORS_ALLOWED_ORIGINS=https://cyber-trace-frontend-alpha.vercel.app
```

Generate a Railway public domain for the service. In the Vercel frontend project's Production environment, set `VITE_API_URL` to `https://<railway-domain>/api`, then redeploy the frontend. The frontend's `VITE_API_URL` and the backend's `CORS_ALLOWED_ORIGINS` must point to the Railway API and the Vercel production domain respectively for sign-in and API requests to work.
