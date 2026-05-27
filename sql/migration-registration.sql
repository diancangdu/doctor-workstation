-- 挂号模块迁移脚本

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
