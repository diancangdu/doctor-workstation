package usc.doctor_workstation_system.springboot.entity;

import lombok.Data;

@Data
public class DoctorPatient {
    private Integer doctorId;
    private Integer patientId;
    private Integer DoctorPatientId;
}
