package usc.doctor_workstation_system.springboot.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import usc.doctor_workstation_system.springboot.controller.request.ChangePasswordRequest;
import usc.doctor_workstation_system.springboot.controller.request.LoginRequest;
import usc.doctor_workstation_system.springboot.controller.request.PasswordRequest;
import usc.doctor_workstation_system.springboot.controller.request.UserPageRequest;
import usc.doctor_workstation_system.springboot.entity.Admin;
import usc.doctor_workstation_system.springboot.entity.Doctor;
import usc.doctor_workstation_system.springboot.entity.Patient;
import usc.doctor_workstation_system.springboot.entity.User;

import java.util.List;

// 操作数据库表user的类
@Mapper
public interface UserMapper {
    // 注解方式
/*    @Select("select * from users")
    List<User> listUsers();*/

    // 查询所有用户
    List<User> selectAllUsers();
    // 根据条件查询用户
    List<User> listByCondition(UserPageRequest userPageRequest);
    // 查询所有角色为患者的用户
    List<Patient> listPatientUsers(UserPageRequest userPageRequest);
    // 查询所有角色为医生的用户
    List<Doctor> listDoctorUsers();
    // 查询所有角色为管理员的用户
    List<Admin> listAdminUsers();
    // 根据id查询用户
    User getUserId (Integer integer);
    // 根据id查询病人
    Patient getPatientById(Integer integer);
    // 根据id查询医生
    Doctor getDoctorById(Integer integer);
    // 根据手机号和密码查询
    User getByPhoneAndPassword(@Param("phoneNumber") String phoneNumber, @Param("password") String password);
    Admin getAdminById(Integer userId);

    // 添加用户
    int addUser(User user);
    // 添加病人
    int addPatient(Patient patient);
    // 添加医生
    int addDoctor(Doctor doctor);
    // 添加管理员
    int addAdmin(Admin admin);

//    void deleteByIds(int[] ids);

    // 删除用户
    int deleteUserByRole(User user);
    int deleteFromUser(User user);

    // 修改用户
    int updateUser(User user);
    int updatePatient(Patient patient);
    int updateDoctor(Doctor doctor);
    int updateAdmin(Admin admin);

    // 登录
    // 根据手机号和密码查询用户
    User getByPhoneAndPassword(LoginRequest loginRequest);
    // 根据手机号查询用户
    User getByPhone(LoginRequest loginRequest);
    // 修改最后登录时间
    int updateLastLoginTime(User user);

    @Select("select * from users where username=#{username}")
    List<User> selectByUsername(String username);


    int updatePassword(PasswordRequest passwordRequest);


    int changePassword(ChangePasswordRequest request);
}
