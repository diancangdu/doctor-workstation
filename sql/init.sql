-- ============================================
-- EMRS 数据库初始化脚本
-- 使用方法：mysql -uroot -p123456 < init.sql
-- ============================================

CREATE DATABASE IF NOT EXISTS emrs DEFAULT CHARSET utf8mb4;
USE emrs;

-- 用户表
CREATE TABLE IF NOT EXISTS user (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL,
    email VARCHAR(100),
    phone_number VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_login_at TIMESTAMP NULL,
    status BOOLEAN DEFAULT TRUE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 医生表
CREATE TABLE IF NOT EXISTS doctor (
    doctor_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    department VARCHAR(100),
    gender VARCHAR(10),
    specialty VARCHAR(200),
    experience_years INT,
    qualification VARCHAR(200),
    title VARCHAR(100),
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES user(user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 患者表
CREATE TABLE IF NOT EXISTS patient (
    patient_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    gender VARCHAR(10),
    date_of_birth DATE,
    address VARCHAR(255),
    emergency_contact VARCHAR(50),
    contact_phone VARCHAR(20),
    medical_history TEXT,
    allergies TEXT,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES user(user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 管理员表
CREATE TABLE IF NOT EXISTS admin (
    admin_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    admin_level VARCHAR(50),
    department VARCHAR(100),
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES user(user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 医患关系表
CREATE TABLE IF NOT EXISTS doctor_patient (
    doctor_patient_id INT AUTO_INCREMENT PRIMARY KEY,
    doctor_id INT NOT NULL,
    patient_id INT NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 病历表
CREATE TABLE IF NOT EXISTS medical_record (
    record_id INT AUTO_INCREMENT PRIMARY KEY,
    patient_id INT NOT NULL,
    doctor_id INT NOT NULL,
    prescription_id INT,
    diagnosis TEXT,
    symptoms TEXT,
    chart TEXT,
    remarks TEXT,
    prescription TEXT,
    status VARCHAR(20) DEFAULT 'active',
    chief_complaint VARCHAR(500),
    present_illness TEXT,
    past_history TEXT,
    physical_exam TEXT,
    record_status VARCHAR(20) DEFAULT 'draft',
    void_reason VARCHAR(500),
    supplement TEXT,
    total_fee DECIMAL(10,2) DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 处方表
CREATE TABLE IF NOT EXISTS prescription (
    prescription_id INT AUTO_INCREMENT PRIMARY KEY,
    doctor_id INT NOT NULL,
    medication_name VARCHAR(200),
    dosage VARCHAR(100),
    frequency VARCHAR(100),
    duration VARCHAR(100),
    instructions TEXT,
    remarks TEXT,
    unit_price DECIMAL(10,2) DEFAULT 0,
    total_price DECIMAL(10,2) DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 诊断模板表
CREATE TABLE IF NOT EXISTS diagnosis_template (
    template_id INT AUTO_INCREMENT PRIMARY KEY,
    department VARCHAR(100),
    diagnosis_name VARCHAR(200),
    symptoms TEXT,
    prescription_hint TEXT,
    usage_count INT DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 药品信息表
CREATE TABLE IF NOT EXISTS drug_info (
    drug_id INT AUTO_INCREMENT PRIMARY KEY,
    drug_name VARCHAR(200),
    specification VARCHAR(100),
    unit_price DECIMAL(10,2),
    dosage_hint VARCHAR(200),
    frequency_hint VARCHAR(100),
    indications TEXT,
    contraindications TEXT,
    adverse_reactions TEXT,
    interactions TEXT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 操作日志表
CREATE TABLE IF NOT EXISTS audit_log (
    log_id INT AUTO_INCREMENT PRIMARY KEY,
    record_id INT NOT NULL,
    user_id INT NOT NULL,
    username VARCHAR(50),
    action VARCHAR(30),
    detail TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 挂号表
CREATE TABLE IF NOT EXISTS registration (
    registration_id INT AUTO_INCREMENT PRIMARY KEY,
    patient_id INT NOT NULL,
    patient_user_id INT NOT NULL,
    doctor_id INT NOT NULL,
    department VARCHAR(100),
    chief_complaint VARCHAR(500),
    queue_number INT DEFAULT 1,
    status VARCHAR(20) DEFAULT 'waiting',
    registration_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    consultation_time TIMESTAMP NULL,
    completion_time TIMESTAMP NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 初始账号（密码均为 admin123 的 BCrypt 哈希）
INSERT INTO user (username, password, phone_number, email, role, status) VALUES
('管理员', '$2a$10$AkEXt6eXtxDiRh2MOxkZIeus/Ge6ywbNuYBeueZWGOdA7TaCsQlci', '13800000000', 'admin@example.com', 'admin', TRUE),
('张医生', '$2a$10$AkEXt6eXtxDiRh2MOxkZIeus/Ge6ywbNuYBeueZWGOdA7TaCsQlci', '13800000001', 'doctor@example.com', 'doctor', TRUE),
('李病人', '$2a$10$AkEXt6eXtxDiRh2MOxkZIeus/Ge6ywbNuYBeueZWGOdA7TaCsQlci', '13800000002', 'patient@example.com', 'patient', TRUE);

INSERT INTO admin (user_id, admin_level, department) VALUES (1, '超级管理员', '信息科');
INSERT INTO doctor (user_id, department, gender, specialty, qualification, title, experience_years) VALUES (2, '内科', 'male', '心内科', '主任医师', '主任医师', 15);
INSERT INTO patient (user_id, gender, date_of_birth, address, emergency_contact, contact_phone, medical_history, allergies) VALUES (3, 'female', '1990-05-15', '广州市天河区', '李四', '13800000003', '高血压', '青霉素');
