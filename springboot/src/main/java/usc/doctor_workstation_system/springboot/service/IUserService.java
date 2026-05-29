package usc.doctor_workstation_system.springboot.service;

import usc.doctor_workstation_system.springboot.controller.dto.LoginDTO;
import usc.doctor_workstation_system.springboot.controller.request.ChangePasswordRequest;
import usc.doctor_workstation_system.springboot.controller.request.LoginRequest;
import usc.doctor_workstation_system.springboot.controller.request.PasswordRequest;
import usc.doctor_workstation_system.springboot.controller.request.UserPageRequest;
import usc.doctor_workstation_system.springboot.entity.Admin;
import usc.doctor_workstation_system.springboot.entity.Doctor;
import usc.doctor_workstation_system.springboot.entity.Patient;
import usc.doctor_workstation_system.springboot.entity.User;

import java.util.List;


public interface IUserService {
    // 查询
    List<User> userList();// 查询所有用户
    Object page(UserPageRequest userPageRequest);// 分页条件查询
    Object patientList(UserPageRequest userPageRequest);// 查询所有患者
    List<Doctor> doctorList();// 查询所有医生
    List<Admin> adminList();// 查询所有管理员
    Doctor getDoctorById(Integer userId);// 根据id查询医生
    Patient getPatientById(Integer userId);// 根据id查询患者
    Object getByUserId(Integer userId);

    // 添加
    int addPatient(Patient patient);
    int addDoctor(Doctor doctor);
    int addAdmin(Admin admin);

    // 删除
    int deleteUserById(User user);

    // 修改
    int updatePatient(Patient patient);
    int updateDoctor(Doctor doctor);
    int updateAdmin(Admin admin);
    int updateUser(User user);
    void changePassword(PasswordRequest request);
    int changePassword(ChangePasswordRequest request);

    LoginDTO login(LoginRequest loginRequest);

    User getById(Integer integer);

}
