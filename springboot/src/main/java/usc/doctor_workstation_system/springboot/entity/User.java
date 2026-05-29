package usc.doctor_workstation_system.springboot.entity;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class User {
    private String username;
    private String password;
    private String role;
    private Integer userId;
    private String email;
    private String phoneNumber;
    private Timestamp createdAt;
    private Timestamp lastLoginAt;
    private boolean status;
}
