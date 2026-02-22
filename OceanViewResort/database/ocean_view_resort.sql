-- MySQL dump 10.13  Distrib 8.0.45, for Win64 (x86_64)
--
-- Host: localhost    Database: ocean_view_resort
-- ------------------------------------------------------
-- Server version	8.0.45

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
-- Table structure for table `audit_logs`
--

DROP TABLE IF EXISTS `audit_logs`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `audit_logs` (
  `log_id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint DEFAULT NULL,
  `action_type` enum('INSERT','UPDATE','DELETE') NOT NULL,
  `table_name` varchar(50) NOT NULL,
  `record_id` bigint NOT NULL,
  `old_values` json DEFAULT NULL,
  `new_values` json DEFAULT NULL,
  `ip_address` varchar(45) DEFAULT NULL,
  `action_timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`log_id`),
  KEY `fk_audit_user` (`user_id`),
  KEY `idx_audit_entity` (`table_name`,`record_id`),
  KEY `idx_audit_time` (`action_timestamp`),
  CONSTRAINT `fk_audit_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE SET NULL ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `audit_logs`
--

LOCK TABLES `audit_logs` WRITE;
/*!40000 ALTER TABLE `audit_logs` DISABLE KEYS */;
/*!40000 ALTER TABLE `audit_logs` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `bills`
--

DROP TABLE IF EXISTS `bills`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `bills` (
  `bill_id` bigint NOT NULL AUTO_INCREMENT,
  `bill_number` varchar(30) NOT NULL,
  `reservation_id` bigint NOT NULL,
  `room_charges` decimal(10,2) NOT NULL DEFAULT '0.00',
  `service_charges` decimal(10,2) NOT NULL DEFAULT '0.00',
  `subtotal_amount` decimal(10,2) NOT NULL DEFAULT '0.00',
  `tax_amount` decimal(10,2) NOT NULL DEFAULT '0.00',
  `discount_amount` decimal(10,2) NOT NULL DEFAULT '0.00',
  `total_amount` decimal(10,2) NOT NULL DEFAULT '0.00',
  `amount_paid` decimal(10,2) NOT NULL DEFAULT '0.00',
  `balance_due` decimal(10,2) NOT NULL DEFAULT '0.00',
  `credit_amount` decimal(10,2) NOT NULL DEFAULT '0.00',
  `payment_status` enum('unpaid','partial','paid','credit') NOT NULL DEFAULT 'unpaid',
  `bill_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `created_by` bigint NOT NULL,
  PRIMARY KEY (`bill_id`),
  UNIQUE KEY `bill_number` (`bill_number`),
  UNIQUE KEY `reservation_id` (`reservation_id`),
  KEY `fk_bill_user` (`created_by`),
  KEY `idx_bill_status` (`payment_status`),
  KEY `idx_bill_date` (`bill_date`),
  KEY `idx_bill_res` (`reservation_id`),
  CONSTRAINT `fk_bill_res` FOREIGN KEY (`reservation_id`) REFERENCES `reservations` (`reservation_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_bill_user` FOREIGN KEY (`created_by`) REFERENCES `users` (`user_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `chk_bill_nonneg` CHECK (((`room_charges` >= 0) and (`service_charges` >= 0) and (`subtotal_amount` >= 0) and (`tax_amount` >= 0) and (`discount_amount` >= 0) and (`total_amount` >= 0) and (`amount_paid` >= 0) and (`balance_due` >= 0) and (`credit_amount` >= 0)))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `bills`
--

LOCK TABLES `bills` WRITE;
/*!40000 ALTER TABLE `bills` DISABLE KEYS */;
/*!40000 ALTER TABLE `bills` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `guests`
--

DROP TABLE IF EXISTS `guests`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `guests` (
  `guest_id` bigint NOT NULL AUTO_INCREMENT,
  `guest_name` varchar(100) NOT NULL,
  `nic_passport` varchar(50) DEFAULT NULL,
  `address` text,
  `city` varchar(50) DEFAULT NULL,
  `country` varchar(50) DEFAULT 'Sri Lanka',
  `contact_number` varchar(20) NOT NULL,
  `email` varchar(100) DEFAULT NULL,
  `date_of_birth` date DEFAULT NULL,
  `guest_type` enum('individual','corporate','vip') NOT NULL DEFAULT 'individual',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0',
  `deleted_at` timestamp NULL DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`guest_id`),
  UNIQUE KEY `nic_passport` (`nic_passport`),
  UNIQUE KEY `uq_guest_email` (`email`),
  KEY `idx_contact` (`contact_number`),
  KEY `idx_nic` (`nic_passport`),
  KEY `idx_guest_deleted` (`is_deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `guests`
--

LOCK TABLES `guests` WRITE;
/*!40000 ALTER TABLE `guests` DISABLE KEYS */;
/*!40000 ALTER TABLE `guests` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `payments`
--

DROP TABLE IF EXISTS `payments`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `payments` (
  `payment_id` bigint NOT NULL AUTO_INCREMENT,
  `bill_id` bigint NOT NULL,
  `payment_method` enum('cash','card','bank_transfer','online') NOT NULL,
  `amount` decimal(10,2) NOT NULL,
  `payment_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `transaction_reference` varchar(100) DEFAULT NULL,
  `card_last4` varchar(4) DEFAULT NULL,
  `card_auth_code` varchar(30) DEFAULT NULL,
  `payment_notes` text,
  `received_by` bigint NOT NULL,
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0',
  `deleted_at` timestamp NULL DEFAULT NULL,
  `deleted_by` bigint DEFAULT NULL,
  PRIMARY KEY (`payment_id`),
  KEY `fk_pay_user` (`received_by`),
  KEY `fk_pay_deleted_by` (`deleted_by`),
  KEY `idx_pay_bill` (`bill_id`),
  KEY `idx_pay_date` (`payment_date`),
  KEY `idx_pay_deleted` (`is_deleted`),
  CONSTRAINT `fk_pay_bill` FOREIGN KEY (`bill_id`) REFERENCES `bills` (`bill_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_pay_deleted_by` FOREIGN KEY (`deleted_by`) REFERENCES `users` (`user_id`) ON DELETE SET NULL ON UPDATE RESTRICT,
  CONSTRAINT `fk_pay_user` FOREIGN KEY (`received_by`) REFERENCES `users` (`user_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `chk_payment_amount` CHECK ((`amount` > 0))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `payments`
--

LOCK TABLES `payments` WRITE;
/*!40000 ALTER TABLE `payments` DISABLE KEYS */;
/*!40000 ALTER TABLE `payments` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `reservation_services`
--

DROP TABLE IF EXISTS `reservation_services`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `reservation_services` (
  `reservation_service_id` bigint NOT NULL AUTO_INCREMENT,
  `reservation_id` bigint NOT NULL,
  `service_id` bigint NOT NULL,
  `quantity` int NOT NULL DEFAULT '1',
  `unit_price` decimal(10,2) NOT NULL,
  `total_price` decimal(10,2) NOT NULL DEFAULT '0.00',
  `service_date` date NOT NULL,
  `notes` text,
  `is_billed` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`reservation_service_id`),
  KEY `fk_rs_service` (`service_id`),
  KEY `idx_rs_reservation` (`reservation_id`),
  KEY `idx_rs_date` (`service_date`),
  KEY `idx_rs_billed` (`is_billed`),
  CONSTRAINT `fk_rs_res` FOREIGN KEY (`reservation_id`) REFERENCES `reservations` (`reservation_id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `fk_rs_service` FOREIGN KEY (`service_id`) REFERENCES `services` (`service_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `chk_rs_qty` CHECK ((`quantity` >= 1)),
  CONSTRAINT `chk_rs_total` CHECK ((`total_price` >= 0))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `reservation_services`
--

LOCK TABLES `reservation_services` WRITE;
/*!40000 ALTER TABLE `reservation_services` DISABLE KEYS */;
/*!40000 ALTER TABLE `reservation_services` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `reservations`
--

DROP TABLE IF EXISTS `reservations`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `reservations` (
  `reservation_id` bigint NOT NULL AUTO_INCREMENT,
  `reservation_number` varchar(30) NOT NULL,
  `guest_id` bigint NOT NULL,
  `room_id` bigint NOT NULL,
  `check_in_date` date NOT NULL,
  `check_out_date` date NOT NULL,
  `adults` int NOT NULL DEFAULT '1',
  `children` int NOT NULL DEFAULT '0',
  `number_of_guests` int NOT NULL DEFAULT '1',
  `special_requests` text,
  `reservation_status` enum('pending','confirmed','checked_in','checked_out','cancelled','no_show') NOT NULL DEFAULT 'pending',
  `status_changed_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `actual_check_in_at` timestamp NULL DEFAULT NULL,
  `actual_check_out_at` timestamp NULL DEFAULT NULL,
  `cancelled_at` timestamp NULL DEFAULT NULL,
  `cancelled_by` bigint DEFAULT NULL,
  `cancellation_reason` varchar(255) DEFAULT NULL,
  `booking_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `created_by` bigint NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `room_rate_at_booking` decimal(10,2) NOT NULL DEFAULT '0.00',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0',
  `deleted_at` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`reservation_id`),
  UNIQUE KEY `reservation_number` (`reservation_number`),
  KEY `fk_res_user` (`created_by`),
  KEY `fk_res_cancel_user` (`cancelled_by`),
  KEY `idx_res_room_dates` (`room_id`,`check_in_date`,`check_out_date`),
  KEY `idx_res_status` (`reservation_status`),
  KEY `idx_res_deleted` (`is_deleted`),
  KEY `idx_res_guest` (`guest_id`),
  CONSTRAINT `fk_res_cancel_user` FOREIGN KEY (`cancelled_by`) REFERENCES `users` (`user_id`) ON DELETE SET NULL ON UPDATE RESTRICT,
  CONSTRAINT `fk_res_guest` FOREIGN KEY (`guest_id`) REFERENCES `guests` (`guest_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_res_room` FOREIGN KEY (`room_id`) REFERENCES `rooms` (`room_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_res_user` FOREIGN KEY (`created_by`) REFERENCES `users` (`user_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `chk_adults` CHECK ((`adults` >= 1)),
  CONSTRAINT `chk_children` CHECK ((`children` >= 0)),
  CONSTRAINT `chk_guest_total` CHECK ((`number_of_guests` >= 1)),
  CONSTRAINT `chk_res_dates` CHECK ((`check_out_date` > `check_in_date`))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `reservations`
--

LOCK TABLES `reservations` WRITE;
/*!40000 ALTER TABLE `reservations` DISABLE KEYS */;
/*!40000 ALTER TABLE `reservations` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `room_types`
--

DROP TABLE IF EXISTS `room_types`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `room_types` (
  `room_type_id` bigint NOT NULL AUTO_INCREMENT,
  `room_type_name` varchar(50) NOT NULL,
  `description` text,
  `base_price` decimal(10,2) NOT NULL,
  `max_occupancy` int NOT NULL DEFAULT '2',
  `amenities` text,
  `is_active` tinyint(1) NOT NULL DEFAULT '1',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`room_type_id`),
  UNIQUE KEY `room_type_name` (`room_type_name`),
  KEY `idx_roomtype_active` (`is_active`),
  CONSTRAINT `chk_base_price` CHECK ((`base_price` > 0)),
  CONSTRAINT `chk_max_occupancy` CHECK ((`max_occupancy` >= 1))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `room_types`
--

LOCK TABLES `room_types` WRITE;
/*!40000 ALTER TABLE `room_types` DISABLE KEYS */;
/*!40000 ALTER TABLE `room_types` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `rooms`
--

DROP TABLE IF EXISTS `rooms`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `rooms` (
  `room_id` bigint NOT NULL AUTO_INCREMENT,
  `room_number` varchar(20) NOT NULL,
  `room_type_id` bigint NOT NULL,
  `floor_number` int DEFAULT NULL,
  `view_type` enum('ocean','garden','city','pool') NOT NULL DEFAULT 'garden',
  `status` enum('available','occupied','maintenance','out_of_service') NOT NULL DEFAULT 'available',
  `is_active` tinyint(1) NOT NULL DEFAULT '1',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`room_id`),
  UNIQUE KEY `room_number` (`room_number`),
  KEY `idx_rooms_status` (`status`),
  KEY `idx_rooms_type` (`room_type_id`),
  KEY `idx_rooms_active` (`is_active`),
  CONSTRAINT `fk_rooms_room_type` FOREIGN KEY (`room_type_id`) REFERENCES `room_types` (`room_type_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `rooms`
--

LOCK TABLES `rooms` WRITE;
/*!40000 ALTER TABLE `rooms` DISABLE KEYS */;
/*!40000 ALTER TABLE `rooms` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `services`
--

DROP TABLE IF EXISTS `services`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `services` (
  `service_id` bigint NOT NULL AUTO_INCREMENT,
  `service_name` varchar(100) NOT NULL,
  `service_description` text,
  `unit_price` decimal(10,2) NOT NULL,
  `service_category` enum('food','laundry','spa','transport','other') NOT NULL DEFAULT 'other',
  `is_active` tinyint(1) NOT NULL DEFAULT '1',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`service_id`),
  KEY `idx_services_active` (`is_active`),
  KEY `idx_services_category` (`service_category`),
  CONSTRAINT `chk_service_price` CHECK ((`unit_price` >= 0))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `services`
--

LOCK TABLES `services` WRITE;
/*!40000 ALTER TABLE `services` DISABLE KEYS */;
/*!40000 ALTER TABLE `services` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `system_config`
--

DROP TABLE IF EXISTS `system_config`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `system_config` (
  `config_id` bigint NOT NULL AUTO_INCREMENT,
  `config_key` varchar(100) NOT NULL,
  `config_value` text,
  `description` text,
  `is_protected` tinyint(1) NOT NULL DEFAULT '0',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `updated_by` bigint DEFAULT NULL,
  PRIMARY KEY (`config_id`),
  UNIQUE KEY `config_key` (`config_key`),
  KEY `fk_config_user` (`updated_by`),
  CONSTRAINT `fk_config_user` FOREIGN KEY (`updated_by`) REFERENCES `users` (`user_id`) ON DELETE SET NULL ON UPDATE RESTRICT
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `system_config`
--

LOCK TABLES `system_config` WRITE;
/*!40000 ALTER TABLE `system_config` DISABLE KEYS */;
INSERT INTO `system_config` VALUES (1,'TAX_RATE','0.10','Tax rate as decimal (e.g., 0.10 = 10%)',1,'2026-02-13 20:29:06',NULL),(2,'DEFAULT_DISCOUNT','0.00','Default discount amount',1,'2026-02-13 20:29:06',NULL);
/*!40000 ALTER TABLE `system_config` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `user_id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL,
  `password_hash` varchar(255) NOT NULL,
  `full_name` varchar(100) NOT NULL,
  `email` varchar(100) NOT NULL,
  `phone` varchar(20) DEFAULT NULL,
  `role` enum('admin','receptionist','manager') NOT NULL DEFAULT 'receptionist',
  `is_active` tinyint(1) NOT NULL DEFAULT '1',
  `last_login` timestamp NULL DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `username` (`username`),
  UNIQUE KEY `email` (`email`),
  KEY `idx_role_active` (`role`,`is_active`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Temporary view structure for view `vw_inhouse_guests`
--

DROP TABLE IF EXISTS `vw_inhouse_guests`;
/*!50001 DROP VIEW IF EXISTS `vw_inhouse_guests`*/;
SET @saved_cs_client     = @@character_set_client;
/*!50503 SET character_set_client = utf8mb4 */;
/*!50001 CREATE VIEW `vw_inhouse_guests` AS SELECT 
 1 AS `reservation_id`,
 1 AS `reservation_number`,
 1 AS `guest_name`,
 1 AS `room_number`,
 1 AS `actual_check_in_at`,
 1 AS `check_out_date`*/;
SET character_set_client = @saved_cs_client;

--
-- Temporary view structure for view `vw_monthly_revenue`
--

DROP TABLE IF EXISTS `vw_monthly_revenue`;
/*!50001 DROP VIEW IF EXISTS `vw_monthly_revenue`*/;
SET @saved_cs_client     = @@character_set_client;
/*!50503 SET character_set_client = utf8mb4 */;
/*!50001 CREATE VIEW `vw_monthly_revenue` AS SELECT 
 1 AS `revenue_month`,
 1 AS `total_revenue`,
 1 AS `total_paid`,
 1 AS `total_outstanding`,
 1 AS `total_credit`*/;
SET character_set_client = @saved_cs_client;

--
-- Temporary view structure for view `vw_outstanding_bills`
--

DROP TABLE IF EXISTS `vw_outstanding_bills`;
/*!50001 DROP VIEW IF EXISTS `vw_outstanding_bills`*/;
SET @saved_cs_client     = @@character_set_client;
/*!50503 SET character_set_client = utf8mb4 */;
/*!50001 CREATE VIEW `vw_outstanding_bills` AS SELECT 
 1 AS `bill_id`,
 1 AS `bill_number`,
 1 AS `reservation_number`,
 1 AS `guest_name`,
 1 AS `contact_number`,
 1 AS `total_amount`,
 1 AS `amount_paid`,
 1 AS `balance_due`,
 1 AS `credit_amount`,
 1 AS `payment_status`,
 1 AS `bill_date`*/;
SET character_set_client = @saved_cs_client;

--
-- Temporary view structure for view `vw_room_occupancy`
--

DROP TABLE IF EXISTS `vw_room_occupancy`;
/*!50001 DROP VIEW IF EXISTS `vw_room_occupancy`*/;
SET @saved_cs_client     = @@character_set_client;
/*!50503 SET character_set_client = utf8mb4 */;
/*!50001 CREATE VIEW `vw_room_occupancy` AS SELECT 
 1 AS `room_id`,
 1 AS `room_number`,
 1 AS `status`,
 1 AS `reservation_id`,
 1 AS `reservation_number`,
 1 AS `check_in_date`,
 1 AS `check_out_date`,
 1 AS `nights`,
 1 AS `reservation_status`*/;
SET character_set_client = @saved_cs_client;

--
-- Temporary view structure for view `vw_unbilled_services`
--

DROP TABLE IF EXISTS `vw_unbilled_services`;
/*!50001 DROP VIEW IF EXISTS `vw_unbilled_services`*/;
SET @saved_cs_client     = @@character_set_client;
/*!50503 SET character_set_client = utf8mb4 */;
/*!50001 CREATE VIEW `vw_unbilled_services` AS SELECT 
 1 AS `reservation_id`,
 1 AS `reservation_number`,
 1 AS `unbilled_items`,
 1 AS `unbilled_amount`*/;
SET character_set_client = @saved_cs_client;

--
-- Final view structure for view `vw_inhouse_guests`
--

/*!50001 DROP VIEW IF EXISTS `vw_inhouse_guests`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_0900_ai_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `vw_inhouse_guests` AS select `r`.`reservation_id` AS `reservation_id`,`r`.`reservation_number` AS `reservation_number`,`g`.`guest_name` AS `guest_name`,`rm`.`room_number` AS `room_number`,`r`.`actual_check_in_at` AS `actual_check_in_at`,`r`.`check_out_date` AS `check_out_date` from ((`reservations` `r` join `guests` `g` on((`r`.`guest_id` = `g`.`guest_id`))) join `rooms` `rm` on((`r`.`room_id` = `rm`.`room_id`))) where ((`r`.`reservation_status` = 'checked_in') and (`r`.`is_deleted` = false)) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `vw_monthly_revenue`
--

/*!50001 DROP VIEW IF EXISTS `vw_monthly_revenue`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_0900_ai_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `vw_monthly_revenue` AS select date_format(`b`.`bill_date`,'%Y-%m') AS `revenue_month`,sum(`b`.`total_amount`) AS `total_revenue`,sum(`b`.`amount_paid`) AS `total_paid`,sum(`b`.`balance_due`) AS `total_outstanding`,sum(`b`.`credit_amount`) AS `total_credit` from `bills` `b` group by date_format(`b`.`bill_date`,'%Y-%m') order by `revenue_month` */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `vw_outstanding_bills`
--

/*!50001 DROP VIEW IF EXISTS `vw_outstanding_bills`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_0900_ai_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `vw_outstanding_bills` AS select `b`.`bill_id` AS `bill_id`,`b`.`bill_number` AS `bill_number`,`r`.`reservation_number` AS `reservation_number`,`g`.`guest_name` AS `guest_name`,`g`.`contact_number` AS `contact_number`,`b`.`total_amount` AS `total_amount`,`b`.`amount_paid` AS `amount_paid`,`b`.`balance_due` AS `balance_due`,`b`.`credit_amount` AS `credit_amount`,`b`.`payment_status` AS `payment_status`,`b`.`bill_date` AS `bill_date` from ((`bills` `b` join `reservations` `r` on((`b`.`reservation_id` = `r`.`reservation_id`))) join `guests` `g` on((`r`.`guest_id` = `g`.`guest_id`))) where (`b`.`payment_status` in ('unpaid','partial','credit')) order by `b`.`bill_date` */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `vw_room_occupancy`
--

/*!50001 DROP VIEW IF EXISTS `vw_room_occupancy`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_0900_ai_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `vw_room_occupancy` AS select `rm`.`room_id` AS `room_id`,`rm`.`room_number` AS `room_number`,`rm`.`status` AS `status`,`r`.`reservation_id` AS `reservation_id`,`r`.`reservation_number` AS `reservation_number`,`r`.`check_in_date` AS `check_in_date`,`r`.`check_out_date` AS `check_out_date`,(to_days(`r`.`check_out_date`) - to_days(`r`.`check_in_date`)) AS `nights`,`r`.`reservation_status` AS `reservation_status` from (`reservations` `r` join `rooms` `rm` on((`r`.`room_id` = `rm`.`room_id`))) where ((`r`.`is_deleted` = false) and (`r`.`reservation_status` in ('confirmed','checked_in','checked_out'))) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `vw_unbilled_services`
--

/*!50001 DROP VIEW IF EXISTS `vw_unbilled_services`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_0900_ai_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `vw_unbilled_services` AS select `r`.`reservation_id` AS `reservation_id`,`r`.`reservation_number` AS `reservation_number`,count(`rs`.`reservation_service_id`) AS `unbilled_items`,sum(`rs`.`total_price`) AS `unbilled_amount` from (`reservations` `r` join `reservation_services` `rs` on((`rs`.`reservation_id` = `r`.`reservation_id`))) where ((`r`.`is_deleted` = false) and (`rs`.`is_billed` = false)) group by `r`.`reservation_id`,`r`.`reservation_number` */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-02-22 12:39:08
