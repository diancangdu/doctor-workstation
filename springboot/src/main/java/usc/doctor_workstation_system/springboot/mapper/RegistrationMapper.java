package usc.doctor_workstation_system.springboot.mapper;

import org.apache.ibatis.annotations.Mapper;
import usc.doctor_workstation_system.springboot.entity.Registration;
import java.util.List;

@Mapper
public interface RegistrationMapper {
    int addRegistration(Registration registration);
    int startConsultation(Integer registrationId);
    int completeConsultation(Integer registrationId);
    int cancelRegistration(Integer registrationId);
    List<Registration> listRegistrations(Registration registration);
    List<Registration> getDoctorQueue(Integer doctorId);
    Registration getById(Integer registrationId);
}
