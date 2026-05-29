package usc.doctor_workstation_system.springboot.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import usc.doctor_workstation_system.springboot.common.Result;
import usc.doctor_workstation_system.springboot.mapper.DiagnosisTemplateMapper;
import usc.doctor_workstation_system.springboot.mapper.DrugInfoMapper;

@RestController
@RequestMapping("/common")
public class CommonController {

    @Autowired
    DiagnosisTemplateMapper diagnosisTemplateMapper;
    @Autowired
    DrugInfoMapper drugInfoMapper;

    @GetMapping("/templates")
    public Result listTemplates(@RequestParam(required = false) String department) {
        if (department != null && !department.isEmpty()) {
            return Result.success(diagnosisTemplateMapper.listByDepartment(department));
        }
        return Result.success(diagnosisTemplateMapper.listAll());
    }

    @GetMapping("/drugs")
    public Result listDrugs() {
        return Result.success(drugInfoMapper.listAll());
    }

    @GetMapping("/drug")
    public Result getDrug(@RequestParam String name) {
        return Result.success(drugInfoMapper.getByName(name));
    }
}
