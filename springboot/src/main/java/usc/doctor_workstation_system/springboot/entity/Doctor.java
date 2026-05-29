package usc.doctor_workstation_system.springboot.entity;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class Doctor {
    private Integer doctorId;
    private Integer userId;
    private String department;
    private String gender;
    private String specialty;
    private Integer experienceYears;
    private String qualification;
    private String title;
    private Timestamp updatedAt;
    private User user;
}
