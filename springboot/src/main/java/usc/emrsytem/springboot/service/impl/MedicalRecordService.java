package usc.emrsytem.springboot.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usc.emrsytem.springboot.controller.request.AddMedicalRecordRequest;
import usc.emrsytem.springboot.controller.request.RecordRequest;
import usc.emrsytem.springboot.entity.AuditLog;
import usc.emrsytem.springboot.entity.MedicalRecord;
import usc.emrsytem.springboot.exception.ServiceException;
import usc.emrsytem.springboot.mapper.AuditLogMapper;
import usc.emrsytem.springboot.mapper.MedicalRecordMapper;
import usc.emrsytem.springboot.service.IMedicalRecordService;
import usc.emrsytem.springboot.utils.ExcelUtil;

import java.util.List;

@Service
public class MedicalRecordService implements IMedicalRecordService {
    @Autowired
    MedicalRecordMapper medicalRecordMapper;
    @Autowired
    AuditLogMapper auditLogMapper;
    @Autowired
    ExcelUtil excelUtil;

    @Override
    @Transactional
    public int addMedicalRecord(AddMedicalRecordRequest request) {
        MedicalRecord medicalRecord = new MedicalRecord();
        BeanUtils.copyProperties(request, medicalRecord);
        return medicalRecordMapper.addMedicalRecord(medicalRecord);
    }

    @Override
    public Object listMedicalRecord(RecordRequest request) {
        try {
            PageHelper.startPage(request.getPageNum(), request.getPageSize());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        List<MedicalRecord> records = medicalRecordMapper.listPrescription(request);
        return new PageInfo<>(records);
    }

    @Override
    public MedicalRecord getById(Integer recordId) {
        return medicalRecordMapper.getById(recordId);
    }

    @Override
    public int updateMedicalRecord(MedicalRecord medicalRecord) {
        return medicalRecordMapper.updateMedicalRecord(medicalRecord);
    }

    @Override
    public int deleteMedicalRecord(Integer recordId) {
        return medicalRecordMapper.deleteMedicalRecord(recordId);
    }

    @Override
    public int archiveMedicalRecord(Integer recordId) {
        MedicalRecord record = medicalRecordMapper.getById(recordId);
        if (record != null) {
            String path = excelUtil.exportToCsv(record, "患者ID:" + record.getPatientId(), "医生ID:" + record.getDoctorId());
            if (path != null) {
                String oldRemarks = record.getRemarks() != null ? record.getRemarks() : "";
                record.setRemarks(oldRemarks + (oldRemarks.isEmpty() ? "" : " ") + "[归档文件:" + path + "]");
                medicalRecordMapper.updateMedicalRecord(record);
            }
        }
        return medicalRecordMapper.archiveMedicalRecord(recordId);
    }

    @Override
    public int restoreMedicalRecord(Integer recordId) {
        MedicalRecord record = medicalRecordMapper.getById(recordId);
        if (record != null) {
            excelUtil.restoreToCsv(record, "患者ID:" + record.getPatientId(), "医生ID:" + record.getDoctorId());
        }
        return medicalRecordMapper.restoreMedicalRecord(recordId);
    }

    @Override
    public int confirmMedicalRecord(Integer recordId, Integer userId, String username) {
        MedicalRecord record = medicalRecordMapper.getById(recordId);
        if (record == null) throw new ServiceException("病历不存在");
        if (record.getDiagnosis() == null || record.getDiagnosis().trim().isEmpty())
            throw new ServiceException("请填写诊断后再确认");
        if (record.getPrescription() == null || record.getPrescription().trim().isEmpty())
            throw new ServiceException("请填写处方后再确认");
        int result = medicalRecordMapper.confirmMedicalRecord(recordId);
        logAudit(recordId, userId, username, "确认病历", null);
        return result;
    }

    @Override
    public int voidMedicalRecord(Integer recordId, String voidReason, Integer userId, String username) {
        int result = medicalRecordMapper.voidMedicalRecord(recordId, voidReason);
        logAudit(recordId, userId, username, "作废病历", "原因: " + voidReason);
        return result;
    }

    @Override
    public int supplementMedicalRecord(Integer recordId, String supplement, Integer userId, String username) {
        MedicalRecord record = medicalRecordMapper.getById(recordId);
        if (record == null) throw new ServiceException("病历不存在");
        String newSupplement = (record.getSupplement() != null ? record.getSupplement() + "\n" : "") + supplement;
        record.setSupplement(newSupplement);
        int result = medicalRecordMapper.updateMedicalRecord(record);
        logAudit(recordId, userId, username, "补增医嘱", supplement);
        return result;
    }

    @Override
    public Object getAuditLogs(Integer recordId) {
        return auditLogMapper.listByRecordId(recordId);
    }

    private void logAudit(Integer recordId, Integer userId, String username, String action, String detail) {
        AuditLog log = new AuditLog();
        log.setRecordId(recordId);
        log.setUserId(userId);
        log.setUsername(username);
        log.setAction(action);
        log.setDetail(detail);
        auditLogMapper.insert(log);
    }
}
