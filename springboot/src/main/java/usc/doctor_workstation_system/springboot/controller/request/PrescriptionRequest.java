package usc.doctor_workstation_system.springboot.controller.request;

import lombok.Data;

@Data
public class PrescriptionRequest extends BaseRequest{
    private Integer doctorId;
    private String medicationName;
    private String remarks;
}
