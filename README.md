# TechMart E-Commerce

TechMart is a robust, multi-module Java EE (Jakarta EE) enterprise e-commerce application designed with an asynchronous architecture.

The system leverages **JAX-RS** for RESTful APIs, **EJB** for core business logic, **JPA (Hibernate)** for database persistence, and **JMS (ActiveMQ)** for decoupled message processing.

---

## 🏗️ Project Architecture

* **techmart-core**: Contains domain entities, DTOs, and JPA persistence layout.
* **techmart-ejb**: Enterprise Java Beans handling core business logic and JMS producers/consumers.
* **techmart-web**: Presentation layer exposing the JAX-RS REST API endpoints.
* **techmart-ear**: Enterprise Archive module that packages all sub-modules for server deployment.

---

## 📖 Setup & Deployment Documentation

To get the application up and running locally, please refer to our detailed step-by-step guides:

* 🚀 **[System Deployment Guide](./Deployment_Guide.md)** — Learn how to build the project, set up the ActiveMQ broker, deploy the `.ear` file to Payara Server 6, and access the REST API endpoints.
* 🗄️ **[Database & JDBC Configuration Guide](./Database_and_JDBC_Configuration_Guide.md)** — Instructions on setting up the MySQL database, configuring the `TechMartPool` JDBC connection pool, installing the MySQL Connector/J driver, and adjusting Payara JNDI settings.

---

## ⚙️ Core Configuration Summary

* **App Server:** Payara Server 6.2025.11 (JDK 17)
* **Database JNDI:** `jdbc/TechMartPool` (MySQL 8.0.39)
* **JMS Broker:** ActiveMQ (`tcp://localhost:61616`)
* **REST Base URL:** `http://localhost:8080/techmart/api/`

---

## 🔐 Default Testing Credentials

For local development and testing evaluation:

| Role | Username | Password |
|------|----------|----------|
| Admin | `admin` | `admin` |