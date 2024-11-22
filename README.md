# Software Bug Reporting and Tracking System

This repository contains the implementation of the **Software Bug Reporting and Tracking System**, which facilitates the identification, tracking, and resolution of software bugs.

## Repository Structure

Below is the structure of the repository and the purpose of each directory:

### Root-Level Files
- `HELP.md`: Provides additional documentation or guidelines related to the project.
- `README.md`: This file, describing the project structure and usage.
- `build.gradle`: Gradle build script for managing project dependencies and build tasks.
- `gradlew` / `gradlew.bat`: Gradle wrapper scripts for Unix and Windows environments.
- `settings.gradle`: Gradle settings file for multi-project builds.

### Key Directories
| Directory                         | Description                                                                                                                                   |
|-----------------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------|
| `/build`                          | Contains compiled classes, generated sources, and resources.                                                                                  |
| `/build/classes/java/main`        | Compiled Java classes for the application (e.g., `AppApplication.class`).                                                                     |
| `/build/resources/main/templates` | Contains application templates (e.g., `bugs.html`).                                                                                          |
| `/gradle/wrapper`                 | Files required for the Gradle wrapper, including `gradle-wrapper.jar` and `gradle-wrapper.properties`.                                        |
| `/src/main/java`                  | Source code for the application, organized under `com.example.bugtrackingsystem`.                                                            |
| `/src/main/resources`             | Application resources, including configuration (`application.properties`), static files, and templates.                                       |
| `/src/test/java`                  | Unit and integration test files for the project.                                                                                              |

## Key Files
- **Main Application**: `src/main/java/com/example/bugtrackingsystem/AppApplication.java`
- **Application Configuration**: `src/main/resources/application.properties`
- **Templates**: `src/main/resources/templates/bugs.html`
- **Tests**: `src/test/java/com/example/bugtrackingsystem/AppApplicationTests.java`

## Build and Run Instructions

1. Clone the repository

2. Build the project using Gradle:
   ```bash
   ./gradlew build
   ```

3. Run the application:
   ```bash
   ./gradlew bootRun
   ```

4. Access the application:
   - Default URL: `http://localhost:8080`
   - Modify `application.properties` in `/src/main/resources` to change application configurations if necessary.

## Testing the Application

To execute the test cases:
```bash
./gradlew test
```

Test files are located in:
- `/src/test/java/com/example/bugtrackingsystem`

## Contribution Guidelines

- All code contributions should adhere to the project’s style and conventions.
- Submit changes via pull requests, ensuring all tests pass locally.
- Refer to `HELP.md` for additional guidelines.

## License





