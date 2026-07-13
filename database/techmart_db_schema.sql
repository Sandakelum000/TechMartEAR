CREATE DATABASE  IF NOT EXISTS `techmart_db` /*!40100 DEFAULT CHARACTER SET utf8mb3 */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `techmart_db`;
-- MySQL dump 10.13  Distrib 8.0.43, for macos15 (arm64)
--
-- Host: 127.0.0.1    Database: techmart_db
-- ------------------------------------------------------
-- Server version	8.0.39

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `address`
--

DROP TABLE IF EXISTS `address`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `address` (
  `id` int NOT NULL AUTO_INCREMENT,
  `line_one` varchar(45) NOT NULL,
  `line_two` varchar(45) DEFAULT NULL,
  `postal_code` varchar(10) DEFAULT NULL,
  `is_primary` tinyint NOT NULL,
  `mobile` varchar(10) NOT NULL,
  `users_id` int NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_address_users1_idx` (`users_id`),
  CONSTRAINT `fk_address_users1` FOREIGN KEY (`users_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=142 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `address`
--

LOCK TABLES `address` WRITE;
/*!40000 ALTER TABLE `address` DISABLE KEYS */;
INSERT INTO `address` VALUES (1,'No 12','Colombo','11618',0,'0759278656',25),(13,'No 12','Colombo','11618',0,'0759278656',25),(14,'No 12','Colombo','11618',0,'0759278656',25),(15,'No 12','Colombo','11618',0,'0759278656',25),(16,'No 12','Colombo','11618',0,'0759278656',25),(17,'No 12','Colombo','11618',0,'0759278656',25),(18,'No 12','Colombo','11618',0,'0759278656',25),(19,'No 12','Colombo','11618',0,'0759278656',25),(20,'No 12','Colombo','11618',0,'0759278656',25),(21,'No 12','Colombo','11618',0,'0759278656',25),(22,'No 12','Colombo','11618',0,'0759278656',25),(23,'No 12','Colombo','11618',0,'0759278656',25),(24,'No 12','Colombo','11618',0,'0759278656',25),(25,'No 12','Colombo','11618',0,'0759278656',25),(26,'No 12','Colombo','11618',0,'0759278656',25),(27,'No 12','Colombo','11618',0,'0759278656',25),(28,'No 12','Colombo','11618',0,'0759278656',25),(29,'No 12','Colombo','11618',0,'0759278656',25),(30,'No 12','Colombo','11618',0,'0759278656',25),(31,'No 12','Colombo','11618',0,'0759278656',25),(32,'No 12','Colombo','11618',0,'0759278656',25),(33,'No 12','Colombo','11618',0,'0759278656',25),(34,'No 12','Colombo','11618',0,'0759278656',25),(35,'No 12','Colombo','11618',0,'0759278656',25),(36,'No 12','Colombo','11618',0,'0759278656',25),(37,'No 12','Colombo','11618',0,'0759278656',25),(38,'No 12','Colombo','11618',0,'0759278656',25),(39,'No 12','Colombo','11618',0,'0759278656',25),(40,'No 12','Colombo','11618',0,'0759278656',25),(41,'No 12','Colombo','11618',0,'0759278656',25),(42,'No 12','Colombo','11618',0,'0759278656',25),(43,'No 12','Colombo','11618',0,'0759278656',25),(44,'No 12','Colombo','11618',0,'0759278656',25),(45,'No 12','Colombo','11618',0,'0759278656',25),(46,'No 12','Colombo','11618',0,'0759278656',25),(47,'No 12','Colombo','11618',0,'0759278656',25),(48,'No 12','Colombo','11618',0,'0759278656',25),(49,'No 12','Colombo','11618',0,'0759278656',25),(50,'No 12','Colombo','11618',0,'0759278656',25),(51,'No 12','Colombo','11618',0,'0759278656',25),(52,'No 12','Colombo','11618',0,'0759278656',25),(53,'No 12','Colombo','11618',0,'0759278656',25),(54,'No 12','Colombo','11618',0,'0759278656',25),(55,'No 12','Colombo','11618',0,'0759278656',25),(56,'No 12','Colombo','11618',0,'0759278656',25),(57,'No 12','Colombo','11618',0,'0759278656',25),(58,'No 12','Colombo','11618',0,'0759278656',25),(59,'No 12','Colombo','11618',0,'0759278656',25),(60,'No 12','Colombo','11618',0,'0759278656',25),(61,'No 12','Colombo','11618',0,'0759278656',25),(62,'No 12','Colombo','11618',0,'0759278656',25),(63,'No 12','Colombo','11618',0,'0759278656',25),(64,'No 12','Colombo','11618',0,'0759278656',25),(65,'No 12','Colombo','11618',0,'0759278656',25),(66,'No 12','Colombo','11618',0,'0759278656',25),(67,'No 12','Colombo','11618',0,'0759278656',25),(68,'No 12','Colombo','11618',0,'0759278656',25),(69,'No 12','Colombo','11618',0,'0759278656',25),(70,'No 12','Colombo','11618',0,'0759278656',25),(71,'No 12','Colombo','11618',0,'0759278656',25),(72,'No 12','Colombo','11618',0,'0759278656',25),(73,'No 12','Colombo','11618',0,'0759278656',25),(74,'No 12','Colombo','11618',0,'0759278656',25),(75,'No 12','Colombo','11618',0,'0759278656',25),(76,'No 12','Colombo','11618',0,'0759278656',25),(77,'No 12','Colombo','11618',0,'0759278656',25),(78,'No 12','Colombo','11618',0,'0759278656',25),(79,'No 12','Colombo','11618',0,'0759278656',25),(80,'No 12','Colombo','11618',0,'0759278656',25),(81,'No 12','Colombo','11618',0,'0759278656',25),(82,'No 12','Colombo','11618',0,'0759278656',25),(83,'No 12','Colombo','11618',0,'0759278656',25),(84,'No 12','Colombo','11618',0,'0759278656',25),(85,'No 12','Colombo','11618',0,'0759278656',25),(86,'No 12','Colombo','11618',0,'0759278656',25),(87,'No 12','Colombo','11618',0,'0759278656',25),(88,'No 12','Colombo','11618',0,'0759278656',25),(89,'No 12','Colombo','11618',0,'0759278656',25),(90,'No 12','Colombo','11618',0,'0759278656',25),(91,'No 12','Colombo','11618',0,'0759278656',25),(92,'No 12','Colombo','11618',0,'0759278656',25),(93,'No 12','Colombo','11618',0,'0759278656',25),(94,'No 12','Colombo','11618',0,'0759278656',25),(95,'No 12','Colombo','11618',0,'0759278656',25),(96,'No 12','Colombo','11618',0,'0759278656',25),(97,'No 12','Colombo','11618',0,'0759278656',25),(98,'No 12','Colombo','11618',0,'0759278656',25),(99,'No 12','Colombo','11618',0,'0759278656',25),(100,'No 12','Colombo','11618',0,'0759278656',25),(101,'No 12','Colombo','11618',0,'0759278656',25),(102,'No 12','Colombo','11618',0,'0759278656',25),(103,'No 12','Colombo','11618',0,'0759278656',25),(104,'No 12','Colombo','11618',0,'0759278656',25),(105,'No 12','Colombo','11618',0,'0759278656',25),(106,'No 12','Colombo','11618',0,'0759278656',25),(107,'No 12','Colombo','11618',0,'0759278656',25),(108,'No 12','Colombo','11618',0,'0759278656',25),(109,'No 12','Colombo','11618',0,'0759278656',25),(110,'No 12','Colombo','11618',0,'0759278656',25),(111,'No 12','Colombo','11618',0,'0759278656',25),(112,'No 12','Colombo','11618',0,'0759278656',25),(113,'No 12','Colombo','11618',0,'0759278656',25),(114,'No 12','Colombo','11618',0,'0759278656',25),(115,'No 12','Colombo','11618',0,'0759278656',25),(116,'No 12','Colombo','11618',0,'0759278656',25),(117,'No 12','Colombo','11618',0,'0759278656',25),(118,'No 12','Colombo','11618',0,'0759278656',25),(119,'No 12','Colombo','11618',0,'0759278656',25),(120,'No 12','Colombo','11618',0,'0759278656',25),(121,'No 12','Colombo','11618',0,'0759278656',25),(122,'No 12','Colombo','11618',0,'0759278656',25),(123,'No 12','Colombo','11618',0,'0759278656',25),(124,'No 12','Colombo','11618',0,'0759278656',25),(125,'No 12','Colombo','11618',0,'0759278656',25),(126,'No 12','Colombo','11618',0,'0759278656',25),(127,'No 12','Colombo','11618',0,'0759278656',25),(128,'No 12','Colombo','11618',0,'0759278656',25),(129,'No 12','Colombo','11618',0,'0759278656',25),(130,'No 12','Colombo','11618',0,'0759278656',25),(131,'No 12','Colombo','11618',0,'0759278656',25),(132,'No 12','Colombo','11618',0,'0759278656',25),(133,'No 12','Colombo','11618',0,'0759278656',25),(134,'No 12','Colombo','11618',0,'0759278656',25),(135,'No 12','Colombo','11618',0,'0759278656',25),(136,'No 12','Colombo','11618',0,'0759278656',25),(137,'No 12','Colombo','11618',0,'0759278656',25),(138,'No 12','Colombo','11618',0,'0759278656',32),(139,'No 12','Colombo','11618',0,'0759278656',32),(140,'biyagama','malwana','11618',1,'0759278656',32),(141,'Kadawatha','2/A ihalabiyanwila','11245',1,'0775683451',34);
/*!40000 ALTER TABLE `address` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `admin`
--

DROP TABLE IF EXISTS `admin`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `admin` (
  `id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(45) NOT NULL,
  `password` varchar(45) NOT NULL,
  `role_id` int NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_admin_role1_idx` (`role_id`),
  CONSTRAINT `fk_admin_role1` FOREIGN KEY (`role_id`) REFERENCES `role` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `admin`
--

LOCK TABLES `admin` WRITE;
/*!40000 ALTER TABLE `admin` DISABLE KEYS */;
INSERT INTO `admin` VALUES (1,'admin','admin',1);
/*!40000 ALTER TABLE `admin` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `brand`
--

DROP TABLE IF EXISTS `brand`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `brand` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(45) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `brand`
--

LOCK TABLES `brand` WRITE;
/*!40000 ALTER TABLE `brand` DISABLE KEYS */;
INSERT INTO `brand` VALUES (1,'Apple'),(2,'Samsung'),(3,'Honor');
/*!40000 ALTER TABLE `brand` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cart`
--

DROP TABLE IF EXISTS `cart`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cart` (
  `id` int NOT NULL AUTO_INCREMENT,
  `qty` double NOT NULL,
  `users_id` int NOT NULL,
  `stock_id` int NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_cart_users1_idx` (`users_id`),
  KEY `fk_cart_stock1_idx` (`stock_id`),
  CONSTRAINT `fk_cart_stock1` FOREIGN KEY (`stock_id`) REFERENCES `stock` (`id`),
  CONSTRAINT `fk_cart_users1` FOREIGN KEY (`users_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=53 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cart`
--

LOCK TABLES `cart` WRITE;
/*!40000 ALTER TABLE `cart` DISABLE KEYS */;
INSERT INTO `cart` VALUES (52,1,34,2);
/*!40000 ALTER TABLE `cart` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `category`
--

DROP TABLE IF EXISTS `category`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `category` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(45) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `category`
--

LOCK TABLES `category` WRITE;
/*!40000 ALTER TABLE `category` DISABLE KEYS */;
INSERT INTO `category` VALUES (1,'Mobile Phones'),(2,'Laptops'),(3,'Accesseries');
/*!40000 ALTER TABLE `category` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `inventory_transaction`
--

DROP TABLE IF EXISTS `inventory_transaction`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `inventory_transaction` (
  `id` int NOT NULL AUTO_INCREMENT,
  `type` varchar(45) NOT NULL,
  `qty` int NOT NULL,
  `price` double NOT NULL,
  `reference` varchar(45) NOT NULL,
  `created_at` datetime NOT NULL,
  `updated_at` datetime NOT NULL,
  `stock_id` int NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_inventory_transaction_stock1_idx` (`stock_id`),
  CONSTRAINT `fk_inventory_transaction_stock1` FOREIGN KEY (`stock_id`) REFERENCES `stock` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=33 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `inventory_transaction`
--

LOCK TABLES `inventory_transaction` WRITE;
/*!40000 ALTER TABLE `inventory_transaction` DISABLE KEYS */;
INSERT INTO `inventory_transaction` VALUES (1,'RECEIVE',50,0,'Supplier shipment','2026-06-25 07:01:14','2026-06-25 07:01:14',1),(2,'RECEIVE',30,0,'Supply batch arrival','2026-06-25 09:57:46','2026-06-25 09:57:46',2),(8,'RECEIVE',20,0,'Initial Stock','2026-06-26 05:18:03','2026-06-26 05:18:03',9),(13,'RECEIVE',40,0,'Initial Stock','2026-06-26 06:25:50','2026-06-26 06:25:50',14),(14,'RECEIVE',12,0,'Initial Stock','2026-06-26 06:31:38','2026-06-26 06:31:38',15),(15,'RECEIVE',12,0,'Initial Stock','2026-06-26 06:36:06','2026-06-26 06:36:06',16),(16,'RECEIVE',12,0,'Initial Stock','2026-06-26 06:40:33','2026-06-26 06:40:33',17),(17,'RECEIVE',12,0,'Initial Stock','2026-06-26 06:49:45','2026-06-26 06:49:45',18),(18,'RECEIVE',12,0,'Initial Stock','2026-06-26 07:00:41','2026-06-26 07:00:41',19),(19,'DAMAGED',10,0,'Due to the Factory issue','2026-06-26 12:06:56','2026-06-26 12:06:56',15),(20,'RECEIVE',50,0,'Supply batch arrival','2026-06-26 12:08:01','2026-06-26 12:08:01',14),(21,'DAMAGED',10,0,'Due to the Factory issue','2026-06-26 12:09:28','2026-06-26 12:09:28',16),(22,'DAMAGED',2,0,'Due to the Factory issue','2026-06-26 12:34:13','2026-06-26 12:34:13',14),(23,'DAMAGED',2,0,'Due to the Factory issue','2026-06-26 12:35:15','2026-06-26 12:35:15',19),(24,'DAMAGED',30,0,'Due to the Factory issue','2026-06-26 12:44:38','2026-06-26 12:44:38',14),(25,'DAMAGED',10,0,'Due to the Factory issue','2026-06-26 12:45:15','2026-06-26 12:45:15',14),(26,'SALE',3,0,'A Sale','2026-06-26 13:07:27','2026-06-26 13:07:27',9),(27,'SALE',4,0,'Due to the Factory issue','2026-06-26 16:51:45','2026-06-26 16:51:45',14),(28,'DAMAGED',1,0,'Due to the Factory issue','2026-06-26 16:52:45','2026-06-26 16:52:45',14),(29,'RECEIVE',10,0,'Supply batch arrival','2026-06-27 04:14:39','2026-06-27 04:14:39',15),(30,'DAMAGED',9,0,'Due to the Factory issue','2026-06-29 14:45:11','2026-06-29 14:45:11',14),(31,'RECEIVE',60,0,'Initial Stock','2026-06-30 08:04:45','2026-06-30 08:04:45',20),(32,'DAMAGED',4,0,'Due to the Factory issue','2026-06-30 15:04:28','2026-06-30 15:04:28',15);
/*!40000 ALTER TABLE `inventory_transaction` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `notification`
--

DROP TABLE IF EXISTS `notification`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `notification` (
  `id` int NOT NULL AUTO_INCREMENT,
  `title` varchar(45) NOT NULL,
  `message` text NOT NULL,
  `status` tinyint NOT NULL,
  `users_id` int NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_notification_users1_idx` (`users_id`),
  CONSTRAINT `fk_notification_users1` FOREIGN KEY (`users_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `notification`
--

LOCK TABLES `notification` WRITE;
/*!40000 ALTER TABLE `notification` DISABLE KEYS */;
/*!40000 ALTER TABLE `notification` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `order_items`
--

DROP TABLE IF EXISTS `order_items`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `order_items` (
  `id` int NOT NULL AUTO_INCREMENT,
  `qty` int NOT NULL,
  `orders_id` bigint NOT NULL,
  `stock_id` int NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_order_items_orders1_idx` (`orders_id`),
  KEY `fk_order_items_stock1_idx` (`stock_id`),
  CONSTRAINT `fk_order_items_orders1` FOREIGN KEY (`orders_id`) REFERENCES `orders` (`id`),
  CONSTRAINT `fk_order_items_stock1` FOREIGN KEY (`stock_id`) REFERENCES `stock` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=132 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `order_items`
--

LOCK TABLES `order_items` WRITE;
/*!40000 ALTER TABLE `order_items` DISABLE KEYS */;
INSERT INTO `order_items` VALUES (125,2,124,2),(126,1,124,1),(127,1,125,2),(128,1,126,1),(129,1,126,3),(130,4,127,1),(131,2,128,1);
/*!40000 ALTER TABLE `order_items` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `order_status`
--

DROP TABLE IF EXISTS `order_status`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `order_status` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(45) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `order_status`
--

LOCK TABLES `order_status` WRITE;
/*!40000 ALTER TABLE `order_status` DISABLE KEYS */;
/*!40000 ALTER TABLE `order_status` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `orders`
--

DROP TABLE IF EXISTS `orders`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `orders` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `status_id` int NOT NULL,
  `users_id` int NOT NULL,
  `created_at` datetime NOT NULL,
  `updated_at` datetime NOT NULL,
  `start_at` bigint DEFAULT NULL,
  `end_at` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_orders_users1_idx` (`users_id`),
  KEY `fk_orders_status1_idx` (`status_id`),
  CONSTRAINT `fk_orders_status1` FOREIGN KEY (`status_id`) REFERENCES `status` (`id`),
  CONSTRAINT `fk_orders_users1` FOREIGN KEY (`users_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=129 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `orders`
--

LOCK TABLES `orders` WRITE;
/*!40000 ALTER TABLE `orders` DISABLE KEYS */;
INSERT INTO `orders` VALUES (124,12,32,'2026-06-30 11:40:52','2026-06-30 11:41:43',1782799852439,1782799903258),(125,12,34,'2026-06-30 13:10:33','2026-06-30 13:12:24',1782805232903,1782805343679),(126,12,32,'2026-06-30 13:11:53','2026-06-30 13:12:23',1782805312972,1782805342597),(127,12,32,'2026-07-04 07:41:21','2026-07-04 07:41:54',1783131080927,1783131113555),(128,12,34,'2026-07-05 09:34:08','2026-07-05 09:34:33',1783224247808,1783224272691);
/*!40000 ALTER TABLE `orders` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `product`
--

DROP TABLE IF EXISTS `product`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `product` (
  `id` int NOT NULL AUTO_INCREMENT,
  `title` varchar(100) NOT NULL,
  `description` text NOT NULL,
  `created_at` datetime DEFAULT NULL,
  `updated_at` datetime DEFAULT NULL,
  `brand_id` int NOT NULL,
  `category_id` int NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_product_brand1_idx` (`brand_id`),
  KEY `fk_product_category1_idx` (`category_id`),
  CONSTRAINT `fk_product_brand1` FOREIGN KEY (`brand_id`) REFERENCES `brand` (`id`),
  CONSTRAINT `fk_product_category1` FOREIGN KEY (`category_id`) REFERENCES `category` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `product`
--

LOCK TABLES `product` WRITE;
/*!40000 ALTER TABLE `product` DISABLE KEYS */;
INSERT INTO `product` VALUES (1,'Apple iPhone 17 Pro 256GB','iPhone 17 Pro Max smartphone delivers exceptional performance thanks to the groundbreaking A19 Pro chip, Apple Intelligence and a professional triple camera system with a 48MP Fusion main camera. Enjoy the visual experience thanks to the beautiful 6.9-inch Super Retina XDR OLED display that can display dazzling images in clear, vivid colors.','2026-06-26 05:18:03','2026-06-26 05:18:03',1,1),(2,'Samsung Galaxy S26 Ultra 1TB','The Samsung Galaxy S26 Ultra in Sri Lanka is available at LKR 329,000.00, updated on 2026-06-19. The Samsung Galaxy S26 Ultra is Samsung’s most advanced flagship smartphone','2026-06-26 05:18:03','2026-06-26 05:18:03',2,1),(3,'Honor X9b 5G 256GB','Introducing the Honor X9b, a cutting-edge smartphone combining performance, style, and affordability. Boasting an impressive 12GB RAM and 256GB storage, this device handles multitasking and storage-intensive tasks effortlessly. The Qualcomm Snapdragon 6 Gen 1 processor','2026-06-26 05:18:03','2026-06-26 05:18:03',3,1),(9,'MacBook Pro 14 inch M5 Chip','The MacBook Pro 14 inch M5 Chip in Sri Lanka is available at LKR 585,000.00, updated on 2026-06-25. The 14-inch MacBook Pro M5 delivers breathtaking speed, Liquid Retina XDR brilliance, AI performance, and up to 18 hours of battery life.. LuxuryX provides genuine Apple products with 1 Year manufacturer warranty, flexible installment options, and free delivery or in-store pickup across Sri Lanka.','2026-06-26 05:18:03','2026-06-26 05:18:03',1,2),(14,'MacBook Air 13 inch M5','MacBook Air 13 inch M5','2026-06-26 06:25:50','2026-06-26 06:25:50',1,2),(15,'Samsung Galaxy A37 (12GB RAM|256GB)','Samsung Galaxy A37 (12GB RAM|256GB) ','2026-06-26 06:31:38','2026-06-26 06:31:38',2,1),(16,'Honor X8d (8GB RAM|256GB)','Honor X8d (8GB RAM|256GB)','2026-06-26 06:36:05','2026-06-26 06:36:05',3,1),(17,'Macbook Pro M1','Macbook Pro M1','2026-06-26 06:40:33','2026-06-26 06:40:33',1,2),(18,'Honor X7d 5G (6GB RAM|128GB)','Honor X7d 5G (6GB RAM|128GB)','2026-06-26 06:49:45','2026-06-26 06:49:45',3,1),(19,'MacBook Pro M2 256GB','MacBook Pro M2 256GB','2026-06-26 07:00:41','2026-06-26 07:01:07',1,2),(20,'AirPods Pro 3','AirPods Pro 3. Up to 2x more Active Noise Cancellation than AirPods Pro 2. Now with heart rate sensing, Live Translation3, longer battery life and advancements in hearing health. Redesigned for a more secure fit.','2026-06-30 08:04:45','2026-06-30 08:06:11',1,3);
/*!40000 ALTER TABLE `product` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `product_images`
--

DROP TABLE IF EXISTS `product_images`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `product_images` (
  `product_id` int NOT NULL,
  `image_path` varchar(255) NOT NULL,
  KEY `fk_product_images_product1_idx` (`product_id`),
  CONSTRAINT `fk_product_images_product1` FOREIGN KEY (`product_id`) REFERENCES `product` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `product_images`
--

LOCK TABLES `product_images` WRITE;
/*!40000 ALTER TABLE `product_images` DISABLE KEYS */;
INSERT INTO `product_images` VALUES (20,'/uploads/products/20/45816e12-6662-40e6-a5e4-52df6b474d27.webp'),(19,'/uploads/products/19/7e15b12e-5efe-4689-aeb8-ddb93d5b606e.webp'),(17,'/uploads/products/17/mac_book_m1_pro.jpg'),(16,'/uploads/products/16/honor_X8d.jpg'),(15,'/uploads/products/15/samsung_Galaxy_A37.jpg'),(14,'/uploads/products/14/mac_book_air_m5.webp'),(18,'/uploads/products/18/honor_X7d.jpg'),(3,'/uploads/products/3/honor_X9b.jpg'),(2,'/uploads/products/2/sam_s26_ultra.webp'),(1,'/uploads/products/1/iphone_17_pro_max.jpg'),(9,'/uploads/products/9/macbook_pro_m5_1.webp');
/*!40000 ALTER TABLE `product_images` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `role`
--

DROP TABLE IF EXISTS `role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `role` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(45) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `role`
--

LOCK TABLES `role` WRITE;
/*!40000 ALTER TABLE `role` DISABLE KEYS */;
INSERT INTO `role` VALUES (1,'ADMIN');
/*!40000 ALTER TABLE `role` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `status`
--

DROP TABLE IF EXISTS `status`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `status` (
  `id` int NOT NULL AUTO_INCREMENT,
  `value` varchar(45) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `status`
--

LOCK TABLES `status` WRITE;
/*!40000 ALTER TABLE `status` DISABLE KEYS */;
INSERT INTO `status` VALUES (1,'ACTIVE'),(2,'PENDING'),(3,'INACTIVE'),(4,'BLOCKED'),(5,'DELIVERED'),(6,'PACKING'),(7,'APPROVED'),(8,'REJECTED'),(9,'CANCELED'),(10,'VERIFIED'),(11,'RECEIVED'),(12,'COMPLETED'),(13,'FAILED_OUT_OF_STOCK');
/*!40000 ALTER TABLE `status` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `stock`
--

DROP TABLE IF EXISTS `stock`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `stock` (
  `id` int NOT NULL AUTO_INCREMENT,
  `price` double NOT NULL,
  `qty` int NOT NULL,
  `status` tinyint NOT NULL,
  `created_at` datetime DEFAULT NULL,
  `updated_at` datetime DEFAULT NULL,
  `product_id` int NOT NULL,
  `warehouse_id` int NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_stock_product1_idx` (`product_id`),
  KEY `fk_stock_warehouse1_idx` (`warehouse_id`),
  CONSTRAINT `fk_stock_product1` FOREIGN KEY (`product_id`) REFERENCES `product` (`id`),
  CONSTRAINT `fk_stock_warehouse1` FOREIGN KEY (`warehouse_id`) REFERENCES `warehouse` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `stock`
--

LOCK TABLES `stock` WRITE;
/*!40000 ALTER TABLE `stock` DISABLE KEYS */;
INSERT INTO `stock` VALUES (1,600,41,1,'2026-06-25 07:01:14','2026-07-05 09:34:33',1,1),(2,400,77,1,'2026-06-25 09:57:46','2026-06-30 13:12:24',2,1),(3,200,14,1,'2026-06-25 09:57:46','2026-06-30 13:12:23',3,1),(9,585000,17,1,'2026-06-26 05:18:03','2026-06-26 13:07:27',9,1),(14,285000,34,1,'2026-06-26 06:25:50','2026-06-29 14:45:11',14,1),(15,123,3,1,'2026-06-26 06:31:38','2026-06-30 15:04:28',15,3),(16,123,60,1,'2026-06-26 06:36:05','2026-06-26 20:56:58',16,2),(17,12,7,1,'2026-06-26 06:40:33','2026-06-29 17:58:37',17,2),(18,12,12,1,'2026-06-26 06:49:45','2026-06-26 06:49:45',18,2),(19,125000,10,1,'2026-06-26 07:00:41','2026-06-26 12:35:15',19,2),(20,110,60,1,'2026-06-30 08:04:45','2026-06-30 08:04:45',20,3);
/*!40000 ALTER TABLE `stock` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `id` int NOT NULL AUTO_INCREMENT,
  `first_name` varchar(45) NOT NULL,
  `last_name` varchar(45) NOT NULL,
  `email` varchar(60) NOT NULL,
  `password` varchar(20) NOT NULL,
  `active` tinyint NOT NULL,
  `created_at` datetime NOT NULL,
  `updated_at` datetime NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=35 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (25,'han','kelum','han@gmail.com','@Hhan0000',1,'2026-06-17 05:19:35','2026-06-17 05:19:35'),(26,'John','Doe','john.doe@gmail.com','Test@1234',1,'2026-06-17 10:33:01','2026-06-17 10:33:01'),(27,'John','Doe','john.do@gmail.com','Test@1234',1,'2026-06-17 10:33:45','2026-06-17 10:33:45'),(28,'Smana','fonseka','samana@gmail.com','Test@1234',1,'2026-06-17 10:47:32','2026-06-17 10:47:32'),(29,'kumara','fonseka','kumara@gmail.com','Test@1234',1,'2026-06-17 13:58:15','2026-06-17 13:58:15'),(30,'denuka','fonseka','denuka@gmail.com','Test@1234',1,'2026-06-17 13:59:24','2026-06-17 13:59:24'),(32,'Heshan','Kelum','heshan@gmail.com','@Heshan0000',1,'2026-06-23 07:34:01','2026-06-23 07:34:01'),(33,'Sahan','Nirmantha','sahan@gmail.com','@Sahan27851',1,'2026-06-23 10:01:00','2026-06-23 10:01:00'),(34,'Saman','Perera','saman@gmail.com','sama',1,'2026-06-30 12:54:40','2026-06-30 12:54:40');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `warehouse`
--

DROP TABLE IF EXISTS `warehouse`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `warehouse` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(45) NOT NULL,
  `location` varchar(45) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `warehouse`
--

LOCK TABLES `warehouse` WRITE;
/*!40000 ALTER TABLE `warehouse` DISABLE KEYS */;
INSERT INTO `warehouse` VALUES (1,'A','Colombo'),(2,'B','Gampaha'),(3,'C','Kiribathgoda');
/*!40000 ALTER TABLE `warehouse` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-07-05 11:33:02
