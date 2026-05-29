package usc.doctor_workstation_system.springboot.controller.request;

import lombok.Data;

@Data
public class AddPrescriptionRequest {
    private Integer DoctorUserId;
    private String medicationName;
    private String dosage;
    private String frequency;
    private String duration;
    private String instructions;
    private String remarks;
}
