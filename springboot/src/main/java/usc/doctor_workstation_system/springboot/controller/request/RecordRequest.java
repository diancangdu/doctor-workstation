package usc.doctor_workstation_system.springboot.controller.request;

import lombok.Data;

@Data
public class RecordRequest extends BaseRequest{
    private String diagnosis;
    private String symptoms;
    private Integer patientId;
    private Integer userId;
    private String status;
}

