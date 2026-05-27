package usc.emrsytem.springboot.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import usc.emrsytem.springboot.controller.dto.LoginDTO;
import usc.emrsytem.springboot.controller.request.ChangePasswordRequest;
import usc.emrsytem.springboot.controller.request.LoginRequest;
import usc.emrsytem.springboot.controller.request.PasswordRequest;
import usc.emrsytem.springboot.controller.request.UserPageRequest;
import usc.emrsytem.springboot.entity.Admin;
import usc.emrsytem.springboot.entity.Doctor;
import usc.emrsytem.springboot.entity.Patient;
import usc.emrsytem.springboot.entity.User;
import usc.emrsytem.springboot.exception.ServiceException;
import usc.emrsytem.springboot.mapper.DoctorPatientMapper;
import usc.emrsytem.springboot.mapper.UserMapper;
import usc.emrsytem.springboot.service.IUserService;
import usc.emrsytem.springboot.utils.PasswordUtil;
import usc.emrsytem.springboot.utils.TokenUtils;

import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class UserService implements IUserService {
    @Autowired
    UserMapper userMapper;
    @Autowired
    DoctorPatientMapper doctorPatientMapper;

    //查询所有用户
    @Override
    public List<User> userList() {
        return userMapper.selectAllUsers();
    }
    // 分页查询
    @Override
    public Object page(UserPageRequest userPageRequest){
        try {
            PageHelper.startPage(userPageRequest.getPageNum(), userPageRequest.getPageSize());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        List<User> users = userMapper.listByCondition(userPageRequest);
        return new PageInfo<>(users);
    }
    // 查询所有患者信息
    @Override
    public Object patientList(UserPageRequest userPageRequest) {
        try {
            PageHelper.startPage(userPageRequest.getPageNum(), userPageRequest.getPageSize());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        List<Patient> patients = userMapper.listPatientUsers(userPageRequest);
        return new PageInfo<>(patients);
    }
    // 查询所有医生信息
    @Override
    public List<Doctor> doctorList() {
        return userMapper.listDoctorUsers();
    }
    // 查询所有管理员信息
    @Override
    public List<Admin> adminList() {
        return userMapper.listAdminUsers();
    }
    // 根据id查询用户
    @Override
    public User getById(Integer integer) {
        return userMapper.getUserId(integer);
    }
    // 根据id查询医生
    @Override
    public Doctor getDoctorById(Integer userId) {
        return userMapper.getDoctorById(userId);
    }
    // 根据id查询患者
    @Override
    public Patient getPatientById(Integer userId) {
        return userMapper.getPatientById(userId);
    }

    @Override
    public Object getByUserId(Integer userId) {
        if (userMapper.getUserId(userId) == null) {
            return new ServiceException("用户不存在");
        }
        User user =  userMapper.getUserId(userId);
        if (user.getRole().equals("patient")) {
            return userMapper.getPatientById(userId);
        } else if (user.getRole().equals("doctor")) {
            return userMapper.getDoctorById(userId);
        } else {
            return userMapper.getAdminById(userId);
        }
    }


//    @Override
//    public int addUser(User user) {
//        return userMapper.addUser(user);
//    }

    // 添加病人
    @Override
    @Transactional
    public int addPatient(Patient patient) {
        User user = patient.getUser();

        // 判断手机号是否存在
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setPhoneNumber(user.getPhoneNumber());
        if (userMapper.getByPhone(loginRequest) != null) {
            throw new ServiceException("手机号已存在");
        }

        // 加密密码
        String encodedPassword = PasswordUtil.encode(user.getPassword());
        user.setPassword(encodedPassword);

        // 存入数据库
        userMapper.addUser(user);
        patient.setUserId(user.getUserId());
        userMapper.addPatient(patient);
        return 0;
    }
    // 添加医生
    @Override
    @Transactional
    public int addDoctor(Doctor doctor) {
        User user = doctor.getUser();

        // 判断手机号是否存在
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setPhoneNumber(user.getPhoneNumber());
        if (userMapper.getByPhone(loginRequest) != null) {
            throw new ServiceException("手机号已存在");
        }

        // 加密密码
        String encodedPassword = PasswordUtil.encode(user.getPassword());
        user.setPassword(encodedPassword);

        userMapper.addUser(user);
        doctor.setUserId(user.getUserId());
        userMapper.addDoctor(doctor);
        return 0;
    }
    // 添加管理员
    @Override
    @Transactional
    public int addAdmin(Admin admin) {
        User user = admin.getUser();

        // 判断手机号是否存在
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setPhoneNumber(user.getPhoneNumber());
        if (userMapper.getByPhone(loginRequest) != null) {
            throw new ServiceException("手机号已存在");
        }

        // 加密密码
        String encodedPassword = PasswordUtil.encode(user.getPassword());
        user.setPassword(encodedPassword);

        userMapper.addUser(user);
        admin.setUserId(user.getUserId());
        userMapper.addAdmin(admin);
        return 0;
    }

    // 删除用户
    @Override
    @Transactional
    public int deleteUserById(User user) {
        switch (user.getRole()) {
            case "患者" -> user.setRole("patient");
            case "医生" -> user.setRole("doctor");
            case "管理员" -> user.setRole("admin");
        }

        if(Objects.equals(user.getRole(), "admin")) {
            throw new ServiceException("管理员账号不可删除");
        }

        if(Objects.equals(user.getRole(), "patient")) {
            doctorPatientMapper.deleteByPatientUserId(user.getUserId());
        }
        userMapper.deleteUserByRole(user);
        userMapper.deleteFromUser(user);
        return 0;
    }

    // 更新用户信息
    @Override
    @Transactional
    public int updateUser(User user) {

        // 判断手机号是否存在
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setPhoneNumber(user.getPhoneNumber());
        if (userMapper.getByPhone(loginRequest) != null) {
            throw new ServiceException("手机号已存在");
        }

        userMapper.updateUser(user);
        return 0;
    }
    // 更新病人信息
    @Override
    @Transactional
    public int updatePatient(Patient patient) {
        User user = patient.getUser();

        // 判断手机号是否存在
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setPhoneNumber(user.getPhoneNumber());
        User u = userMapper.getByPhone(loginRequest);
        if (userMapper.getByPhone(loginRequest) != null && !Objects.equals(u.getUserId(), user.getUserId())) {
            throw new ServiceException("手机号已存在");
        }

        if(Objects.equals(user.getRole(), "患者")) {
            user.setRole("patient");
        }
        if(Objects.equals(patient.getGender(), "男")) {
            patient.setGender("male");
        } else {
            patient.setGender("female");
        }

        // 加密密码
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            String encodedPassword = PasswordUtil.encode(user.getPassword());
            user.setPassword(encodedPassword);
        }

        userMapper.updateUser(user);
        userMapper.updatePatient(patient);
        return 0;
    }
    // 更新医生信息
    @Override
    @Transactional
    public int updateDoctor(Doctor doctor) {
        User user = doctor.getUser();

        // 判断手机号是否存在
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setPhoneNumber(user.getPhoneNumber());
        User u = userMapper.getByPhone(loginRequest);
        if (userMapper.getByPhone(loginRequest) != null && !Objects.equals(u.getUserId(), user.getUserId())) {
            throw new ServiceException("手机号已存在");
        }

        if(Objects.equals(user.getRole(), "医生")) {
            user.setRole("doctor");
        }
        if(Objects.equals(doctor.getGender(), "男") || Objects.equals(doctor.getGender(), "male")) {
            doctor.setGender("male");
        } else {
            doctor.setGender("female");
        }

        // 加密密码
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            String encodedPassword = PasswordUtil.encode(user.getPassword());
            user.setPassword(encodedPassword);
        }


        userMapper.updateUser(user);
        userMapper.updateDoctor(doctor);
        return 0;
    }
    // 更新管理员信息
    @Override
    @Transactional
    public int updateAdmin(Admin admin) {
        User user = admin.getUser();

        // 判断手机号是否存在
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setPhoneNumber(user.getPhoneNumber());
        User u = userMapper.getByPhone(loginRequest);
        if (u != null && !Objects.equals(u.getUserId(), user.getUserId())) {
            throw new ServiceException("手机号已存在");
        }

        if(Objects.equals(user.getRole(), "管理员")) {
            user.setRole("admin");
        }
        if(Objects.equals(admin.getAdminLevel(),"超级管理员") || Objects.equals(admin.getAdminLevel(),"super")) {
            admin.setAdminLevel("super");
        } else {
            admin.setAdminLevel("normal");
        }

        // 加密密码
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            String encodedPassword = PasswordUtil.encode(user.getPassword());
            user.setPassword(encodedPassword);
        }

        userMapper.updateUser(user);
        userMapper.updateAdmin(admin);
        return 0;
    }

    // 登录
    @Override
    @Transactional
    public LoginDTO login(LoginRequest loginRequest) {
        User user = userMapper.getByPhone(loginRequest);
        if (user == null) {
            throw new ServiceException("用户不存在");
        }

        // 验证密码是否匹配
        boolean isPasswordMatch = PasswordUtil.matches(loginRequest.getPassword(), user.getPassword());
        if (!isPasswordMatch) {
            throw new ServiceException("用户名或密码错误");
        }

        // 判断状态是否可用
        if (!user.isStatus()) {
            throw new ServiceException("用户已被禁用，请联系管理员");
        }

        // 修改最后登录时间
        Timestamp currentTime = new Timestamp(System.currentTimeMillis());
        User tempUser = new User();
        tempUser.setUserId(user.getUserId());
        tempUser.setLastLoginAt(currentTime);
        userMapper.updateLastLoginTime(tempUser);


        LoginDTO loginDTO = new LoginDTO();
        BeanUtils.copyProperties(user, loginDTO);

        String token = TokenUtils.genToken(String.valueOf(user.getUserId()), user.getPassword());
        loginDTO.setToken(token);
        return loginDTO;
    }

    // 修改密码
    @Override
    public void changePassword(PasswordRequest request) {
        // 新密码加密
        request.setNewPassword(PasswordUtil.encode(request.getNewPassword()));

        int count = userMapper.updatePassword(request);
        if (count <= 0) {
            throw new ServiceException("修改密码失败");
        }
    }

    @Override
    public int changePassword(ChangePasswordRequest request) {
        // 检查新旧密码是否为空
        if (StringUtils.isEmpty(request.getOldPassword()) || StringUtils.isEmpty(request.getNewPassword())) {
            throw new ServiceException("密码不能为空");
        }
        // 比对新旧密码是否一致
        if (request.getNewPassword().equals(request.getOldPassword())) {
            throw new ServiceException("新密码不能与旧密码相同");
        }
        // 检查旧密码是否正确
        Integer userId = request.getUserId();
        if (userId == null) {
            throw new ServiceException("无法获取用户，请重试");
        }
        if (userMapper.getUserId(userId) == null) {
            throw new ServiceException("用户不存在");
        }

        User user = userMapper.getUserId(userId);

        // 验证密码是否匹配
        boolean isPasswordMatch = PasswordUtil.matches(request.getOldPassword(), user.getPassword());
        if (!isPasswordMatch) {
            throw new ServiceException("旧密码不正确");
        }
        // 新密码加密
        String newPassword = PasswordUtil.encode(request.getNewPassword());
        // 更新用户密码
        request.setNewPassword(newPassword);
        return userMapper.changePassword(request);
    }

}
