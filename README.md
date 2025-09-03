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
cd bug-tracking-system

### Run the application

Copy code
./gradlew bootRun
Or run directly from IntelliJ IDEA.

Access the app
Open http://localhost:8080 in your browser.


### Screenshots
Add screenshots to docs/screenshots/ and reference them here:

Login Page

Bug Report Form

Bug List

Duplicate Detection Example


![Login](docs/screenshots/login.png)
![Bug Report](docs/screenshots/report-bug.png)
![Bug List](docs/screenshots/bug-list.png)
![Duplicate Detection](docs/screenshots/duplicate-detection.png)

### Future Improvements
Email notifications for bug updates

Role-based access control (Admin / Developer / Reporter)

Analytics dashboard for bug trends

### Author
Andy Akofio-Sowah




