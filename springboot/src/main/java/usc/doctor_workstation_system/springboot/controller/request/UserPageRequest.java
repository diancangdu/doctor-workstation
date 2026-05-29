package usc.doctor_workstation_system.springboot.controller.request;

import lombok.Data;

@Data
public class UserPageRequest extends BaseRequest{
    private String username;
    private String phoneNumber;
    private String role;
    private String email;
}
