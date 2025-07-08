CREATE DATABASE  IF NOT EXISTS `hospitaldemo` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `hospitaldemo`;
-- MySQL dump 10.13  Distrib 8.0.42, for Win64 (x86_64)
--
-- Host: localhost    Database: hospitaldemo
-- ------------------------------------------------------
-- Server version	8.0.42

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
-- Table structure for table `appointments`
--

DROP TABLE IF EXISTS `appointments`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `appointments` (
                                `id` bigint NOT NULL AUTO_INCREMENT,
                                `created_date_time` datetime(6) DEFAULT NULL,
                                `created_by` varchar(255) DEFAULT NULL,
                                `status` int DEFAULT NULL,
                                `modified_date_time` datetime(6) DEFAULT NULL,
                                `updated_by` varchar(255) DEFAULT NULL,
                                `age` int DEFAULT NULL,
                                `appointment_date_time` datetime(6) DEFAULT NULL,
                                `email` varchar(255) DEFAULT NULL,
                                `name` varchar(255) DEFAULT NULL,
                                `phone_number` varchar(255) DEFAULT NULL,
                                `reason` varchar(255) DEFAULT NULL,
                                `doctor_id` bigint DEFAULT NULL,
                                `patient_id` bigint DEFAULT NULL,
                                `room_id` bigint DEFAULT NULL,
                                `note_cancel` varchar(255) DEFAULT NULL,
                                `conclusion` varchar(255) DEFAULT NULL,
                                `prescription` varchar(255) DEFAULT NULL,
                                `pay_cost` decimal(38,2) DEFAULT NULL,
                                `payment_status` int DEFAULT NULL,
                                `total_cost` decimal(38,2) DEFAULT NULL,
                                `full_name` varchar(255) DEFAULT NULL,
                                `phone` varchar(255) DEFAULT NULL,
                                `vnp_txt_ref` varchar(255) DEFAULT NULL,
                                `appointment_id` bigint DEFAULT NULL,
                                `recep_id` bigint DEFAULT NULL,
                                PRIMARY KEY (`id`),
                                KEY `FKmujeo4tymoo98cmf7uj3vsv76` (`doctor_id`),
                                KEY `FK8exap5wmg8kmb1g1rx3by21yt` (`patient_id`),
                                KEY `FKbsma6x4pnujct0e6xkycu9864` (`room_id`),
                                KEY `FKhh1fyvidiepmkl061yee7n7ow` (`appointment_id`),
                                KEY `FKmse9k77i553sdeply5we7hci3` (`recep_id`),
                                CONSTRAINT `FK8exap5wmg8kmb1g1rx3by21yt` FOREIGN KEY (`patient_id`) REFERENCES `patients` (`id`),
                                CONSTRAINT `FKbsma6x4pnujct0e6xkycu9864` FOREIGN KEY (`room_id`) REFERENCES `rooms` (`id`),
                                CONSTRAINT `FKhh1fyvidiepmkl061yee7n7ow` FOREIGN KEY (`appointment_id`) REFERENCES `appointments` (`id`),
                                CONSTRAINT `FKmse9k77i553sdeply5we7hci3` FOREIGN KEY (`recep_id`) REFERENCES `receptionists` (`id`),
                                CONSTRAINT `FKmujeo4tymoo98cmf7uj3vsv76` FOREIGN KEY (`doctor_id`) REFERENCES `doctors` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `appointments`
--

LOCK TABLES `appointments` WRITE;
/*!40000 ALTER TABLE `appointments` DISABLE KEYS */;
INSERT INTO `appointments` VALUES (1,NULL,NULL,0,NULL,NULL,18,'2000-12-11 17:00:00.000000','chuyendizz@gmail.com','Nguyễn Trang','0987666111','Tôi bị đau trong tai, có dấu hiệu ù ù trong đầu. Tôi muốn được khám chuyên sâu',1,4,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(3,NULL,NULL,0,NULL,NULL,19,'2025-06-16 09:00:00.000000','chuyendizz@gmail.com','Nguyễn Văn Trang','0987666111','Tôi bị đau họng. Nghi do bị cúm. Cần khám tổng quát',1,4,2,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(5,NULL,NULL,0,NULL,NULL,20,'2025-06-16 17:00:00.000000','chuyendizz@gmail.com','Nguyễn Văn Trang','0987666111','Tôi bị đau họng',3,4,2,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(9,NULL,NULL,0,NULL,NULL,22,'2025-06-18 17:00:00.000000','chuyendizz@gmail.com','Nguyễn Văn Trang','0987666111','Bị đau họng',1,4,2,'Có việc gấp',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(10,NULL,NULL,2,NULL,NULL,NULL,'2025-06-17 19:08:00.000000','chuyendizz@gmail.com','Lâm Thị Bình','0917666555','Có vấn đề về họng',1,3,2,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(11,NULL,NULL,2,NULL,NULL,NULL,'2025-06-19 07:09:00.000000','chuyendizz@gmail.com','Mic Le Bim','0947134196','Có vấn đề về tai',3,3,2,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(13,NULL,NULL,2,NULL,NULL,NULL,'2025-06-16 08:43:00.000000','ntt@gmail.com','Nguyễn Thuỳ Trang','6308348000','Yếu',1,3,2,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(14,NULL,NULL,-1,NULL,NULL,NULL,'2025-06-21 10:34:00.000000','hvv@gmail.com','Hoàng Văn Vinh','0947134196','Yếu tai',1,3,2,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(15,NULL,NULL,1,NULL,NULL,19,'2025-07-06 17:00:00.000000','chuyendizz@gmail.com','Nguyễn Văn Trang','0987666111','Bị đau trong tai',1,4,2,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(16,NULL,NULL,1,NULL,NULL,20,'2025-07-07 07:00:00.000000','chuyendizz@gmail.com','Nguyễn Văn Trang','0987666111','Bị đau trong tai',1,4,2,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL);
/*!40000 ALTER TABLE `appointments` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `conclusion`
--

DROP TABLE IF EXISTS `conclusion`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `conclusion` (
                              `id` bigint NOT NULL AUTO_INCREMENT,
                              `created_date_time` datetime(6) DEFAULT NULL,
                              `created_by` varchar(255) DEFAULT NULL,
                              `status` int DEFAULT NULL,
                              `modified_date_time` datetime(6) DEFAULT NULL,
                              `updated_by` varchar(255) DEFAULT NULL,
                              `content` varchar(255) DEFAULT NULL,
                              `prescription` varchar(4000) DEFAULT NULL,
                              `appointment_id` bigint DEFAULT NULL,
                              `version` bigint DEFAULT NULL,
                              PRIMARY KEY (`id`),
                              UNIQUE KEY `UK67pdwi9oh7hcsyjpn67rmce0d` (`appointment_id`),
                              CONSTRAINT `FKk6c6a5vfpoxxrj7sn1mkpib8s` FOREIGN KEY (`appointment_id`) REFERENCES `appointments` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `conclusion`
--

LOCK TABLES `conclusion` WRITE;
/*!40000 ALTER TABLE `conclusion` DISABLE KEYS */;
INSERT INTO `conclusion` VALUES (1,NULL,NULL,NULL,NULL,NULL,'Viêm tai','1. Ciprofloxacin (Otogesic)\r\nCông dụng: Kháng sinh dùng điều trị viêm tai ngoài hoặc viêm tai giữa.\r\nLiều dùng: 3-4 giọt vào tai bị viêm, ngày 2-3 lần.\r\nGiá: 50000 VND\r\n2. Amoxicillin 500mg\r\nCông dụng: Kháng sinh phổ rộng, dùng trong viêm tai giữa hoặc viêm tai có biến chứng.\r\nLiều dùng: 500mg x 3 lần/ngày, dùng trong 5-7 ngày.\r\nGiá: 30000 VND',15,0),(2,NULL,NULL,NULL,NULL,NULL,'Viêm tai, về uống thuốc theo chỉ định','1. Ciprofloxacin (Otogesic)\r\n Công dụng: Kháng sinh dùng điều trị viêm tai ngoài hoặc viêm tai giữa.\r\n Liều dùng: 3-4 giọt vào tai bị viêm, ngày 2-3 lần.\r\n Giá: 50000 VND\r\n 2. Amoxicillin 500mg\r\n Công dụng: Kháng sinh phổ rộng, dùng trong viêm tai giữa hoặc viêm tai có biến chứng.\r\n Liều dùng: 500mg x 3 lần/ngày, dùng trong 5-7 ngày.\r\n Giá: 30000 VND',16,NULL);
/*!40000 ALTER TABLE `conclusion` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `diagnosis`
--

DROP TABLE IF EXISTS `diagnosis`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `diagnosis` (
                             `id` bigint NOT NULL AUTO_INCREMENT,
                             `created_date_time` datetime(6) DEFAULT NULL,
                             `created_by` varchar(255) DEFAULT NULL,
                             `status` int DEFAULT NULL,
                             `modified_date_time` datetime(6) DEFAULT NULL,
                             `updated_by` varchar(255) DEFAULT NULL,
                             `content` varchar(255) DEFAULT NULL,
                             `appointment_id` bigint DEFAULT NULL,
                             `level` int DEFAULT NULL,
                             `price` decimal(38,2) DEFAULT NULL,
                             PRIMARY KEY (`id`),
                             KEY `FKbkbgbpqbqpkkcnbsw23o8xy09` (`appointment_id`),
                             CONSTRAINT `FKbkbgbpqbqpkkcnbsw23o8xy09` FOREIGN KEY (`appointment_id`) REFERENCES `appointments` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `diagnosis`
--

LOCK TABLES `diagnosis` WRITE;
/*!40000 ALTER TABLE `diagnosis` DISABLE KEYS */;
INSERT INTO `diagnosis` VALUES (2,NULL,NULL,NULL,NULL,NULL,'Lần 1: Có dấu hiệu viêm tai, cần siêu âm kĩ hơn',15,1,NULL),(3,NULL,NULL,NULL,NULL,NULL,'Lần 2: Sau siêu âm, đã rõ là viêm tai',15,2,NULL),(5,NULL,NULL,NULL,NULL,NULL,'Lần 1: Sau khi siêu âm đã rõ là viêm tai',16,1,NULL);
/*!40000 ALTER TABLE `diagnosis` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `doctors`
--

DROP TABLE IF EXISTS `doctors`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `doctors` (
                           `id` bigint NOT NULL AUTO_INCREMENT,
                           `created_date_time` datetime(6) DEFAULT NULL,
                           `created_by` varchar(255) DEFAULT NULL,
                           `status` int DEFAULT NULL,
                           `modified_date_time` datetime(6) DEFAULT NULL,
                           `updated_by` varchar(255) DEFAULT NULL,
                           `email` varchar(255) DEFAULT NULL,
                           `first_name` varchar(255) DEFAULT NULL,
                           `last_name` varchar(255) DEFAULT NULL,
                           `phone_number` varchar(20) DEFAULT NULL,
                           `specialization` varchar(255) DEFAULT NULL,
                           `yoe` int DEFAULT NULL,
                           `user_id` bigint DEFAULT NULL,
                           `avatar` varchar(255) DEFAULT NULL,
                           `certificate_file_name` varchar(255) DEFAULT NULL,
                           `address` varchar(255) DEFAULT NULL,
                           PRIMARY KEY (`id`),
                           KEY `FKe9pf5qtxxkdyrwibaevo9frtk` (`user_id`),
                           CONSTRAINT `FKe9pf5qtxxkdyrwibaevo9frtk` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `doctors`
--

LOCK TABLES `doctors` WRITE;
/*!40000 ALTER TABLE `doctors` DISABLE KEYS */;
INSERT INTO `doctors` VALUES (1,'2023-05-29 22:35:14.000000','admin',1,NULL,NULL,'lean@gmail.com','Lê Văn','An','0901234111','Tai',10,8,'http://res.cloudinary.com/djyw3ytjd/image/upload/v1751357206/sxz4asig5bsifvwwun1p.png','http://res.cloudinary.com/djyw3ytjd/image/upload/v1751357204/q96bmipbcdibxfbnfkax.pdf',NULL),(2,'2023-05-29 22:35:14.000000','admin',1,NULL,NULL,'tran.binh@example.com','Trần','Bình','0912345678','Tai',12,NULL,'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRKnoumLGxOBE_zvHeuEV8nd06SQOY1w46x-Q&s',NULL,NULL),(3,'2023-05-29 22:35:14.000000','admin',1,NULL,NULL,'nguyen.cam@example.com','Nguyễn','Cẩm','0923456789','Tai',8,NULL,'https://hoanghamobile.com/tin-tuc/wp-content/uploads/2024/06/anh-bac-si.jpg',NULL,NULL),(4,'2023-05-29 22:35:14.000000','admin',1,NULL,NULL,'pham.duy@example.com','Phạm','Duy','0934567890','Tai',6,NULL,'https://i.pinimg.com/736x/8b/9e/e3/8b9ee3b48f353725e25c23321ab18649.jpg',NULL,NULL),(5,'2015-05-29 22:35:14.000000','admin',1,NULL,NULL,'hoang.anh@example.com','Hoàng','Anh','0945678901','Mũi',9,NULL,'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcR6vkLej_bKmmM-GsfU1rf5XLloXPOr79PyAg&s',NULL,NULL),(6,'2015-05-29 22:35:14.000000','admin',1,NULL,NULL,'vu.hoa@example.com','Vũ','Hoa','0956789012','Mũi',7,NULL,'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQ8Re0sr07LvN98BJ1mJNd6yUoyUXDFGIVtaw&s',NULL,NULL),(7,'2015-05-29 22:35:14.000000','admin',1,NULL,NULL,'do.khanh@example.com','Đỗ','Khánh','0967890123','Mũi',11,NULL,'https://taimuihongsg.com/wp-content/uploads/2018/05/Kim-Bun-ThuongE_taimuihongsg.jpg',NULL,NULL),(8,'2005-05-29 22:35:14.000000','admin',1,NULL,NULL,'pham.lan@example.com','Phạm','Lan','0978901234','Họng',15,NULL,'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSBgPShc-ycBpczZZQlkWCJs4pqZ6r_GkWrdQ&s',NULL,NULL),(9,'2005-05-29 22:35:14.000000','admin',1,NULL,NULL,'nguyen.hieu@example.com','Nguyễn','Hiếu','0989012345','Họng',13,NULL,'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQpwmU4hCu08pPMnNA0WfEfVoLXgmAUA9noug&s',NULL,NULL),(10,'2005-05-29 22:35:14.000000','admin',1,NULL,NULL,'tran.mai@example.com','Trần','Mai','0990123456','Họng',14,NULL,'https://hoanghamobile.com/tin-tuc/wp-content/uploads/2024/06/anh-bac-si-12.jpg',NULL,NULL);
/*!40000 ALTER TABLE `doctors` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `logs`
--

DROP TABLE IF EXISTS `logs`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `logs` (
                        `id` bigint NOT NULL AUTO_INCREMENT,
                        `created_date_time` datetime(6) DEFAULT NULL,
                        `created_by` varchar(255) DEFAULT NULL,
                        `status` int DEFAULT NULL,
                        `modified_date_time` datetime(6) DEFAULT NULL,
                        `updated_by` varchar(255) DEFAULT NULL,
                        `content` varchar(255) DEFAULT NULL,
                        `recep_id` bigint DEFAULT NULL,
                        `user_id` bigint DEFAULT NULL,
                        PRIMARY KEY (`id`),
                        KEY `FK95ecsavc1r109m33nsi3mihbi` (`recep_id`),
                        KEY `FKgqy8beil5y4almtq1tiyofije` (`user_id`),
                        CONSTRAINT `FK95ecsavc1r109m33nsi3mihbi` FOREIGN KEY (`recep_id`) REFERENCES `receptionists` (`id`),
                        CONSTRAINT `FKgqy8beil5y4almtq1tiyofije` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `logs`
--

LOCK TABLES `logs` WRITE;
/*!40000 ALTER TABLE `logs` DISABLE KEYS */;
INSERT INTO `logs` VALUES (1,NULL,NULL,NULL,NULL,NULL,'Tạo mới lịch hẹn cho bệnh nhân: Hoàng Văn Vinh, thời gian: 2025-06-20T17:34',1,NULL),(2,'2025-06-19 14:23:54.139614',NULL,NULL,NULL,NULL,'Cập nhật trạng thái của người dùng thành Mở khoá có id 9',NULL,3),(3,'2025-06-19 14:25:57.654667',NULL,NULL,NULL,NULL,'Cập nhật thông tin phòng có id 2',NULL,3);
/*!40000 ALTER TABLE `logs` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `nurses`
--

DROP TABLE IF EXISTS `nurses`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `nurses` (
                          `id` bigint NOT NULL AUTO_INCREMENT,
                          `created_date_time` datetime(6) DEFAULT NULL,
                          `created_by` varchar(255) DEFAULT NULL,
                          `status` int DEFAULT NULL,
                          `modified_date_time` datetime(6) DEFAULT NULL,
                          `updated_by` varchar(255) DEFAULT NULL,
                          `address` varchar(255) DEFAULT NULL,
                          `first_name` varchar(255) DEFAULT NULL,
                          `last_name` varchar(255) DEFAULT NULL,
                          `phone_number` varchar(20) DEFAULT NULL,
                          `user_id` bigint DEFAULT NULL,
                          `avatar` varchar(255) DEFAULT NULL,
                          PRIMARY KEY (`id`),
                          KEY `FK91rtea8eoy5devpkpwuqsjk7c` (`user_id`),
                          CONSTRAINT `FK91rtea8eoy5devpkpwuqsjk7c` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `nurses`
--

LOCK TABLES `nurses` WRITE;
/*!40000 ALTER TABLE `nurses` DISABLE KEYS */;
INSERT INTO `nurses` VALUES (1,'2025-06-28 00:00:00.000000','ADMIN',1,'2025-06-28 00:00:00.000000','ADMIN','Hanoi','Nguyen Y','Ta','0987111222',10,'http://res.cloudinary.com/djyw3ytjd/image/upload/v1751422090/wt0omh2fnmjxp9whvwlw.jpg');
/*!40000 ALTER TABLE `nurses` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `patients`
--

DROP TABLE IF EXISTS `patients`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `patients` (
                            `id` bigint NOT NULL AUTO_INCREMENT,
                            `created_date_time` datetime(6) DEFAULT NULL,
                            `created_by` varchar(255) DEFAULT NULL,
                            `status` int DEFAULT NULL,
                            `modified_date_time` datetime(6) DEFAULT NULL,
                            `updated_by` varchar(255) DEFAULT NULL,
                            `address` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
                            `date_of_birth` date DEFAULT NULL,
                            `first_name` varchar(255) DEFAULT NULL,
                            `gender` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
                            `last_name` varchar(255) DEFAULT NULL,
                            `phone` varchar(20) DEFAULT NULL,
                            `user_id` bigint DEFAULT NULL,
                            `avatar` varchar(255) DEFAULT NULL,
                            PRIMARY KEY (`id`),
                            KEY `FKuwca24wcd1tg6pjex8lmc0y7` (`user_id`),
                            CONSTRAINT `FKuwca24wcd1tg6pjex8lmc0y7` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `patients`
--

LOCK TABLES `patients` WRITE;
/*!40000 ALTER TABLE `patients` DISABLE KEYS */;
INSERT INTO `patients` VALUES (1,NULL,NULL,1,NULL,NULL,'3602 Gaylord Dr','2000-12-11','Hoàng','MALE','Vinh','6308348000',1,NULL),(2,NULL,NULL,1,NULL,NULL,'3602 Gaylord Dr','2000-03-11','Nguyễn','FEMALE','Trang','6308348000',2,NULL),(3,NULL,NULL,1,NULL,NULL,'Quận 5','2000-12-11','Minh','MALE','Tuấn','0912345611',4,NULL),(4,NULL,NULL,1,NULL,NULL,'3602 Gaylord Dr','2000-12-09','Nguyễn Văn','MALE','Trang','0987666111',9,'http://res.cloudinary.com/djyw3ytjd/image/upload/v1751421640/mykte6feovcjnezh8ghk.jpg');
/*!40000 ALTER TABLE `patients` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `receptionists`
--

DROP TABLE IF EXISTS `receptionists`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `receptionists` (
                                 `id` bigint NOT NULL AUTO_INCREMENT,
                                 `created_date_time` datetime(6) DEFAULT NULL,
                                 `created_by` varchar(255) DEFAULT NULL,
                                 `status` int DEFAULT NULL,
                                 `modified_date_time` datetime(6) DEFAULT NULL,
                                 `updated_by` varchar(255) DEFAULT NULL,
                                 `email` varchar(255) DEFAULT NULL,
                                 `first_name` varchar(255) DEFAULT NULL,
                                 `last_name` varchar(255) DEFAULT NULL,
                                 `phone_number` varchar(20) DEFAULT NULL,
                                 `user_id` bigint DEFAULT NULL,
                                 `avatar` varchar(255) DEFAULT NULL,
                                 PRIMARY KEY (`id`),
                                 KEY `FKq3ssn9a7reu88v2rnejl6vmte` (`user_id`),
                                 CONSTRAINT `FKq3ssn9a7reu88v2rnejl6vmte` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `receptionists`
--

LOCK TABLES `receptionists` WRITE;
/*!40000 ALTER TABLE `receptionists` DISABLE KEYS */;
INSERT INTO `receptionists` VALUES (1,NULL,NULL,1,NULL,NULL,'letan@gmail.com','Lễ','Văn Tân','0987112312',7,'http://res.cloudinary.com/djyw3ytjd/image/upload/v1751422051/mgk2mkgizaxwcpv6sl4d.png');
/*!40000 ALTER TABLE `receptionists` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `request`
--

DROP TABLE IF EXISTS `request`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `request` (
                           `id` bigint NOT NULL AUTO_INCREMENT,
                           `created_date_time` datetime(6) DEFAULT NULL,
                           `created_by` varchar(255) DEFAULT NULL,
                           `status` int DEFAULT NULL,
                           `modified_date_time` datetime(6) DEFAULT NULL,
                           `updated_by` varchar(255) DEFAULT NULL,
                           `content` varchar(50) NOT NULL,
                           `user_id` bigint DEFAULT NULL,
                           PRIMARY KEY (`id`),
                           KEY `FKg03bldv86pfuboqfefx48p6u3` (`user_id`),
                           CONSTRAINT `FKg03bldv86pfuboqfefx48p6u3` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `request`
--

LOCK TABLES `request` WRITE;
/*!40000 ALTER TABLE `request` DISABLE KEYS */;
INSERT INTO `request` VALUES (2,'2025-06-19 14:46:05.870557',NULL,NULL,NULL,NULL,'Tôi muốn',9);
/*!40000 ALTER TABLE `request` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `reviews`
--

DROP TABLE IF EXISTS `reviews`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `reviews` (
                           `id` bigint NOT NULL AUTO_INCREMENT,
                           `created_date_time` datetime(6) DEFAULT NULL,
                           `created_by` varchar(255) DEFAULT NULL,
                           `status` int DEFAULT NULL,
                           `modified_date_time` datetime(6) DEFAULT NULL,
                           `updated_by` varchar(255) DEFAULT NULL,
                           `first_name` varchar(255) DEFAULT NULL,
                           `star` int DEFAULT NULL,
                           `patient_id` bigint DEFAULT NULL,
                           PRIMARY KEY (`id`),
                           KEY `FKkg4mbcdlujuf6rsml463et0bh` (`patient_id`),
                           CONSTRAINT `FKkg4mbcdlujuf6rsml463et0bh` FOREIGN KEY (`patient_id`) REFERENCES `patients` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `reviews`
--

LOCK TABLES `reviews` WRITE;
/*!40000 ALTER TABLE `reviews` DISABLE KEYS */;
/*!40000 ALTER TABLE `reviews` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `roles`
--

DROP TABLE IF EXISTS `roles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `roles` (
                         `id` bigint NOT NULL AUTO_INCREMENT,
                         `created_date_time` datetime(6) DEFAULT NULL,
                         `created_by` varchar(255) DEFAULT NULL,
                         `status` int DEFAULT NULL,
                         `modified_date_time` datetime(6) DEFAULT NULL,
                         `updated_by` varchar(255) DEFAULT NULL,
                         `name` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
                         PRIMARY KEY (`id`),
                         UNIQUE KEY `UKofx66keruapi6vyqpv6f2or37` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `roles`
--

LOCK TABLES `roles` WRITE;
/*!40000 ALTER TABLE `roles` DISABLE KEYS */;
INSERT INTO `roles` VALUES (1,NULL,NULL,NULL,NULL,NULL,'ADMIN'),(2,NULL,NULL,NULL,NULL,NULL,'DOCTOR'),(3,NULL,NULL,NULL,NULL,NULL,'RECEPTIONIST'),(4,NULL,NULL,NULL,NULL,NULL,'PATIENT'),(5,NULL,NULL,NULL,NULL,NULL,'NURSE');
/*!40000 ALTER TABLE `roles` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `rooms`
--

DROP TABLE IF EXISTS `rooms`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `rooms` (
                         `id` bigint NOT NULL AUTO_INCREMENT,
                         `created_date_time` datetime(6) DEFAULT NULL,
                         `created_by` varchar(255) DEFAULT NULL,
                         `status` int DEFAULT NULL,
                         `modified_date_time` datetime(6) DEFAULT NULL,
                         `updated_by` varchar(255) DEFAULT NULL,
                         `capacity` int DEFAULT NULL,
                         `description` varchar(500) DEFAULT NULL,
                         `location` varchar(255) DEFAULT NULL,
                         `room_name` varchar(100) NOT NULL,
                         `room_type` varchar(100) DEFAULT NULL,
                         `phone_number` varchar(20) DEFAULT NULL,
                         `primary_doctor` bigint DEFAULT NULL,
                         `primary_nurse` bigint DEFAULT NULL,
                         PRIMARY KEY (`id`),
                         KEY `FK3kldibiwp67ay4xxxx2kjyv3u` (`primary_doctor`),
                         KEY `FKmvj0epabe0f67exysw48uif1t` (`primary_nurse`),
                         CONSTRAINT `FK3kldibiwp67ay4xxxx2kjyv3u` FOREIGN KEY (`primary_doctor`) REFERENCES `doctors` (`id`),
                         CONSTRAINT `FKmvj0epabe0f67exysw48uif1t` FOREIGN KEY (`primary_nurse`) REFERENCES `nurses` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `rooms`
--

LOCK TABLES `rooms` WRITE;
/*!40000 ALTER TABLE `rooms` DISABLE KEYS */;
INSERT INTO `rooms` VALUES (2,NULL,NULL,NULL,NULL,NULL,NULL,'Phòng khám mũi chuyên sâu','Tầng 1','P101','Phòng khám mũi','0901234111',1,NULL);
/*!40000 ALTER TABLE `rooms` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `shifts`
--

DROP TABLE IF EXISTS `shifts`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `shifts` (
                          `id` bigint NOT NULL AUTO_INCREMENT,
                          `created_date_time` datetime(6) DEFAULT NULL,
                          `created_by` varchar(255) DEFAULT NULL,
                          `status` int DEFAULT NULL,
                          `modified_date_time` datetime(6) DEFAULT NULL,
                          `updated_by` varchar(255) DEFAULT NULL,
                          `description` varchar(255) DEFAULT NULL,
                          `end_time` datetime(6) NOT NULL,
                          `start_time` datetime(6) NOT NULL,
                          `type` varchar(255) DEFAULT NULL,
                          `doctor_id` bigint DEFAULT NULL,
                          `nurse_id` bigint DEFAULT NULL,
                          PRIMARY KEY (`id`),
                          KEY `FKjm5n210pw8co0ftrntordidel` (`doctor_id`),
                          KEY `FK9aegt7kvo6nvdt3eb7a11v86n` (`nurse_id`),
                          CONSTRAINT `FK9aegt7kvo6nvdt3eb7a11v86n` FOREIGN KEY (`nurse_id`) REFERENCES `nurses` (`id`),
                          CONSTRAINT `FKjm5n210pw8co0ftrntordidel` FOREIGN KEY (`doctor_id`) REFERENCES `doctors` (`id`),
                          CONSTRAINT `FKk06mv2ogrb522aklvwlujwqmv` FOREIGN KEY (`nurse_id`) REFERENCES `doctors` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `shifts`
--

LOCK TABLES `shifts` WRITE;
/*!40000 ALTER TABLE `shifts` DISABLE KEYS */;
INSERT INTO `shifts` VALUES (2,NULL,NULL,NULL,NULL,NULL,'','2025-06-29 10:00:00.000000','2025-06-29 06:00:00.000000',NULL,1,NULL),(3,NULL,NULL,NULL,NULL,NULL,'Ca làm sáng','2025-06-29 10:00:00.000000','2025-06-29 06:00:00.000000',NULL,NULL,1);
/*!40000 ALTER TABLE `shifts` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
                         `id` bigint NOT NULL AUTO_INCREMENT,
                         `created_date_time` datetime(6) DEFAULT NULL,
                         `created_by` varchar(255) DEFAULT NULL,
                         `status` int DEFAULT NULL,
                         `modified_date_time` datetime(6) DEFAULT NULL,
                         `updated_by` varchar(255) DEFAULT NULL,
                         `email` varchar(50) NOT NULL,
                         `password` varchar(120) NOT NULL,
                         `role_id` bigint DEFAULT NULL,
                         `created_by_receptionist` tinyint(1) DEFAULT '0',
                         `is_first_login` tinyint(1) DEFAULT '0',
                         `login_attempts` int DEFAULT '0',
                         PRIMARY KEY (`id`),
                         UNIQUE KEY `UK6dotkott2kjsp8vw4d0m25fb7` (`email`),
                         KEY `FKp56c1712k691lhsyewcssf40f` (`role_id`),
                         CONSTRAINT `FKp56c1712k691lhsyewcssf40f` FOREIGN KEY (`role_id`) REFERENCES `roles` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,NULL,NULL,1,NULL,NULL,'test@gmail.com','$2a$10$OnQpkw.ulzLGKnZ34Oecpu8syeEX6tkovJumkbW5/hLM8ilzGXjTa',4,0,0,0),(2,NULL,NULL,1,NULL,NULL,'phamminhhiep0402@gmail.com','$2a$10$oaeHyie3lSBGFKIgubY7G.Eofj/tsaXbXiP9dPLRamVJDpwMZEFfe',4,0,0,0),(3,NULL,NULL,1,NULL,NULL,'admin@gmail.com','$2a$10$oaeHyie3lSBGFKIgubY7G.Eofj/tsaXbXiP9dPLRamVJDpwMZEFfe',1,0,0,0),(4,NULL,NULL,0,NULL,NULL,'lvc@gmail.com','$2a$10$dZ8FEPYRPYARYMjYt8fjZu3ghCKb2wAIeLp866irlR93WyblMCs0e',4,0,0,0),(5,NULL,NULL,1,NULL,NULL,'khoahoangfb@gmail.com','$2a$10$9fJhVZqumYrFxZwXUSOHCeXeQ6yL8sqsFwd0M.oe7m2zHuTXjl9Ne',1,0,0,0),(7,NULL,NULL,1,NULL,NULL,'letan@gmail.com','$2a$10$oaeHyie3lSBGFKIgubY7G.Eofj/tsaXbXiP9dPLRamVJDpwMZEFfe',3,0,0,0),(8,NULL,NULL,1,NULL,NULL,'lean@gmail.com','$2a$10$oaeHyie3lSBGFKIgubY7G.Eofj/tsaXbXiP9dPLRamVJDpwMZEFfe',2,0,0,0),(9,NULL,NULL,1,NULL,NULL,'chuyendizz@gmail.com','$2a$10$Yd7AU/W1GdYYajcHY.fZcOROJqz8JyYrsyxNKa8LxjvrAKhElqLAK',4,0,0,0),(10,NULL,NULL,1,NULL,NULL,'yta@gmail.com','$2a$10$Yd7AU/W1GdYYajcHY.fZcOROJqz8JyYrsyxNKa8LxjvrAKhElqLAK',5,0,0,0);
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping events for database 'hospitaldemo'
--

--
-- Dumping routines for database 'hospitaldemo'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-07-08  9:00:02
