package usc.doctor_workstation_system.springboot.controller.request;

import lombok.Data;

@Data
public class ChangePasswordRequest {
    private String oldPassword;
    private String newPassword;
    private String confirmPassword;
    private Integer userId;
}
