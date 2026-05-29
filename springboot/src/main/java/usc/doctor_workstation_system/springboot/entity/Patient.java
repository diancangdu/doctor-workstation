package usc.doctor_workstation_system.springboot.entity;

import lombok.Data;

import java.sql.Timestamp;
import java.util.Date;

@Data
public class Patient {
    private Integer patientId;
    private Integer userId;
    private String gender;
    private Date dateOfBirth;
    private String address;
    private String emergencyContact;
    private String contactPhone;
    private String medicalHistory;
    private String allergies;
    private Timestamp updatedAt;
    private User user;
}
