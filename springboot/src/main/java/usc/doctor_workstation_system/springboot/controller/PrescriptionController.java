package usc.doctor_workstation_system.springboot.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import usc.doctor_workstation_system.springboot.common.Result;
import usc.doctor_workstation_system.springboot.controller.request.AddPrescriptionRequest;
import usc.doctor_workstation_system.springboot.controller.request.PrescriptionRequest;
import usc.doctor_workstation_system.springboot.service.IPrescriptionService;

@CrossOrigin
@RestController
@RequestMapping("/prescription")
public class PrescriptionController {
    @Autowired
    IPrescriptionService prescriptionService;

    @PutMapping("/add")
    @Transactional
    public Result addPrescription(@RequestBody AddPrescriptionRequest addPrescriptionRequest){
        prescriptionService.addPrescription(addPrescriptionRequest);
        return Result.success();
    }

    // 查询
    @GetMapping("/list")
    public Result prescriptionList(PrescriptionRequest params){
        return Result.success(prescriptionService.listPrescription(params));
    }

    // 删除
    @DeleteMapping("/delete/{id}")
    public Result deletePrescription(@PathVariable Integer id){
        prescriptionService.deletePrescription(id);
        return Result.success();
    }
}
