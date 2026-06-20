# React + Vite


# CodeCollab - Real-Time Code Collaboration Platform

## Overview

CodeCollab is a real-time code collaboration platform that enables multiple users to write, edit, and collaborate on code simultaneously within a shared room. The platform is designed to help developers, students, and teams collaborate efficiently from different locations through live code synchronization.

The application is built using React for the frontend, Spring Boot for the backend, and MySQL for data management. Real-time communication is achieved using WebSocket technology, allowing instant updates across all connected users.

## Features

* Create and join coding rooms using a unique Room ID
* Real-time code synchronization between multiple users
* Multi-user collaboration
* Responsive and user-friendly interface
* Fast and reliable WebSocket communication
* Secure backend APIs using Spring Boot
* Database integration with MySQL
* Scalable architecture for future enhancements

## Tech Stack

### Frontend

* React.js
* HTML5
* CSS3
* JavaScript

### Backend

* Spring Boot
* Java
* Maven
* WebSocket

### Database

* MySQL

## Project Architecture

### Frontend

* User Interface
* Room Management
* Code Editor Integration
* WebSocket Client

### Backend

* REST Controllers
* WebSocket Server
* Service Layer
* Repository Layer
* Database Management

### Database

* User Information
* Room Details
* Session Management

## How It Works

1. A user creates a room and receives a unique Room ID.
2. Other users join the room using the Room ID.
3. Users can write and edit code collaboratively.
4. Every code update is sent to the server through WebSocket.
5. The server broadcasts the changes to all connected users instantly.
6. All participants see the updated code in real time.

## Installation and Setup

### Backend Setup

1. Configure MySQL database.
2. Update application.properties with database credentials.
3. Run the Spring Boot application.

```bash
mvn spring-boot:run
```

### Frontend Setup

```bash
npm install
npm start
```

## Future Enhancements

* Authentication and Authorization
* Video and Voice Chat
* Multi-language Code Execution
* Code Version History
* AI-Powered Code Suggestions
* Cloud Deployment

## Author

Kanayya Mahindrakar

## Project Goal

The goal of CodeCollab is to provide a seamless real-time coding experience that improves teamwork, remote learning, technical interviews, pair programming, and collaborative software development.
