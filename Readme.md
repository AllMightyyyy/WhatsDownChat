
# WhatsDown

WhatsDown is a feature-rich real-time chat application inspired by WhatsApp, designed to facilitate seamless and secure communication among users. Leveraging a robust Java Spring Boot backend and a dynamic React frontend, WhatsDown offers functionalities such as user authentication (including OAuth2), real-time messaging via WebSockets, message attachments, emoji support, and role-based access control. Whether you're looking to chat one-on-one or create group conversations, WhatsDown provides an intuitive and responsive interface for all your communication needs.

## Table of Contents

- [Features](#features)
- [Technologies Used](#technologies-used)
- [Architecture](#architecture)
- [Getting Started](#getting-started)
    - [Prerequisites](#prerequisites)
    - [Backend Setup](#backend-setup)
    - [Frontend Setup](#frontend-setup)
- [Configuration](#configuration)
- [Usage](#usage)
- [Troubleshooting](#troubleshooting)
- [Deployment](#deployment)
- [Contributing](#contributing)
- [License](#license)
- [Acknowledgments](#acknowledgments)

## Features

- **User Authentication & Authorization**
    - **JWT Authentication:** Secure user login with JSON Web Tokens.
    - **OAuth2 Integration:** Login via Google and Facebook for enhanced convenience.
- **Real-Time Messaging**
    - **WebSockets (STOMP):** Instant message delivery and reception.
    - **One-on-One & Group Chats:** Flexible communication channels.
- **Message Attachments**
    - **Image & Video Support:** Share media files effortlessly.
- **Emoji Support**
    - **Emoji Picker:** Easily insert emojis into your messages.
- **User Roles & Permissions**
    - **Role-Based Access Control:** Manage user capabilities within the app.
- **Responsive Design**
    - **Cross-Device Compatibility:** Optimized for desktop and mobile experiences.
- **Security**
    - **Password Encryption:** BCrypt for secure password storage.
    - **CORS Configuration:** Controlled cross-origin requests.

## Technologies Used

### Backend

- **Java 17**
- **Spring Boot**
- **Spring Security** (JWT & OAuth2)
- **Spring Data JPA**
- **Hibernate**
- **MySQL**
- **WebSockets** (STOMP protocol)
- **MapStruct** (DTO Mapping)
- **Lombok** (Boilerplate Reduction)
- **Maven** (Build Automation)

### Frontend

- **React**
- **TypeScript**
- **Redux** (State Management)
- **Material-UI** (UI Components)
- **Axios** (HTTP Client)
- **SockJS & StompJS** (WebSocket Communication)
- **Emoji Picker Libraries**
- **React Router** (Routing)
- **ESLint & Prettier** (Code Quality)

## Architecture

WhatsDown follows a client-server architecture, where the backend handles data management, business logic, and security, while the frontend manages user interactions and displays information. Real-time communication is facilitated through WebSockets, ensuring instant message delivery and updates.

![Architecture Diagram](docs/architecture-diagram.png) <!-- Replace with actual path to diagram -->


## Getting Started

Follow these instructions to set up WhatsDown on your local machine for development and testing purposes.

### Prerequisites

Ensure you have the following installed:

- **Java 17 JDK**: [Download](https://www.oracle.com/java/technologies/javase-jdk17-downloads.html)
- **Maven**: [Download](https://maven.apache.org/download.cgi)
- **Node.js & npm**: [Download](https://nodejs.org/)
- **MySQL**: [Download](https://www.mysql.com/downloads/)
- **Git**: [Download](https://git-scm.com/downloads)

### Backend Setup

1. **Clone the Repository**

   ```bash
   git clone https://github.com/yourusername/whatsdown.git
   cd whatsdown/backend
   ```

2. **Configure the Database**

    - **Create MySQL Database:**

      ```sql
      CREATE DATABASE whatsdown;
      ```

    - **Update `application.properties`:**

      Navigate to `backend/src/main/resources/application.properties` and update the database credentials:

      ```properties
      spring.datasource.url=jdbc:mysql://localhost:3306/whatsdown
      spring.datasource.username=your_mysql_username
      spring.datasource.password=your_mysql_password
      spring.jpa.hibernate.ddl-auto=update
      spring.jpa.show-sql=true
      spring.jpa.database-platform=org.hibernate.dialect.MySQLDialect
      ```

3. **Configure JWT and OAuth2**

    - **Set JWT Secret & Expiration:**

      ```properties
      # JWT Configuration
      app.jwtSecret=your_jwt_secret_key
      app.jwtExpirationMs=86400000
      app.jwtRefreshExpirationMs=3600000
      ```

      *(Ensure `app.jwtSecret` is a secure, randomly generated string.)*

    - **OAuth2 Client Credentials:**

      ```properties
      # OAuth2 Client Configuration
 
      # Google
      spring.security.oauth2.client.registration.google.client-id=your_google_client_id
      spring.security.oauth2.client.registration.google.client-secret=your_google_client_secret
      spring.security.oauth2.client.registration.google.scope=openid, profile, email
 
      # Redirect URIs
      spring.security.oauth2.client.registration.google.redirect-uri={baseUrl}/login/oauth2/code/{registrationId}
      ```

      *(Replace with your actual OAuth2 credentials.)*

### Frontend Setup

1. **Navigate to the Frontend Directory**

   ```bash
   cd ../frontend
   ```

2. **Install Dependencies**

   Install all required packages by running:

   ```bash
   npm install
   ```

3. **Configure Environment Variables**

   In the `frontend` directory, create an `.env` file for environment-specific variables:

   ```env
   REACT_APP_BACKEND_URL=http://localhost:8081
   ```

   *(Adjust `REACT_APP_BACKEND_URL` if your backend is running on a different URL or port).*

4. **Start the Frontend**

   ```bash
   npm start
   ```

   The frontend should now be running on `http://localhost:3000` by default.

---

## Configuration

To customize WhatsDown, refer to the following configuration files:

- **Backend Configuration:** `backend/src/main/resources/application.properties`
- **Frontend Environment Variables:** `frontend/.env`

Adjust these files to match your local environment and specific preferences.

## Usage

- **Register a New Account** or **Login with Google/Facebook** (if enabled).
- **Create Chats** by selecting users or groups.
- **Send Messages** with text, emojis, or attachments (images/videos).
- **Manage Chats** through role-based controls if you're an admin or group owner.
- **Real-Time Communication** ensures all messages update instantly.

## Troubleshooting

- **Database Connection Issues:** Ensure MySQL is running and the `application.properties` file has correct credentials.
- **Port Conflicts:** Change ports in `application.properties` (backend) or `.env` (frontend) if required.
- **CORS Issues:** Check CORS configuration in `backend/src/main/java/org/example/whatsdownbackend/config/WebSecurityConfig.java`.

## Deployment

To deploy WhatsDown, follow these general steps:

### Backend Deployment

1. **Build the Backend**

   ```bash
   mvn clean install
   ```

2. **Deploy to Server** (e.g., using Tomcat, Docker, or any cloud service like AWS or Heroku).

### Frontend Deployment

1. **Build the Frontend**

   ```bash
   npm run build
   ```

2. **Serve the Production Build** (e.g., via NGINX, Apache, or cloud hosting services).

## Contributing

Contributions are welcome! Please fork this repository, create a feature branch, and submit a pull request for review.

## License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.

## Acknowledgments

A big thank you to me, me, and me for making this project possible!

```