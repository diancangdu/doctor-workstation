package usc.emrsytem.springboot.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import usc.emrsytem.springboot.common.Result;
import usc.emrsytem.springboot.entity.Registration;
import usc.emrsytem.springboot.service.IRegistrationService;

@RestController
@RequestMapping("/registration")
public class RegistrationController {

    @Autowired
    IRegistrationService registrationService;

    @PostMapping("/add")
    public Result addRegistration(@RequestBody Registration registration) {
        registrationService.addRegistration(registration);
        return Result.success();
    }

    @GetMapping("/list")
    public Result listRegistrations(Registration params) {
        return Result.success(registrationService.listRegistrations(params));
    }

    @GetMapping("/queue")
    public Result getDoctorQueue(@RequestParam Integer doctorId) {
        return Result.success(registrationService.getDoctorQueue(doctorId));
    }

    @PutMapping("/start/{id}")
    public Result startConsultation(@PathVariable Integer id) {
        registrationService.startConsultation(id);
        return Result.success();
    }

    @PutMapping("/complete/{id}")
    public Result completeConsultation(@PathVariable Integer id) {
        registrationService.completeConsultation(id);
        return Result.success();
    }

    @PutMapping("/cancel/{id}")
    public Result cancelRegistration(@PathVariable Integer id) {
        registrationService.cancelRegistration(id);
        return Result.success();
    }
}
