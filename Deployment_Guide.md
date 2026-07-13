# Deployment Guide  
## TechMart Enterprise E-Commerce System

---

# 1. Introduction

TechMart is a Java EE enterprise e-commerce system built using:

- JAX-RS RESTful API layer
- EJB (Stateless, Singleton, Message Driven Beans)
- JPA (Hibernate persistence)
- JMS messaging with ActiveMQ integration
- Payara Server 6.2025.11

The system supports asynchronous processing for inventory and checkout operations using JMS queues and topics.

---

# 2. System Requirements

| Component | Version |
|----------|---------|
| Java JDK | 17.0.19 |
| Payara Server | 6.2025.11 |
| MySQL Server | 8.0.39 |
| Database | techmart_db |
| ActiveMQ Broker | 6.2.x |
| MySQL Connector/J | Installed in Payara |

---

# 3. Project Architecture

```
techmart-core   → Entities, DTOs, JPA persistence
techmart-ejb    → Business logic + JMS producers/consumers
techmart-web    → JAX-RS REST API layer
techmart-ear    → EAR deployment package
```

---

# 4. REST API Configuration

The REST layer is implemented using JAX-RS.

```java
@ApplicationPath("/api")
public class TechMartRest extends Application {
}
```

### Base API URL

```
http://localhost:8080/techmart/api/
```

### Example endpoints

- /api/users
- /api/admin
- /api/advanced-search
- /api/products
- /api/carts
- /api/orders
- /api/checkouts

---

# 5. Database Configuration

## Database Name

```
techmart_db
```

## JDBC Resource

- JNDI Name: `jdbc/TechMartPool`
- Pool Name: `TechMartPool`

The system uses Payara JDBC connection pooling for efficient database access.

---

# 6. JDBC Connection Pool

| Property | Value |
|----------|-------|
| Pool Name | TechMartPool |
| Resource Type | javax.sql.DataSource |
| Datasource Class | com.mysql.cj.jdbc.MysqlDataSource |
| Max Pool Size | 100 |
| Min Pool Size | 8 |
| Idle Timeout | 300s |

---

# 7. JMS + ActiveMQ Configuration

TechMart uses JMS for asynchronous processing.

## 7.1 Topic Publisher

```java
@Resource(lookup = "jms/TechmartConnectionFactory")
private ConnectionFactory connectionFactory;

@Resource(lookup = "jms/techmartTopic")
private Topic topic;
```

Used for:
- Inventory broadcast updates
- Real-time stock notifications

---

## 7.2 Queue Producer

```java
@Resource(lookup = "jms/myQueueConnectionFactory")
private ConnectionFactory connectionFactory;

@Resource(lookup = "jms/inventoryQueue")
private Queue queue;
```

Used for:
- Inventory processing tasks
- Order-related background processing

---

## 7.3 Message Driven Beans

### Topic Listener

```java
@MessageDriven(activationConfig = {
    @ActivationConfigProperty(
        propertyName = "destinationLookup",
        propertyValue = "jms/techmartTopic"
    ),
    @ActivationConfigProperty(
        propertyName = "destinationType",
        propertyValue = "jakarta.jms.Topic"
    )
})
public class InventoryTopicListener implements MessageListener {
}
```

---

### Queue Listener

```java
@MessageDriven(
    activationConfig = {
        @ActivationConfigProperty(
            propertyName = "destinationLookup",
            propertyValue = "jms/inventoryQueue"
        ),
        @ActivationConfigProperty(
            propertyName = "destinationType",
            propertyValue = "jakarta.jms.Queue"
        )
    }
)
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class InventoryMDB implements MessageListener {
}
```

---

## 7.4 ActiveMQ Checkout Consumer

```java
@Singleton
@Startup
public class ActiveMQCheckoutConsumer {

    private static final String BROKER_URL = "tcp://localhost:61616";
    private static final String QUEUE_NAME = "checkoutQueue";

    @EJB
    private OrderService orderService;
}
```

Used for:
- Asynchronous checkout processing
- Order finalization workflow

---

# 8. Application Configuration Properties

## app.properties

```
app.url=http://localhost:8080/techmart
app.public.url=https://<ngrok-url>/techmart
```

Used for:
- Local development access
- Public access via ngrok tunneling

---

# 9. File Upload Configuration

Product images are stored in the server filesystem:

```java
public static final String UPLOAD_DIR =
        System.getProperty("user.home") + "/techmart-uploads";
```

### Notes:
- Images are saved outside the application server
- This ensures persistence across deployments
- Product images are accessed via this directory

---

# 10. Deployment Steps

## Step 1: Start Services
- Start MySQL Server
- Start ActiveMQ Broker (`tcp://localhost:61616`)
- Start Payara Server

---

## Step 2: Configure Database
- Create database: `techmart_db`
- Import the SQL initialization scripts located in the root database directory:
  ```text
  /database/techmart_db_schema.sql

---

## Step 3: Configure Payara
- JDBC Pool: `TechMartPool`
- JNDI Resource: `jdbc/TechMartPool`
- MySQL driver installed in `glassfish/lib`

---

## Step 4: Build Project

```bash
mvn clean install
```

---

## Step 5: Deploy EAR

```bash
asadmin deploy techmart-ear.ear
```

---

# 11. Application Access

### REST API Base URL

```
http://localhost:8080/techmart/api/
```

### Public URL (ngrok)

```
https://xxxxx.ngrok-free.app/techmart/api/
```

---

# 12. Default Login Credentials

For testing and demonstration purposes, the system includes a default administrator account.

## Admin Dashboard Access

| Role | Username | Password |
|------|----------|----------|
| Admin | admin | admin |

---

## Notes

- This account is used to access the Admin Dashboard.
- The admin panel provides access to:
  - Product management
  - Inventory management
  - Order management


# 13. Troubleshooting

## Database Issues
- Verify MySQL is running
- Check JDBC pool ping status

## JMS Issues
- Ensure ActiveMQ is running on port 61616
- Verify destination names in Payara

## Deployment Issues
- Check server.log in Payara domain

---

# 14. Summary

TechMart is a full enterprise Java EE system integrating:

- RESTful JAX-RS APIs
- EJB business logic layer
- JMS asynchronous messaging with ActiveMQ
- JPA persistence with MySQL
- JDBC connection pooling via Payara

This architecture ensures scalability, performance, and loose coupling between system components.