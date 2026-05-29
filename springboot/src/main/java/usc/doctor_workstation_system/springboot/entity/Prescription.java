package usc.doctor_workstation_system.springboot.entity;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class Prescription {
    private Integer prescriptionId;
    private Integer doctorId;
    private String medicationName;
    private String dosage;
    private String frequency;
    private String duration;
    private String instructions;
    private String remarks;
    private java.math.BigDecimal unitPrice;
    private java.math.BigDecimal totalPrice;
    private Timestamp createdAt;
}
