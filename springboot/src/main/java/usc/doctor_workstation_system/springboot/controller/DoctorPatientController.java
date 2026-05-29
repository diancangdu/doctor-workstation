package usc.doctor_workstation_system.springboot.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import usc.doctor_workstation_system.springboot.common.Result;
import usc.doctor_workstation_system.springboot.controller.request.DoctorPatientRequest;
import usc.doctor_workstation_system.springboot.service.IDoctorPatientService;

@CrossOrigin
@RestController
@RequestMapping("/DoctorPatient")
public class DoctorPatientController {
    @Autowired
    IDoctorPatientService doctorPatientService;

    @PutMapping("/add")
    public Result addDoctorPatient(@RequestBody DoctorPatientRequest doctorPatientRequest){
        doctorPatientService.addDoctorPatient(doctorPatientRequest);
        return Result.success();
    }

    @GetMapping("/getByUserId")
    public Result getByUserId(@RequestParam Integer userId){
        return Result.success(doctorPatientService.getByUserId(userId));
    }

    // 删除--------------
    // 根据医生和患者id删除单条
    @DeleteMapping("/delete/{doctorUserId}/{patientUserId}")
    public Result deleteByUserId(@PathVariable("doctorUserId") Integer doctorUserId, @PathVariable("patientUserId") Integer patientUserId){
        doctorPatientService.deleteByUserId(doctorUserId, patientUserId);
        return Result.success();
    }
    // 根据患者id删除全部
    @DeleteMapping("/deleteByPatientId/{patientUserId}")
    public Result deleteByPatientId(@PathVariable("patientUserId") Integer patientUserId){
        doctorPatientService.deleteByPatientUserId(patientUserId);
        return Result.success();
    }
}
