# AI Government Schemes Assistant & Eligibility Checker

An intelligent, AI-powered web application built with Spring Boot and modern Web technologies to assist citizens in finding, evaluating eligibility for, and answering questions about government welfare schemes across India.

---

## 🌟 Key Features

- **Scheme Eligibility Checker**: Interactively check eligibility for various central and state schemes (PMAY, PM-Kisan, Sukanya Samriddhi Yojana, etc.) based on age, income, occupation, category, and gender.
- **AI RAG Assistant & Chatbot**: Interactive AI chatbot leveraging Spring AI and Retrieval-Augmented Generation (RAG) with Apache PDFBox to read government policy PDFs and answer citizen queries with source page citations.
- **Multilingual Support**: Supports English and Hindi translations for broader citizen accessibility.
- **User & Admin Dashboards**:
  - **Citizen Dashboard**: Access scheme lists, save favorites, check eligibility, and chat with the AI assistant.
  - **Admin Portal**: Add/edit/delete government schemes, upload official policy PDFs, and view user feedback.
- **JWT & Role-Based Authentication**: Secure authentication system with Spring Security and JWT tokens.

---

## 🛠️ Technology Stack

### Backend
- **Framework**: Spring Boot 3.3.2 (Java 17)
- **AI Integration**: Spring AI (OpenAI / Gemini API compatible)
- **Database**: H2 In-Memory Database (zero-install) / MySQL compatible
- **Security**: Spring Security + JJWT (JSON Web Token)
- **PDF Processing**: Apache PDFBox 3.0.2

### Frontend
- **Templating & Views**: Thymeleaf, HTML5, CSS3 (Custom Responsive Styling), Vanilla JavaScript (ES6+)
- **Integrated Server**: Embedded Tomcat serving dynamic templates and static assets seamlessly from the same codebase.

---

## 📋 Prerequisites

Before running the application, ensure you have the following installed on your system:

- **Java Development Kit (JDK)**: Version 17 or higher
- **Apache Maven**: Version 3.8.x or higher (or use bundled Maven executable)

---

## ⚙️ Environment Setup & Configuration

The application works out-of-the-box using built-in defaults. Optionally, you can configure an OpenAI or Gemini API key for full AI vector embeddings:

### Environment Variables (Optional)

| Variable | Description | Default |
| --- | --- | --- |
| `OPENAI_API_KEY` | OpenAI or Gemini API Key | `dummy-key-to-allow-startup` |
| `OPENAI_BASE_URL` | Base URL for OpenAI/Gemini API | `https://api.openai.com` |
| `OPENAI_MODEL` | AI Model identifier | `gpt-4o-mini` |

*Note: If no API key is provided, the application automatically uses its internal local keyword and PDF text fallback search engine.*

---

## 🚀 How to Run the Application

### 1. Clone the Repository

```bash
git clone https://github.com/Sachin95999/Govt-Scheme-Checker-with-Ai-Bot.git
cd Govt-Scheme-Checker-with-Ai-Bot
```

### 2. Build the Project

```bash
mvn clean package -DskipTests
```

### 3. Run the Backend & Frontend (Combined Web App)

```bash
mvn spring-boot:run
```

Once started, open your web browser and navigate to:
**`http://localhost:8080`**

---

## 🔑 Default Login Credentials

The database automatically seeds default demo accounts upon initial startup:

- **Citizen User**:
  - **Username**: `user`
  - **Password**: `user123`
- **Administrator**:
  - **Username**: `admin`
  - **Password**: `admin123`

---

## 📂 Project Structure

```text
Govt-Scheme-Checker-with-Ai-Bot/
├── pom.xml                                 # Maven dependencies & build configuration
├── README.md                               # Project documentation
├── .gitignore                              # Git ignore rules
└── src/
    └── main/
        ├── java/com/assistant/scheme/      # Backend Java source files
        │   ├── config/                     # Security, Web MVC, and Database Seeder configs
        │   ├── controller/                 # REST & View Controllers (Auth, Chat, Admin, Schemes)
        │   ├── dto/                        # Data Transfer Objects
        │   ├── model/                      # JPA Entities (User, Scheme, UploadedDocument, etc.)
        │   ├── repository/                 # Spring Data JPA Repositories
        │   └── service/                    # Business Logic, Chat & RAG Document Services
        └── resources/
            ├── application.yml             # Application configuration
            ├── static/                     # Frontend static assets (styles.css, app.js)
            └── templates/                  # Frontend HTML views (index, dashboard, eligibility, admin)
```

---

## 📜 License

This project is open-source and available under the [MIT License](LICENSE).
