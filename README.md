# Playwright Java Automation Framework with TestNG

Enterprise-grade test automation framework built using **Playwright for Java** and **TestNG** for end-to-end testing of the OrangeHRM demo platform, featuring continuous integration via Jenkins.

---

## 🚀 Tech Stack

- **Language:** Java 21
- **Automation Tool:** [Microsoft Playwright for Java](https://playwright.dev/java/) (v1.61.0)
- **Test Runner:** [TestNG](https://testng.org/) (v7.11.0)
- **Build Tool:** Apache Maven
- **Test Execution Plugin:** Maven Surefire Plugin (v3.5.3)
- **CI/CD:** Jenkins Declarative Pipeline with GitHub Webhook triggers

---

## 📁 Project Structure

```text
Playwright-Java/
├── src/
│   ├── main/java/           # Page objects, utility classes, and base setup
│   └── test/java/           # TestNG test classes
│       └── com/neel/playwright/tests/
│           ├── LoginTest.java
│           ├── LogoutTest.java
│           ├── CreateBuzzPostTest.java
│           ├── DeleteBuzzPostTest.java
│           └── PIMEmployeeTest.java
├── test_data/               # Dynamic test data files
├── Jenkinsfile              # Declarative CI/CD pipeline definition
├── pom.xml                  # Maven dependencies and Surefire configuration
├── testng.xml               # TestNG test suite configuration
└── README.md                # Project documentation
```

---

## 🧪 Test Scenarios

The suite automates the core workflows of OrangeHRM:

1. **Login Test (`LoginTest`):** Authenticates user credentials and asserts dashboard visibility.
2. **Logout Test (`LogoutTest`):** Verifies user profile dropdown logout and session termination.
3. **Buzz Post Creation (`CreateBuzzPostTest`):** Publishes dynamic newsfeed posts to the Buzz module.
4. **Buzz Post Deletion (`DeleteBuzzPostTest`):** Locates and deletes the created Buzz post.
5. **PIM Employee Lifecycle (`PIMEmployeeTest`):** Adds a new employee with personal details, login credentials, and verifies record creation and cleanup.

---

## ⚙️ Prerequisites

- **Java JDK 21+** installed and configured in `JAVA_HOME`.
- **Apache Maven 3.8+** installed and available in `PATH`.
- **Git** for version control.

---

## 🛠️ Local Execution

### 1. Clone the repository
```bash
git clone https://github.com/neelgaganlikki-pixel/PlaywrightMCP-with-Java---TestNG.git
cd PlaywrightMCP-with-Java---TestNG
```

### 2. Run the full TestNG test suite via Maven
```bash
mvn clean test
```

### 3. Run a specific TestNG XML suite
```bash
mvn test -Dsurefire.suiteXmlFiles=testng.xml
```

---

## 📊 Test Reports

After test execution, reports are generated in the `target/` directory:
- **Surefire Reports:** `target/surefire-reports/index.html`
- **Surefire XMLs:** `target/surefire-reports/testng-results.xml`

---

## 🔄 CI/CD with Jenkins

This repository is integrated with Jenkins:
- Automatically triggered on code pushes to the `main` branch via GitHub Webhooks.
- Executes tests in headless mode and captures execution metrics.
- Publishes JUnit / TestNG test results and sends automated build notifications.

