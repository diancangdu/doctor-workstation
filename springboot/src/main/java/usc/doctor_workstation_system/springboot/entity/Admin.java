package usc.doctor_workstation_system.springboot.entity;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class Admin {
    private Integer adminId;
    private Integer userId;
    private String adminLevel;
    private String department;
    private Timestamp updatedAt;
    private User user;
}
