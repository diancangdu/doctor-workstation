package usc.doctor_workstation_system.springboot.controller.request;

import lombok.Data;

@Data
public class PasswordRequest {
    private String password;
    private String phoneNumber;
    private String newPassword;
}
