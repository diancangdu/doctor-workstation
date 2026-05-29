package usc.doctor_workstation_system.springboot.entity;

import lombok.Data;

@Data
public class DiagnosisTemplate {
    private Integer templateId;
    private String department;
    private String diagnosisName;
    private String symptoms;
    private String prescriptionHint;
    private Integer usageCount;
}
