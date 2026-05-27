package usc.emrsytem.springboot.service;

import usc.emrsytem.springboot.entity.Registration;
import java.util.List;

public interface IRegistrationService {
    int addRegistration(Registration registration);
    int startConsultation(Integer registrationId);
    int completeConsultation(Integer registrationId);
    int cancelRegistration(Integer registrationId);
    Object listRegistrations(Registration registration);
    List<Registration> getDoctorQueue(Integer doctorId);
}
