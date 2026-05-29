package usc.doctor_workstation_system.springboot.service;

import usc.doctor_workstation_system.springboot.entity.Registration;
import java.util.List;

public interface IRegistrationService {
    int addRegistration(Registration registration);
    int startConsultation(Integer registrationId);
    int completeConsultation(Integer registrationId);
    int cancelRegistration(Integer registrationId);
    Object listRegistrations(Registration registration);
    List<Registration> getDoctorQueue(Integer doctorId);
}
