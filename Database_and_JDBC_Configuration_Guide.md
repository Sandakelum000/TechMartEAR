# Database and JDBC Configuration Guide

## Project Information

| Item | Value |
|------|------|
| Project | TechMart Enterprise E-Commerce System |
| Application Server | Payara Server 6.2025.11 |
| Java Development Kit | JDK 17.0.19 |
| Database Server | MySQL Community Server 8.0.39 |
| Persistence Provider | Hibernate ORM (JPA) |
| Database Connectivity | JDBC Connection Pool |

---

# 1. Overview

The TechMart application uses a managed JDBC Connection Pool provided by Payara Server to establish and manage database connections efficiently. Connection pooling minimizes the overhead of repeatedly creating database connections, resulting in improved application performance and scalability.

---

# 2. Software Requirements

| Software | Version |
|----------|---------|
| Payara Server | 6.2025.11 |
| Java Development Kit | 17.0.19 |
| MySQL Server | 8.0.39 |
| MySQL Connector/J | Installed in Payara Server |

---

# 3. MySQL JDBC Driver Installation

Payara Server does not include the MySQL JDBC driver by default. Therefore, the MySQL Connector/J driver was manually installed.

## Installation Location

```
PAYARA_HOME/glassfish/domains/domain1/lib/
```

The MySQL Connector/J JAR file was copied into the above directory and the Payara domain was restarted to load the driver successfully.

This configuration enables Payara Server to communicate with the MySQL database using the MySQL JDBC driver.

---

# 4. JDBC Connection Pool Configuration

A JDBC Connection Pool named **TechMartPool** was created using the Payara Administration Console.

## General Settings

| Property | Value |
|----------|-------|
| Pool Name | TechMartPool |
| Resource Type | javax.sql.DataSource |
| Datasource Class | com.mysql.cj.jdbc.MysqlDataSource |
| Ping | Enabled |
| Deployment Order | 100 |

---

## Pool Configuration

| Property | Value |
|----------|-------|
| Initial Pool Size | 8 |
| Minimum Pool Size | 8 |
| Maximum Pool Size | 100 |
| Pool Resize Quantity | 2 |
| Idle Timeout | 300 Seconds |
| Maximum Wait Time | 60000 Milliseconds |

---

# 5. JDBC Resource Configuration

A JDBC Resource was created in Payara Server to allow the application to access the configured connection pool.

| Property | Value |
|----------|-------|
| JNDI Name | jdbc/TechMartPool |
| Connection Pool | TechMartPool |
| Status | Enabled |
| Deployment Order | 100 |

The application references this JNDI resource within the JPA persistence configuration (`persistence.xml`) to obtain managed database connections.

---

# 6. Additional Connection Properties

The following vendor-specific properties were configured for the MySQL datasource.

| Property | Value |
|----------|-------|
| allowPublicKeyRetrieval | true |
| useSSL | false |

### Purpose of Additional Properties

**allowPublicKeyRetrieval=true**

Allows the MySQL Connector/J driver to retrieve the server's RSA public key when authenticating with MySQL 8.x. This resolves authentication issues that may occur when connecting to newer MySQL server versions.

**useSSL=false**

Disables SSL encryption for local development. Since the TechMart application is deployed and tested in a local development environment, encrypted database communication is not required.

---

# 7. Persistence Configuration

The application's JPA persistence configuration is located at:

techmart-core/src/main/resources/META-INF/persistence.xml

The persistence unit references the following managed JDBC resource:

```
jdbc/TechMartPool
```

Payara Server manages all database connections through the configured JDBC Connection Pool.

# 8. Connection Verification

The connection pool configuration was verified using the **Ping** feature available in the Payara Administration Console.

A successful ping confirms:

- Correct database credentials
- Successful communication with the MySQL server
- Proper installation of the MySQL JDBC driver
- Valid connection pool configuration

---

# 9. Summary

The TechMart Enterprise E-Commerce System utilizes Payara Server's managed JDBC Connection Pool together with MySQL Connector/J to provide efficient and reliable database connectivity.

By using connection pooling, the application minimizes database connection overhead, improves performance under concurrent workloads, and supports scalable enterprise application deployment.