package usc.doctor_workstation_system.springboot.entity;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class MedicalRecord {
    private Integer recordId;
    private Integer patientId;
    private String diagnosis;
    private String symptoms;
    private String chart;
    private Integer prescriptionId;
    private Integer doctorId;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private String prescription;
    private String remarks;
    private String status;
    private String chiefComplaint;
    private String presentIllness;
    private String pastHistory;
    private String physicalExam;
    private String recordStatus;
    private String voidReason;
    private String supplement;
    private java.math.BigDecimal totalFee;
}
