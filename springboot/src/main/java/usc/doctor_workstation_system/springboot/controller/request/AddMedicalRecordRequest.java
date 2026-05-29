package usc.doctor_workstation_system.springboot.controller.request;

import lombok.Data;

@Data
public class AddMedicalRecordRequest {
    private Integer patientId;
    private String diagnosis;
    private String symptoms;
    private String chart;
    private Integer prescriptionId;
    private Integer doctorId;
    private String prescription;
    private String remarks;
}
