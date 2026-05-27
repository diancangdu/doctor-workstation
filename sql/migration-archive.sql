-- 病历归档功能迁移脚本
-- 为 medical_record 表增加 status 字段，支持归档/恢复

ALTER TABLE medical_record ADD COLUMN status VARCHAR(20) DEFAULT 'active' AFTER remarks;

-- 将现有所有记录标记为 active
UPDATE medical_record SET status = 'active' WHERE status IS NULL;
