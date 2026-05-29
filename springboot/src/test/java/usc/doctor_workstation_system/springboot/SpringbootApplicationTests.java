package usc.doctor_workstation_system.springboot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import usc.doctor_workstation_system.springboot.mapper.DoctorPatientMapper;
import usc.doctor_workstation_system.springboot.mapper.UserMapper;
import usc.doctor_workstation_system.springboot.service.IDoctorPatientService;
import usc.doctor_workstation_system.springboot.service.impl.UserService;

@SpringBootTest
class SpringbootApplicationTests {

	@Autowired
	private UserMapper userMapper;
    @Autowired
    private UserService userService;
	@Autowired
	private DoctorPatientMapper doctorPatientMapper;
	@Autowired
	private IDoctorPatientService doctorPatientService;


}
