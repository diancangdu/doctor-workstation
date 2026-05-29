package usc.doctor_workstation_system.springboot.controller.request;

import lombok.Data;

import java.util.List;

@Data
public class DoctorPatientRequest {
    private Integer doctorValue; // 对应医生的userId
    private List<Integer> patientValues; // 对应患者的userId
}
