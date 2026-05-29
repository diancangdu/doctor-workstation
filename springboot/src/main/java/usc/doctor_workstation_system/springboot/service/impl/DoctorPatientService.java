package usc.doctor_workstation_system.springboot.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import usc.doctor_workstation_system.springboot.controller.request.DoctorPatientRequest;
import usc.doctor_workstation_system.springboot.entity.DoctorPatient;
import usc.doctor_workstation_system.springboot.exception.ServiceException;
import usc.doctor_workstation_system.springboot.mapper.DoctorPatientMapper;
import usc.doctor_workstation_system.springboot.mapper.UserMapper;
import usc.doctor_workstation_system.springboot.service.IDoctorPatientService;

import java.util.List;

@Slf4j
@Service
public class DoctorPatientService implements IDoctorPatientService {

    @Autowired
    DoctorPatientMapper doctorPatientMapper;
    @Autowired
    UserMapper userMapper;

    @Override
    public int addDoctorPatient(DoctorPatientRequest doctorPatientRequest) {
        Integer doctorUserId = doctorPatientRequest.getDoctorValue();
        List<Integer> patientUserIds = doctorPatientRequest.getPatientValues();
        if (doctorUserId == null || patientUserIds == null || patientUserIds.isEmpty()) {
            throw new ServiceException("参数不能为空");
        }
        for (Integer patientUserId : patientUserIds) {
            Integer patientId = userMapper.getPatientById(patientUserId).getPatientId();
            Integer doctorId = userMapper.getDoctorById(doctorUserId).getDoctorId();
            if (patientId == null || doctorId == null) {
                throw new ServiceException("用户不存在");
            }
            if (doctorPatientMapper.getByDocAndPatId(doctorId, patientId) != null) {
                throw new ServiceException("医生 userId: " + doctorUserId + "和患者 userId: " + patientUserId +"关系已存在");
            }
            doctorPatientMapper.addDoctorPatient(doctorId, patientId);

        }

        return 0;
    }

    @Override
    public List<DoctorPatient> getByUserId(Integer userId) {
        if (userMapper.getDoctorById(userId) == null)
            throw new ServiceException("用户非医生");
        Integer doctorId = userMapper.getDoctorById(userId).getDoctorId();
        return doctorPatientMapper.getByDoctorId(doctorId);
    }

    @Override
    public int deleteByUserId(Integer doctorUserId, Integer patientUserId) {
        if(userMapper.getDoctorById(doctorUserId) == null || userMapper.getPatientById(patientUserId) == null) {
            throw new ServiceException("用户不存在");
        }
        Integer doctorId = userMapper.getDoctorById(doctorUserId).getDoctorId();
        Integer patientId = userMapper.getPatientById(patientUserId).getPatientId();

        return doctorPatientMapper.deleteByUserId(doctorId, patientId);
    }

    @Override
    public int deleteByPatientUserId(Integer patientUserId) {
        if(userMapper.getPatientById(patientUserId) == null) {
            throw new ServiceException("用户不存在");
        }
        Integer patientId = userMapper.getPatientById(patientUserId).getPatientId();
        return doctorPatientMapper.deleteByPatientUserId(patientId);
    }
}
