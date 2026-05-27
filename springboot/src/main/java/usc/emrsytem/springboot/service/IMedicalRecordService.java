package usc.emrsytem.springboot.service;

import usc.emrsytem.springboot.controller.request.AddMedicalRecordRequest;
import usc.emrsytem.springboot.controller.request.RecordRequest;
import usc.emrsytem.springboot.entity.MedicalRecord;

public interface IMedicalRecordService {
    // 添加
    int addMedicalRecord(AddMedicalRecordRequest request);

    Object listMedicalRecord(RecordRequest request);

    MedicalRecord getById(Integer recordId);

    int updateMedicalRecord(MedicalRecord medicalRecord);

    int deleteMedicalRecord(Integer recordId);

    int archiveMedicalRecord(Integer recordId);

    int restoreMedicalRecord(Integer recordId);

    int confirmMedicalRecord(Integer recordId, Integer userId, String username);

    int voidMedicalRecord(Integer recordId, String voidReason, Integer userId, String username);

    int supplementMedicalRecord(Integer recordId, String supplement, Integer userId, String username);

    Object getAuditLogs(Integer recordId);
}
