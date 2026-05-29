package usc.doctor_workstation_system.springboot.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import usc.doctor_workstation_system.springboot.common.Result;
import usc.doctor_workstation_system.springboot.controller.request.ChangePasswordRequest;
import usc.doctor_workstation_system.springboot.controller.request.LoginRequest;
import usc.doctor_workstation_system.springboot.controller.request.PasswordRequest;
import usc.doctor_workstation_system.springboot.controller.request.UserPageRequest;
import usc.doctor_workstation_system.springboot.entity.Admin;
import usc.doctor_workstation_system.springboot.entity.Doctor;
import usc.doctor_workstation_system.springboot.entity.Patient;
import usc.doctor_workstation_system.springboot.entity.User;
import usc.doctor_workstation_system.springboot.service.IUserService;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    IUserService userService;

    // 查询
    @GetMapping("/list")
    public Result userList(){
        List<User> users = userService.userList();
        return Result.success(users);
    }
    @GetMapping("/page")
    public Result page(UserPageRequest userPageRequest) {
        userService.page(userPageRequest);
        return Result.success(userService.page(userPageRequest));
    }
    @GetMapping("/patient")
    public Result patientList(UserPageRequest userPageRequest){
        userService.patientList(userPageRequest);
        return Result.success(userService.patientList(userPageRequest));
    }
    @GetMapping("/doctor")
    public Result doctorList(){
        List<Doctor> doctor = userService.doctorList();
        return Result.success(doctor);
    }
    @GetMapping("/admin")
    public Result adminList(){
        List<Admin> admin = userService.adminList();
        return Result.success(admin);
    }

    @GetMapping("/getByUserId")
    public Result getByUserId(@RequestParam("userId") Integer userId){
        return Result.success(userService.getByUserId(userId));
    }
//    @GetMapping("getDoctorById")
//    public Result getDoctorById(@RequestParam("userId") Integer userId){
//        return Result.success(userService.getDoctorById(userId));
//
//    }
//    @GetMapping("getPatientById/{id}")
//    public Result getPatientById(@PathVariable Integer id){
//        return Result.success(userService.getPatientById(id));
//    }

//    @GetMapping("/info/{id}")
//    public Result info(@PathVariable Integer id) {
//        return Result.success(userService.selectById(id));
//    }

    // 添加
    // 添加病人
    @PostMapping("/addPatient")
    public Result addPatient(@RequestBody Patient patient) {
        userService.addPatient(patient);
        return Result.success();
    }
    // 添加医生
    @PostMapping("/addDoctor")
    public Result addDoctor(@RequestBody Doctor doctor) {
        userService.addDoctor(doctor);
        return Result.success();
    }
    // 添加管理员
    @PostMapping("/addAdmin")
    public Result addAdmin(@RequestBody Admin admin) {
        userService.addAdmin(admin);
        return Result.success();
    }

    // 删除
    // 删除用户
    @PostMapping("/deleteUserByRole")
    public Result deleteUserById(@RequestBody User user) {
        userService.deleteUserById(user);
        return Result.success();
    }

    // 修改
    @PutMapping("/updateUser")
    public Result updateUser(@RequestBody User user) {
        userService.updateUser(user);
        return Result.success();
    }
    @PostMapping("/updatePatient")
    public Result updateUser(@RequestBody Patient patient) {
        userService.updatePatient(patient);
        return Result.success();
    }
    @PostMapping("/updateDoctor")
    public Result updateDoctor(@RequestBody Doctor doctor) {
        userService.updateDoctor(doctor);
        return Result.success();
    }
    @PostMapping("/updateAdmin")
    public Result updateAdmin(@RequestBody Admin admin) {
        userService.updateAdmin(admin);
        return Result.success();
    }

    // 登录
    // 添加管理员
//    @CrossOrigin
    @PostMapping("/login")
    public Result Login(@RequestBody LoginRequest loginRequest) {
        return Result.success(userService.login(loginRequest));
    }

    // 修改密码
    @PutMapping("/updatePassword")
    public Result password(@RequestBody PasswordRequest request) {
        userService.changePassword(request);
        return Result.success();
    }

    // 个人修改密码
    @PutMapping("/changePassword")
    public Result changePassword(@RequestBody ChangePasswordRequest request) {
        userService.changePassword(request);
        return Result.success();
    }

}
