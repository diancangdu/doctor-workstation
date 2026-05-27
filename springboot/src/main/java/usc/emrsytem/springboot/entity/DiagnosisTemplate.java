package usc.emrsytem.springboot.entity;

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
