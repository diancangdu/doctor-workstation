-- 病历功能增强迁移脚本
-- 包含归档、病历字段扩展、审核锁定、补增等功能所需的所有 DDL 变更

-- 1. 归档 status 字段
ALTER TABLE medical_record ADD COLUMN status VARCHAR(20) DEFAULT 'active' AFTER remarks;
UPDATE medical_record SET status = 'active' WHERE status IS NULL;

-- 2. 病历结构化字段
ALTER TABLE medical_record ADD COLUMN chief_complaint VARCHAR(500) AFTER status;
ALTER TABLE medical_record ADD COLUMN present_illness TEXT AFTER chief_complaint;
ALTER TABLE medical_record ADD COLUMN past_history TEXT AFTER present_illness;
ALTER TABLE medical_record ADD COLUMN physical_exam TEXT AFTER past_history;

-- 3. 审核锁定
ALTER TABLE medical_record ADD COLUMN record_status VARCHAR(20) DEFAULT 'draft' AFTER physical_exam;

-- 4. 作废原因 + 补增医嘱 + 费用
ALTER TABLE medical_record ADD COLUMN void_reason VARCHAR(500) AFTER record_status;
ALTER TABLE medical_record ADD COLUMN supplement TEXT AFTER void_reason;
ALTER TABLE medical_record ADD COLUMN total_fee DECIMAL(10,2) DEFAULT 0 AFTER supplement;

-- 5. 操作日志表
CREATE TABLE IF NOT EXISTS audit_log (
    log_id INT AUTO_INCREMENT PRIMARY KEY,
    record_id INT NOT NULL,
    user_id INT NOT NULL,
    username VARCHAR(50),
    action VARCHAR(30),
    detail TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
