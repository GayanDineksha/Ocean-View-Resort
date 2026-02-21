/* =========================================================
   Ocean View Resort – FULL FIXED MySQL 8+ Script
   - Clean re-run safe (drops DB)
   - Tables + Views + Triggers + Stored Procedures
   - Uses utf8mb4_0900_ai_ci
   ========================================================= */

-- -------------------------
-- DATABASE
-- -------------------------
DROP DATABASE IF EXISTS ocean_view_resort;
CREATE DATABASE ocean_view_resort
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_0900_ai_ci;

USE ocean_view_resort;

SET sql_safe_updates = 0;

-- =========================================================
-- TABLES (create in dependency order)
-- =========================================================

-- -------------------------
-- USERS
-- -------------------------
CREATE TABLE users (
  user_id BIGINT PRIMARY KEY AUTO_INCREMENT,
  username VARCHAR(50) NOT NULL UNIQUE,
  password_hash VARCHAR(255) NOT NULL,
  full_name VARCHAR(100) NOT NULL,
  email VARCHAR(100) NOT NULL UNIQUE,
  phone VARCHAR(20),
  role ENUM('admin','receptionist','manager') NOT NULL DEFAULT 'receptionist',
  is_active BOOLEAN NOT NULL DEFAULT TRUE,
  last_login TIMESTAMP NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  INDEX idx_role_active (role, is_active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- -------------------------
-- GUESTS
-- NOTE: UNIQUE(email) allows multiple NULLs in MySQL
-- -------------------------
CREATE TABLE guests (
  guest_id BIGINT PRIMARY KEY AUTO_INCREMENT,
  guest_name VARCHAR(100) NOT NULL,
  nic_passport VARCHAR(50) UNIQUE,
  address TEXT,
  city VARCHAR(50),
  country VARCHAR(50) DEFAULT 'Sri Lanka',
  contact_number VARCHAR(20) NOT NULL,
  email VARCHAR(100) NULL,
  date_of_birth DATE,
  guest_type ENUM('individual','corporate','vip') NOT NULL DEFAULT 'individual',

  is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
  deleted_at TIMESTAMP NULL,

  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

  UNIQUE KEY uq_guest_email (email),
  INDEX idx_contact (contact_number),
  INDEX idx_nic (nic_passport),
  INDEX idx_guest_deleted (is_deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- -------------------------
-- ROOM TYPES
-- -------------------------
CREATE TABLE room_types (
  room_type_id BIGINT PRIMARY KEY AUTO_INCREMENT,
  room_type_name VARCHAR(50) NOT NULL UNIQUE,
  description TEXT,
  base_price DECIMAL(10,2) NOT NULL,
  max_occupancy INT NOT NULL DEFAULT 2,
  amenities TEXT,
  is_active BOOLEAN NOT NULL DEFAULT TRUE,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

  CONSTRAINT chk_base_price CHECK (base_price > 0),
  CONSTRAINT chk_max_occupancy CHECK (max_occupancy >= 1),

  INDEX idx_roomtype_active (is_active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- -------------------------
-- ROOMS
-- -------------------------
CREATE TABLE rooms (
  room_id BIGINT PRIMARY KEY AUTO_INCREMENT,
  room_number VARCHAR(20) NOT NULL UNIQUE,
  room_type_id BIGINT NOT NULL,
  floor_number INT,
  view_type ENUM('ocean','garden','city','pool') NOT NULL DEFAULT 'garden',
  status ENUM('available','occupied','maintenance','out_of_service') NOT NULL DEFAULT 'available',
  is_active BOOLEAN NOT NULL DEFAULT TRUE,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

  CONSTRAINT fk_rooms_room_type
    FOREIGN KEY (room_type_id) REFERENCES room_types(room_type_id)
    ON UPDATE RESTRICT ON DELETE RESTRICT,

  INDEX idx_rooms_status (status),
  INDEX idx_rooms_type (room_type_id),
  INDEX idx_rooms_active (is_active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- -------------------------
-- RESERVATIONS
-- -------------------------
CREATE TABLE reservations (
  reservation_id BIGINT PRIMARY KEY AUTO_INCREMENT,
  reservation_number VARCHAR(30) NOT NULL UNIQUE,

  guest_id BIGINT NOT NULL,
  room_id BIGINT NOT NULL,

  check_in_date DATE NOT NULL,
  check_out_date DATE NOT NULL,

  adults INT NOT NULL DEFAULT 1,
  children INT NOT NULL DEFAULT 0,
  number_of_guests INT NOT NULL DEFAULT 1,

  special_requests TEXT,

  reservation_status ENUM('pending','confirmed','checked_in','checked_out','cancelled','no_show')
    NOT NULL DEFAULT 'pending',

  status_changed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  actual_check_in_at TIMESTAMP NULL,
  actual_check_out_at TIMESTAMP NULL,

  cancelled_at TIMESTAMP NULL,
  cancelled_by BIGINT NULL,
  cancellation_reason VARCHAR(255) NULL,

  booking_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  created_by BIGINT NOT NULL,

  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

  room_rate_at_booking DECIMAL(10,2) NOT NULL DEFAULT 0.00,

  is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
  deleted_at TIMESTAMP NULL,

  CONSTRAINT fk_res_guest
    FOREIGN KEY (guest_id) REFERENCES guests(guest_id)
    ON UPDATE RESTRICT ON DELETE RESTRICT,

  CONSTRAINT fk_res_room
    FOREIGN KEY (room_id) REFERENCES rooms(room_id)
    ON UPDATE RESTRICT ON DELETE RESTRICT,

  CONSTRAINT fk_res_user
    FOREIGN KEY (created_by) REFERENCES users(user_id)
    ON UPDATE RESTRICT ON DELETE RESTRICT,

  CONSTRAINT fk_res_cancel_user
    FOREIGN KEY (cancelled_by) REFERENCES users(user_id)
    ON UPDATE RESTRICT ON DELETE SET NULL,

  CONSTRAINT chk_res_dates CHECK (check_out_date > check_in_date),
  CONSTRAINT chk_adults CHECK (adults >= 1),
  CONSTRAINT chk_children CHECK (children >= 0),
  CONSTRAINT chk_guest_total CHECK (number_of_guests >= 1),

  INDEX idx_res_room_dates (room_id, check_in_date, check_out_date),
  INDEX idx_res_status (reservation_status),
  INDEX idx_res_deleted (is_deleted),
  INDEX idx_res_guest (guest_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- -------------------------
-- SERVICES (allow 0.00 complimentary)
-- -------------------------
CREATE TABLE services (
  service_id BIGINT PRIMARY KEY AUTO_INCREMENT,
  service_name VARCHAR(100) NOT NULL,
  service_description TEXT,
  unit_price DECIMAL(10,2) NOT NULL,
  service_category ENUM('food','laundry','spa','transport','other') NOT NULL DEFAULT 'other',
  is_active BOOLEAN NOT NULL DEFAULT TRUE,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

  CONSTRAINT chk_service_price CHECK (unit_price >= 0),

  INDEX idx_services_active (is_active),
  INDEX idx_services_category (service_category)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- -------------------------
-- RESERVATION SERVICES
-- -------------------------
CREATE TABLE reservation_services (
  reservation_service_id BIGINT PRIMARY KEY AUTO_INCREMENT,
  reservation_id BIGINT NOT NULL,
  service_id BIGINT NOT NULL,
  quantity INT NOT NULL DEFAULT 1,
  unit_price DECIMAL(10,2) NOT NULL,
  total_price DECIMAL(10,2) NOT NULL DEFAULT 0.00,
  service_date DATE NOT NULL,
  notes TEXT,
  is_billed BOOLEAN NOT NULL DEFAULT FALSE,

  CONSTRAINT fk_rs_res
    FOREIGN KEY (reservation_id) REFERENCES reservations(reservation_id)
    ON UPDATE RESTRICT ON DELETE CASCADE,

  CONSTRAINT fk_rs_service
    FOREIGN KEY (service_id) REFERENCES services(service_id)
    ON UPDATE RESTRICT ON DELETE RESTRICT,

  CONSTRAINT chk_rs_qty CHECK (quantity >= 1),
  CONSTRAINT chk_rs_total CHECK (total_price >= 0),

  INDEX idx_rs_reservation (reservation_id),
  INDEX idx_rs_date (service_date),
  INDEX idx_rs_billed (is_billed)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- -------------------------
-- SYSTEM CONFIG
-- -------------------------
CREATE TABLE system_config (
  config_id BIGINT PRIMARY KEY AUTO_INCREMENT,
  config_key VARCHAR(100) NOT NULL UNIQUE,
  config_value TEXT,
  description TEXT,
  is_protected BOOLEAN NOT NULL DEFAULT FALSE,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  updated_by BIGINT NULL,

  CONSTRAINT fk_config_user
    FOREIGN KEY (updated_by) REFERENCES users(user_id)
    ON UPDATE RESTRICT ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO system_config(config_key, config_value, description, is_protected)
VALUES
  ('TAX_RATE', '0.10', 'Tax rate as decimal (e.g., 0.10 = 10%)', TRUE),
  ('DEFAULT_DISCOUNT', '0.00', 'Default discount amount', TRUE);

-- -------------------------
-- BILLS
-- -------------------------
CREATE TABLE bills (
  bill_id BIGINT PRIMARY KEY AUTO_INCREMENT,
  bill_number VARCHAR(30) NOT NULL UNIQUE,
  reservation_id BIGINT NOT NULL UNIQUE,

  room_charges DECIMAL(10,2) NOT NULL DEFAULT 0.00,
  service_charges DECIMAL(10,2) NOT NULL DEFAULT 0.00,

  subtotal_amount DECIMAL(10,2) NOT NULL DEFAULT 0.00,
  tax_amount DECIMAL(10,2) NOT NULL DEFAULT 0.00,
  discount_amount DECIMAL(10,2) NOT NULL DEFAULT 0.00,

  total_amount DECIMAL(10,2) NOT NULL DEFAULT 0.00,
  amount_paid DECIMAL(10,2) NOT NULL DEFAULT 0.00,
  balance_due DECIMAL(10,2) NOT NULL DEFAULT 0.00,

  credit_amount DECIMAL(10,2) NOT NULL DEFAULT 0.00,
  payment_status ENUM('unpaid','partial','paid','credit') NOT NULL DEFAULT 'unpaid',

  bill_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  created_by BIGINT NOT NULL,

  CONSTRAINT fk_bill_res
    FOREIGN KEY (reservation_id) REFERENCES reservations(reservation_id)
    ON UPDATE RESTRICT ON DELETE RESTRICT,

  CONSTRAINT fk_bill_user
    FOREIGN KEY (created_by) REFERENCES users(user_id)
    ON UPDATE RESTRICT ON DELETE RESTRICT,

  CONSTRAINT chk_bill_nonneg CHECK (
    room_charges >= 0 AND service_charges >= 0 AND subtotal_amount >= 0 AND
    tax_amount >= 0 AND discount_amount >= 0 AND total_amount >= 0 AND
    amount_paid >= 0 AND balance_due >= 0 AND credit_amount >= 0
  ),

  INDEX idx_bill_status (payment_status),
  INDEX idx_bill_date (bill_date),
  INDEX idx_bill_res (reservation_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- -------------------------
-- PAYMENTS (soft delete supported)
-- -------------------------
CREATE TABLE payments (
  payment_id BIGINT PRIMARY KEY AUTO_INCREMENT,
  bill_id BIGINT NOT NULL,
  payment_method ENUM('cash','card','bank_transfer','online') NOT NULL,
  amount DECIMAL(10,2) NOT NULL,
  payment_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

  transaction_reference VARCHAR(100),
  card_last4 VARCHAR(4) NULL,
  card_auth_code VARCHAR(30) NULL,

  payment_notes TEXT,
  received_by BIGINT NOT NULL,

  is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
  deleted_at TIMESTAMP NULL,
  deleted_by BIGINT NULL,

  CONSTRAINT fk_pay_bill
    FOREIGN KEY (bill_id) REFERENCES bills(bill_id)
    ON UPDATE RESTRICT ON DELETE RESTRICT,

  CONSTRAINT fk_pay_user
    FOREIGN KEY (received_by) REFERENCES users(user_id)
    ON UPDATE RESTRICT ON DELETE RESTRICT,

  CONSTRAINT fk_pay_deleted_by
    FOREIGN KEY (deleted_by) REFERENCES users(user_id)
    ON UPDATE RESTRICT ON DELETE SET NULL,

  CONSTRAINT chk_payment_amount CHECK (amount > 0),

  INDEX idx_pay_bill (bill_id),
  INDEX idx_pay_date (payment_date),
  INDEX idx_pay_deleted (is_deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- -------------------------
-- AUDIT LOGS
-- -------------------------
CREATE TABLE audit_logs (
  log_id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NULL,
  action_type ENUM('INSERT','UPDATE','DELETE') NOT NULL,
  table_name VARCHAR(50) NOT NULL,
  record_id BIGINT NOT NULL,
  old_values JSON NULL,
  new_values JSON NULL,
  ip_address VARCHAR(45) NULL,
  action_timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

  CONSTRAINT fk_audit_user
    FOREIGN KEY (user_id) REFERENCES users(user_id)
    ON UPDATE RESTRICT ON DELETE SET NULL,

  INDEX idx_audit_entity (table_name, record_id),
  INDEX idx_audit_time (action_timestamp)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =========================================================
-- VIEWS (REPORTS)
-- =========================================================

CREATE OR REPLACE VIEW vw_outstanding_bills AS
SELECT
  b.bill_id, b.bill_number,
  r.reservation_number,
  g.guest_name, g.contact_number,
  b.total_amount, b.amount_paid, b.balance_due, b.credit_amount,
  b.payment_status, b.bill_date
FROM bills b
JOIN reservations r ON b.reservation_id = r.reservation_id
JOIN guests g ON r.guest_id = g.guest_id
WHERE b.payment_status IN ('unpaid','partial','credit')
ORDER BY b.bill_date;

CREATE OR REPLACE VIEW vw_room_occupancy AS
SELECT
  rm.room_id, rm.room_number, rm.status,
  r.reservation_id, r.reservation_number,
  r.check_in_date, r.check_out_date,
  DATEDIFF(r.check_out_date, r.check_in_date) AS nights,
  r.reservation_status
FROM reservations r
JOIN rooms rm ON r.room_id = rm.room_id
WHERE r.is_deleted = FALSE
  AND r.reservation_status IN ('confirmed','checked_in','checked_out');

CREATE OR REPLACE VIEW vw_monthly_revenue AS
SELECT
  DATE_FORMAT(b.bill_date, '%Y-%m') AS revenue_month,
  SUM(b.total_amount) AS total_revenue,
  SUM(b.amount_paid) AS total_paid,
  SUM(b.balance_due) AS total_outstanding,
  SUM(b.credit_amount) AS total_credit
FROM bills b
GROUP BY DATE_FORMAT(b.bill_date, '%Y-%m')
ORDER BY revenue_month;

CREATE OR REPLACE VIEW vw_unbilled_services AS
SELECT
  r.reservation_id, r.reservation_number,
  COUNT(rs.reservation_service_id) AS unbilled_items,
  SUM(rs.total_price) AS unbilled_amount
FROM reservations r
JOIN reservation_services rs ON rs.reservation_id = r.reservation_id
WHERE r.is_deleted = FALSE
  AND rs.is_billed = FALSE
GROUP BY r.reservation_id, r.reservation_number;

CREATE OR REPLACE VIEW vw_inhouse_guests AS
SELECT
  r.reservation_id, r.reservation_number,
  g.guest_name, rm.room_number,
  r.actual_check_in_at, r.check_out_date
FROM reservations r
JOIN guests g ON r.guest_id = g.guest_id
JOIN rooms rm ON r.room_id = rm.room_id
WHERE r.reservation_status = 'checked_in'
  AND r.is_deleted = FALSE;

-- =========================================================
-- TRIGGERS
-- =========================================================
DELIMITER //

-- clean re-run
DROP TRIGGER IF EXISTS trg_res_before_insert//
DROP TRIGGER IF EXISTS trg_res_before_update//
DROP TRIGGER IF EXISTS trg_res_after_insert_room_status//
DROP TRIGGER IF EXISTS trg_res_after_update_room_status//
DROP TRIGGER IF EXISTS trg_rs_total_ins//
DROP TRIGGER IF EXISTS trg_rs_total_upd//
DROP TRIGGER IF EXISTS trg_bills_calc_ins//
DROP TRIGGER IF EXISTS trg_bills_calc_upd//
DROP TRIGGER IF EXISTS trg_pay_after_insert//
DROP TRIGGER IF EXISTS trg_pay_after_update_softdelete//
DROP TRIGGER IF EXISTS trg_pay_after_delete//
DROP TRIGGER IF EXISTS trg_audit_res_ins//
DROP TRIGGER IF EXISTS trg_audit_res_upd//
DROP TRIGGER IF EXISTS trg_audit_pay_ins//
DROP TRIGGER IF EXISTS trg_audit_pay_upd//

-- -------------------------
-- reservations BEFORE INSERT
-- -------------------------
CREATE TRIGGER trg_res_before_insert
BEFORE INSERT ON reservations
FOR EACH ROW
BEGIN
  DECLARE v_price DECIMAL(10,2);
  DECLARE v_room_status VARCHAR(20);
  DECLARE v_room_active BOOLEAN;
  DECLARE v_max_occ INT;

  SET NEW.number_of_guests = NEW.adults + NEW.children;

  IF NEW.check_out_date <= NEW.check_in_date THEN
    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT='Check-out must be after check-in.';
  END IF;

  SELECT status, is_active INTO v_room_status, v_room_active
  FROM rooms
  WHERE room_id = NEW.room_id;

  IF v_room_active IS NULL THEN
    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT='Invalid room.';
  END IF;

  IF v_room_active = FALSE THEN
    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT='Room is inactive.';
  END IF;

  IF v_room_status IN ('maintenance','out_of_service') THEN
    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT='Room not available (maintenance/out_of_service).';
  END IF;

  SELECT rt.max_occupancy INTO v_max_occ
  FROM rooms rm
  JOIN room_types rt ON rm.room_type_id = rt.room_type_id
  WHERE rm.room_id = NEW.room_id;

  IF NEW.number_of_guests > v_max_occ THEN
    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT='Guest count exceeds max occupancy.';
  END IF;

  IF NEW.reservation_status IN ('pending','confirmed','checked_in') THEN
    IF EXISTS (
      SELECT 1
      FROM reservations r
      WHERE r.room_id = NEW.room_id
        AND r.is_deleted = FALSE
        AND r.reservation_status IN ('pending','confirmed','checked_in')
        AND NEW.check_in_date < r.check_out_date
        AND NEW.check_out_date > r.check_in_date
    ) THEN
      SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT='Room already booked for those dates.';
    END IF;
  END IF;

  SELECT rt.base_price INTO v_price
  FROM rooms rm
  JOIN room_types rt ON rm.room_type_id = rt.room_type_id
  WHERE rm.room_id = NEW.room_id;

  SET NEW.room_rate_at_booking = COALESCE(v_price,0.00);
  SET NEW.status_changed_at = CURRENT_TIMESTAMP;
END//

-- -------------------------
-- reservations BEFORE UPDATE
-- -------------------------
CREATE TRIGGER trg_res_before_update
BEFORE UPDATE ON reservations
FOR EACH ROW
BEGIN
  DECLARE v_room_status VARCHAR(20);
  DECLARE v_room_active BOOLEAN;
  DECLARE v_max_occ INT;

  SET NEW.number_of_guests = NEW.adults + NEW.children;

  IF NEW.check_out_date <= NEW.check_in_date THEN
    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT='Check-out must be after check-in.';
  END IF;

  IF NEW.reservation_status <> OLD.reservation_status THEN
    SET NEW.status_changed_at = CURRENT_TIMESTAMP;

    IF NEW.reservation_status = 'checked_in' AND NEW.actual_check_in_at IS NULL THEN
      SET NEW.actual_check_in_at = CURRENT_TIMESTAMP;
    END IF;

    IF NEW.reservation_status = 'checked_out' AND NEW.actual_check_out_at IS NULL THEN
      SET NEW.actual_check_out_at = CURRENT_TIMESTAMP;
    END IF;

    IF NEW.reservation_status = 'cancelled' AND NEW.cancelled_at IS NULL THEN
      SET NEW.cancelled_at = CURRENT_TIMESTAMP;
    END IF;
  END IF;

  IF NEW.reservation_status IN ('pending','confirmed','checked_in') THEN
    SELECT status, is_active INTO v_room_status, v_room_active
    FROM rooms
    WHERE room_id = NEW.room_id;

    IF v_room_active = FALSE THEN
      SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT='Room is inactive.';
    END IF;

    IF v_room_status IN ('maintenance','out_of_service') THEN
      SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT='Room not available (maintenance/out_of_service).';
    END IF;

    SELECT rt.max_occupancy INTO v_max_occ
    FROM rooms rm
    JOIN room_types rt ON rm.room_type_id = rt.room_type_id
    WHERE rm.room_id = NEW.room_id;

    IF NEW.number_of_guests > v_max_occ THEN
      SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT='Guest count exceeds max occupancy.';
    END IF;

    IF EXISTS (
      SELECT 1
      FROM reservations r
      WHERE r.room_id = NEW.room_id
        AND r.reservation_id <> OLD.reservation_id
        AND r.is_deleted = FALSE
        AND r.reservation_status IN ('pending','confirmed','checked_in')
        AND NEW.check_in_date < r.check_out_date
        AND NEW.check_out_date > r.check_in_date
    ) THEN
      SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT='Room already booked for those dates.';
    END IF;
  END IF;
END//

-- -------------------------
-- Room status automation
-- -------------------------
CREATE TRIGGER trg_res_after_insert_room_status
AFTER INSERT ON reservations
FOR EACH ROW
BEGIN
  IF NEW.reservation_status IN ('confirmed','checked_in') THEN
    UPDATE rooms
    SET status = CASE
      WHEN status IN ('maintenance','out_of_service') THEN status
      ELSE 'occupied'
    END
    WHERE room_id = NEW.room_id;
  END IF;
END//

CREATE TRIGGER trg_res_after_update_room_status
AFTER UPDATE ON reservations
FOR EACH ROW
BEGIN
  IF NEW.reservation_status IN ('confirmed','checked_in')
     AND OLD.reservation_status NOT IN ('confirmed','checked_in') THEN
    UPDATE rooms
    SET status = CASE
      WHEN status IN ('maintenance','out_of_service') THEN status
      ELSE 'occupied'
    END
    WHERE room_id = NEW.room_id;
  END IF;

  IF NEW.reservation_status IN ('checked_out','cancelled','no_show')
     AND OLD.reservation_status IN ('pending','confirmed','checked_in') THEN

    IF NOT EXISTS (
      SELECT 1
      FROM reservations r
      WHERE r.room_id = NEW.room_id
        AND r.is_deleted = FALSE
        AND r.reservation_status IN ('pending','confirmed','checked_in')
        AND CURDATE() < r.check_out_date
        AND CURDATE() >= r.check_in_date
    ) THEN
      UPDATE rooms
      SET status = CASE
        WHEN status IN ('maintenance','out_of_service') THEN status
        ELSE 'available'
      END
      WHERE room_id = NEW.room_id;
    END IF;
  END IF;
END//

-- -------------------------
-- reservation_services total_price
-- -------------------------
CREATE TRIGGER trg_rs_total_ins
BEFORE INSERT ON reservation_services
FOR EACH ROW
BEGIN
  SET NEW.total_price = NEW.quantity * NEW.unit_price;
END//

CREATE TRIGGER trg_rs_total_upd
BEFORE UPDATE ON reservation_services
FOR EACH ROW
BEGIN
  SET NEW.total_price = NEW.quantity * NEW.unit_price;
END//

-- -------------------------
-- bills calc (subtotal/total/balance/credit/status)
-- -------------------------
CREATE TRIGGER trg_bills_calc_ins
BEFORE INSERT ON bills
FOR EACH ROW
BEGIN
  SET NEW.subtotal_amount = NEW.room_charges + NEW.service_charges;

  SET NEW.total_amount = (NEW.subtotal_amount + NEW.tax_amount) - NEW.discount_amount;
  IF NEW.total_amount < 0 THEN SET NEW.total_amount = 0; END IF;

  IF NEW.amount_paid >= NEW.total_amount THEN
    SET NEW.balance_due = 0;
    SET NEW.credit_amount = NEW.amount_paid - NEW.total_amount;
    SET NEW.payment_status = IF(NEW.credit_amount > 0, 'credit', 'paid');
  ELSE
    SET NEW.balance_due = NEW.total_amount - NEW.amount_paid;
    SET NEW.credit_amount = 0;
    SET NEW.payment_status = IF(NEW.amount_paid > 0, 'partial', 'unpaid');
  END IF;
END//

CREATE TRIGGER trg_bills_calc_upd
BEFORE UPDATE ON bills
FOR EACH ROW
BEGIN
  SET NEW.subtotal_amount = NEW.room_charges + NEW.service_charges;

  SET NEW.total_amount = (NEW.subtotal_amount + NEW.tax_amount) - NEW.discount_amount;
  IF NEW.total_amount < 0 THEN SET NEW.total_amount = 0; END IF;

  IF NEW.amount_paid >= NEW.total_amount THEN
    SET NEW.balance_due = 0;
    SET NEW.credit_amount = NEW.amount_paid - NEW.total_amount;
    SET NEW.payment_status = IF(NEW.credit_amount > 0, 'credit', 'paid');
  ELSE
    SET NEW.balance_due = NEW.total_amount - NEW.amount_paid;
    SET NEW.credit_amount = 0;
    SET NEW.payment_status = IF(NEW.amount_paid > 0, 'partial', 'unpaid');
  END IF;
END//

-- -------------------------
-- payments -> update bills.amount_paid (then bill trigger recalcs)
-- -------------------------
CREATE TRIGGER trg_pay_after_insert
AFTER INSERT ON payments
FOR EACH ROW
BEGIN
  DECLARE v_paid DECIMAL(10,2);

  SELECT COALESCE(SUM(amount),0) INTO v_paid
  FROM payments
  WHERE bill_id = NEW.bill_id AND is_deleted = FALSE;

  UPDATE bills
  SET amount_paid = v_paid
  WHERE bill_id = NEW.bill_id;
END//

CREATE TRIGGER trg_pay_after_update_softdelete
AFTER UPDATE ON payments
FOR EACH ROW
BEGIN
  DECLARE v_paid DECIMAL(10,2);

  IF NEW.is_deleted <> OLD.is_deleted OR NEW.amount <> OLD.amount THEN
    SELECT COALESCE(SUM(amount),0) INTO v_paid
    FROM payments
    WHERE bill_id = NEW.bill_id AND is_deleted = FALSE;

    UPDATE bills
    SET amount_paid = v_paid
    WHERE bill_id = NEW.bill_id;
  END IF;
END//

-- If you ever HARD DELETE payments (optional but safe)
CREATE TRIGGER trg_pay_after_delete
AFTER DELETE ON payments
FOR EACH ROW
BEGIN
  DECLARE v_paid DECIMAL(10,2);

  SELECT COALESCE(SUM(amount),0) INTO v_paid
  FROM payments
  WHERE bill_id = OLD.bill_id AND is_deleted = FALSE;

  UPDATE bills
  SET amount_paid = v_paid
  WHERE bill_id = OLD.bill_id;
END//

-- -------------------------
-- AUDIT: reservations + payments
-- -------------------------
CREATE TRIGGER trg_audit_res_ins
AFTER INSERT ON reservations
FOR EACH ROW
BEGIN
  INSERT INTO audit_logs(user_id, action_type, table_name, record_id, old_values, new_values)
  VALUES (
    NEW.created_by, 'INSERT', 'reservations', NEW.reservation_id,
    NULL,
    JSON_OBJECT(
      'reservation_number', NEW.reservation_number,
      'guest_id', NEW.guest_id,
      'room_id', NEW.room_id,
      'check_in_date', NEW.check_in_date,
      'check_out_date', NEW.check_out_date,
      'status', NEW.reservation_status,
      'room_rate_at_booking', NEW.room_rate_at_booking
    )
  );
END//

CREATE TRIGGER trg_audit_res_upd
AFTER UPDATE ON reservations
FOR EACH ROW
BEGIN
  INSERT INTO audit_logs(user_id, action_type, table_name, record_id, old_values, new_values)
  VALUES (
    NEW.created_by, 'UPDATE', 'reservations', NEW.reservation_id,
    JSON_OBJECT(
      'status', OLD.reservation_status,
      'room_id', OLD.room_id,
      'check_in_date', OLD.check_in_date,
      'check_out_date', OLD.check_out_date,
      'is_deleted', OLD.is_deleted
    ),
    JSON_OBJECT(
      'status', NEW.reservation_status,
      'room_id', NEW.room_id,
      'check_in_date', NEW.check_in_date,
      'check_out_date', NEW.check_out_date,
      'is_deleted', NEW.is_deleted
    )
  );
END//

CREATE TRIGGER trg_audit_pay_ins
AFTER INSERT ON payments
FOR EACH ROW
BEGIN
  INSERT INTO audit_logs(user_id, action_type, table_name, record_id, old_values, new_values)
  VALUES (
    NEW.received_by, 'INSERT', 'payments', NEW.payment_id,
    NULL,
    JSON_OBJECT(
      'bill_id', NEW.bill_id,
      'amount', NEW.amount,
      'method', NEW.payment_method,
      'payment_date', NEW.payment_date,
      'is_deleted', NEW.is_deleted
    )
  );
END//

CREATE TRIGGER trg_audit_pay_upd
AFTER UPDATE ON payments
FOR EACH ROW
BEGIN
  IF NEW.is_deleted <> OLD.is_deleted THEN
    INSERT INTO audit_logs(user_id, action_type, table_name, record_id, old_values, new_values)
    VALUES (
      NEW.deleted_by, 'DELETE', 'payments', NEW.payment_id,
      JSON_OBJECT(
        'bill_id', OLD.bill_id,
        'amount', OLD.amount,
        'method', OLD.payment_method,
        'payment_date', OLD.payment_date
      ),
      NULL
    );
  END IF;
END//

DELIMITER ;

-- =========================================================
-- STORED PROCEDURES
-- =========================================================
DELIMITER //

DROP PROCEDURE IF EXISTS sp_validate_system_config//
DROP PROCEDURE IF EXISTS sp_check_room_availability//
DROP PROCEDURE IF EXISTS sp_create_reservation//
DROP PROCEDURE IF EXISTS sp_generate_bill//
DROP PROCEDURE IF EXISTS sp_add_payment//
DROP PROCEDURE IF EXISTS sp_soft_delete_payment//

CREATE PROCEDURE sp_validate_system_config()
BEGIN
  IF NOT EXISTS (SELECT 1 FROM system_config WHERE config_key='TAX_RATE') THEN
    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT='Missing required config: TAX_RATE';
  END IF;

  IF NOT EXISTS (SELECT 1 FROM system_config WHERE config_key='DEFAULT_DISCOUNT') THEN
    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT='Missing required config: DEFAULT_DISCOUNT';
  END IF;
END//

CREATE PROCEDURE sp_check_room_availability(
  IN p_room_id BIGINT,
  IN p_check_in DATE,
  IN p_check_out DATE
)
BEGIN
  IF p_check_out <= p_check_in THEN
    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT='Invalid date range.';
  END IF;

  SELECT
    CASE
      WHEN EXISTS (
        SELECT 1
        FROM reservations r
        WHERE r.room_id = p_room_id
          AND r.is_deleted = FALSE
          AND r.reservation_status IN ('pending','confirmed','checked_in')
          AND p_check_in < r.check_out_date
          AND p_check_out > r.check_in_date
      )
      THEN 0 ELSE 1
    END AS is_available;
END//

CREATE PROCEDURE sp_create_reservation(
  IN p_guest_id BIGINT,
  IN p_room_id BIGINT,
  IN p_check_in DATE,
  IN p_check_out DATE,
  IN p_adults INT,
  IN p_children INT,
  IN p_special_requests TEXT,
  IN p_created_by BIGINT,
  OUT o_reservation_id BIGINT,
  OUT o_reservation_number VARCHAR(30)
)
BEGIN
  DECLARE v_res_no VARCHAR(30);

  DECLARE EXIT HANDLER FOR SQLEXCEPTION
  BEGIN
    ROLLBACK;
    RESIGNAL;
  END;

  START TRANSACTION;

  SET v_res_no = CONCAT(
    'RES-',
    DATE_FORMAT(NOW(), '%Y%m%d'),
    '-',
    LPAD(FLOOR(RAND()*999999), 6, '0')
  );

  INSERT INTO reservations(
    reservation_number, guest_id, room_id,
    check_in_date, check_out_date,
    adults, children,
    special_requests,
    reservation_status,
    created_by
  ) VALUES (
    v_res_no, p_guest_id, p_room_id,
    p_check_in, p_check_out,
    p_adults, p_children,
    p_special_requests,
    'pending',
    p_created_by
  );

  SET o_reservation_id = LAST_INSERT_ID();
  SET o_reservation_number = v_res_no;

  COMMIT;
END//

CREATE PROCEDURE sp_generate_bill(
  IN p_reservation_id BIGINT,
  IN p_created_by BIGINT,
  OUT o_bill_id BIGINT,
  OUT o_bill_number VARCHAR(30)
)
BEGIN
  DECLARE v_bill_no VARCHAR(30);
  DECLARE v_check_in DATE;
  DECLARE v_check_out DATE;
  DECLARE v_rate DECIMAL(10,2);
  DECLARE v_nights INT;

  DECLARE v_room_charges DECIMAL(10,2);
  DECLARE v_service_charges DECIMAL(10,2);
  DECLARE v_tax_rate DECIMAL(10,4);
  DECLARE v_tax_amount DECIMAL(10,2);
  DECLARE v_discount DECIMAL(10,2);

  DECLARE EXIT HANDLER FOR SQLEXCEPTION
  BEGIN
    ROLLBACK;
    RESIGNAL;
  END;

  START TRANSACTION;

  IF EXISTS (SELECT 1 FROM bills WHERE reservation_id = p_reservation_id) THEN
    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT='Bill already exists for this reservation.';
  END IF;

  CALL sp_validate_system_config();

  SELECT check_in_date, check_out_date, room_rate_at_booking
    INTO v_check_in, v_check_out, v_rate
  FROM reservations
  WHERE reservation_id = p_reservation_id
    AND is_deleted = FALSE;

  IF v_check_in IS NULL OR v_check_out IS NULL THEN
    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT='Reservation not found or deleted.';
  END IF;

  SET v_nights = DATEDIFF(v_check_out, v_check_in);
  IF v_nights < 1 THEN
    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT='Invalid reservation dates for billing.';
  END IF;

  SET v_room_charges = v_nights * v_rate;

  SELECT COALESCE(SUM(total_price),0) INTO v_service_charges
  FROM reservation_services
  WHERE reservation_id = p_reservation_id
    AND is_billed = FALSE;

  SELECT CAST(config_value AS DECIMAL(10,4)) INTO v_tax_rate
  FROM system_config WHERE config_key='TAX_RATE';

  SELECT CAST(config_value AS DECIMAL(10,2)) INTO v_discount
  FROM system_config WHERE config_key='DEFAULT_DISCOUNT';

  SET v_tax_amount = ROUND(v_tax_rate * (v_room_charges + v_service_charges), 2);

  SET v_bill_no = CONCAT('BILL-', DATE_FORMAT(NOW(), '%Y%m%d'), '-', LPAD(FLOOR(RAND()*999999), 6, '0'));

  INSERT INTO bills(
    bill_number, reservation_id,
    room_charges, service_charges, tax_amount, discount_amount,
    created_by
  ) VALUES (
    v_bill_no, p_reservation_id,
    v_room_charges, v_service_charges, v_tax_amount, v_discount,
    p_created_by
  );

  -- mark ONLY unbilled items as billed
  UPDATE reservation_services
  SET is_billed = TRUE
  WHERE reservation_id = p_reservation_id
    AND is_billed = FALSE;

  SET o_bill_id = LAST_INSERT_ID();
  SET o_bill_number = v_bill_no;

  COMMIT;
END//

/* FIXED: p_method is VARCHAR (NOT ENUM in procedure params) */
CREATE PROCEDURE sp_add_payment(
  IN p_bill_id BIGINT,
  IN p_method VARCHAR(20),
  IN p_amount DECIMAL(10,2),
  IN p_txn_ref VARCHAR(100),
  IN p_card_last4 VARCHAR(4),
  IN p_card_auth VARCHAR(30),
  IN p_notes TEXT,
  IN p_received_by BIGINT
)
BEGIN
  DECLARE EXIT HANDLER FOR SQLEXCEPTION
  BEGIN
    ROLLBACK;
    RESIGNAL;
  END;

  START TRANSACTION;

  IF p_amount <= 0 THEN
    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT='Payment amount must be greater than 0.';
  END IF;

  IF p_method NOT IN ('cash','card','bank_transfer','online') THEN
    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT='Invalid payment method.';
  END IF;

  IF NOT EXISTS (SELECT 1 FROM bills WHERE bill_id = p_bill_id) THEN
    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT='Invalid bill.';
  END IF;

  INSERT INTO payments(
    bill_id, payment_method, amount,
    transaction_reference, card_last4, card_auth_code,
    payment_notes, received_by
  ) VALUES (
    p_bill_id, p_method, p_amount,
    p_txn_ref, p_card_last4, p_card_auth,
    p_notes, p_received_by
  );

  -- totals/status updated by triggers
  COMMIT;
END//

CREATE PROCEDURE sp_soft_delete_payment(
  IN p_payment_id BIGINT,
  IN p_deleted_by BIGINT
)
BEGIN
  DECLARE v_bill_id BIGINT;

  DECLARE EXIT HANDLER FOR SQLEXCEPTION
  BEGIN
    ROLLBACK;
    RESIGNAL;
  END;

  START TRANSACTION;

  SELECT bill_id INTO v_bill_id
  FROM payments
  WHERE payment_id = p_payment_id
    AND is_deleted = FALSE;

  IF v_bill_id IS NULL THEN
    SIGNAL SQLSTATE '45000'
      SET MESSAGE_TEXT = 'Payment not found or already deleted.';
  END IF;

  UPDATE payments
  SET is_deleted = TRUE,
      deleted_at = CURRENT_TIMESTAMP,
      deleted_by = p_deleted_by
  WHERE payment_id = p_payment_id;

  -- bill amount_paid recalculated by trg_pay_after_update_softdelete
  COMMIT;
END//

DELIMITER ;

-- =========================================================
-- QUICK CHECKS (run AFTER everything is created)
-- =========================================================
SHOW TABLES;
SHOW FULL TABLES WHERE TABLE_TYPE='VIEW';
SHOW TRIGGERS;
SHOW PROCEDURE STATUS WHERE Db = DATABASE();
