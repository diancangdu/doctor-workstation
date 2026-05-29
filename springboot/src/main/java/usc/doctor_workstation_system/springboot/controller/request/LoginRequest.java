package usc.doctor_workstation_system.springboot.controller.request;

import lombok.Data;

@Data
public class LoginRequest {
    private String phoneNumber;
    private String password;
}
