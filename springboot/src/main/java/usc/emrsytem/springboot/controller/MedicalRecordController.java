package usc.emrsytem.springboot.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import usc.emrsytem.springboot.common.Result;
import usc.emrsytem.springboot.controller.request.AddMedicalRecordRequest;
import usc.emrsytem.springboot.controller.request.RecordRequest;
import usc.emrsytem.springboot.entity.MedicalRecord;
import usc.emrsytem.springboot.service.IMedicalRecordService;

@RestController
@RequestMapping("/medicalRecord")
public class MedicalRecordController {

    @Autowired
    IMedicalRecordService medicalRecordService;

    @PutMapping("/add")
    public Result addMedicalRecord(@RequestBody AddMedicalRecordRequest request) {
        medicalRecordService.addMedicalRecord(request);
        return Result.success();
    }

    @GetMapping("/getById/{recordId}")
    public Result getById(@PathVariable Integer recordId) {
        return Result.success(medicalRecordService.getById(recordId));
    }

    @GetMapping("/list")
    public Result listMedicalRecord(RecordRequest params) {
        return Result.success(medicalRecordService.listMedicalRecord(params));
    }

    @PostMapping("/update")
    public Result updateMedicalRecord(@RequestBody MedicalRecord medicalRecord) {
        medicalRecordService.updateMedicalRecord(medicalRecord);
        return Result.success();
    }

    @PutMapping("/archive/{recordId}")
    public Result archiveMedicalRecord(@PathVariable Integer recordId) {
        medicalRecordService.archiveMedicalRecord(recordId);
        return Result.success();
    }

    @PutMapping("/restore/{recordId}")
    public Result restoreMedicalRecord(@PathVariable Integer recordId) {
        medicalRecordService.restoreMedicalRecord(recordId);
        return Result.success();
    }

    @PutMapping("/confirm/{recordId}")
    public Result confirmMedicalRecord(@PathVariable Integer recordId, @RequestParam Integer userId, @RequestParam String username) {
        medicalRecordService.confirmMedicalRecord(recordId, userId, username);
        return Result.success();
    }

    @PutMapping("/void/{recordId}")
    public Result voidMedicalRecord(@PathVariable Integer recordId, @RequestParam String voidReason, @RequestParam Integer userId, @RequestParam String username) {
        medicalRecordService.voidMedicalRecord(recordId, voidReason, userId, username);
        return Result.success();
    }

    @PutMapping("/supplement/{recordId}")
    public Result supplementMedicalRecord(@PathVariable Integer recordId, @RequestBody MedicalRecord body, @RequestParam Integer userId, @RequestParam String username) {
        medicalRecordService.supplementMedicalRecord(recordId, body.getSupplement(), userId, username);
        return Result.success();
    }

    @GetMapping("/auditLogs/{recordId}")
    public Result getAuditLogs(@PathVariable Integer recordId) {
        return Result.success(medicalRecordService.getAuditLogs(recordId));
    }

    @DeleteMapping("/delete/{recordId}")
    public Result deleteMedicalRecord(@PathVariable Integer recordId) {
        medicalRecordService.deleteMedicalRecord(recordId);
        return Result.success();
    }
}
