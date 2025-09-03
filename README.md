🐞 Software Bug Reporting & Tracking System

A full-stack web application for managing software bug reports, detecting duplicates, and tracking progress. Built with Java, Spring Boot, and Thymeleaf, this project was designed to improve software quality by making bug reporting and resolution more efficient.

✨ Features

🔐 User Accounts – secure login & registration with password hashing

📝 Bug Reporting – users can submit detailed bug reports

🏷 Bug Classification – automatic categorisation of bugs based on description (e.g., UI Bug, Performance, Security)

🔍 Duplicate Detection – compares new reports with existing ones using Cosine Similarity

🗂 Bug Tracking – view, update, and resolve bugs via an interactive dashboard

📬 Contact Form – users can send feedback or contact administrators

🏢 Company Integration – supports multiple companies/projects

🛠 Tech Stack

Backend: Java 19, Spring Boot

Frontend: Thymeleaf, HTML, CSS

Database: MySQL / H2 (configurable)

Security: Spring Security (BCrypt password hashing)

Duplicate Detection: Cosine Similarity (NLP approach)

Build Tool: Gradle

🚀 Getting Started
1️⃣ Clone the repository
git clone https://github.com/AndyAkofioSowah/bug-tracking-system.git
cd bug-tracking-system

2️⃣ Run the application
./gradlew bootRun


Or run directly from IntelliJ.

3️⃣ Access the app

Open browser: http://localhost:8080

Default login (if seeded):

username: admin@example.com
password: password

📸 Screenshots
🔑 Login Page

📝 Bug Report Form

📋 Bug List with Categories

🔍 Duplicate Detection in Action

🎥 Demo Video

▶️ Watch Demo on YouTube

(-)

📌 Future Improvements

Add email notifications when a bug is assigned/resolved

Add role-based access (Admin / Developer / Reporter)

Add analytics dashboard for bug trends

👨‍💻 Author

Andy Akofio-Sowah





