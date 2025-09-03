# Software Bug Reporting & Tracking System

A full-stack web application for managing software bug reports, detecting duplicates, and tracking their resolution.  
This project was built with **Java, Spring Boot, and Thymeleaf** to streamline software quality assurance.

---

## Features

- Secure login and registration with password hashing  
- Submit detailed bug reports with descriptions and metadata  
- Automatic bug classification (e.g., UI, Performance, Security)  
- Duplicate bug detection using Cosine Similarity  
- Track, update, and resolve bug reports via a dashboard  
- Contact form for user feedback  
- Supports multiple companies/projects
- Role-based access control(Admin/User/Overseer)

---

## Tech Stack

- **Backend:** Java 19, Spring Boot  
- **Frontend:** Thymeleaf, HTML, CSS  
- **Database:** MySQL / H2 (in-memory option for testing)  
- **Security:** Spring Security (BCrypt password hashing)  
- **Duplicate Detection:** Cosine Similarity (NLP approach)  
- **Build Tool:** Gradle  

---

## Getting Started

### Clone the repository

git clone https://github.com/AndyAkofioSowah/bug-tracking-system.git


### Run the application

Run directly from IntelliJ IDEA.

Access the app
Open http://localhost:8080 in your browser.


### Screenshots
Add screenshots to docs/screenshots/ and reference them here:

Login Page

Bug Report Form

Bug List

Duplicate Detection Example


## Screenshots

### 🔹 Welcome Page
![Welcome Page](docs/Welcome_Page.png)

### 🔹 Dashboard (Bug Submission Form)
![Dashboard Form](docs/dashboard:form.png)

### 🔹 Duplicate Bug Detection
![Duplicate Detection](docs/duplicatedetection.png)

### 🔹 Submitted Bugs
![Submitted Bugs](docs/submitted_bugs.png)

### Future Improvements
Email notifications for bug updates


Analytics dashboard for bug trends

### Author
Andy Akofio-Sowah




