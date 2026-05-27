package usc.emrsytem.springboot.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import usc.emrsytem.springboot.controller.request.RecordRequest;
import usc.emrsytem.springboot.entity.MedicalRecord;
import usc.emrsytem.springboot.entity.Registration;
import usc.emrsytem.springboot.exception.ServiceException;
import usc.emrsytem.springboot.mapper.MedicalRecordMapper;
import usc.emrsytem.springboot.mapper.RegistrationMapper;
import usc.emrsytem.springboot.service.IRegistrationService;

import java.util.List;

@Service
public class RegistrationService implements IRegistrationService {
    @Autowired
    RegistrationMapper registrationMapper;
    @Autowired
    MedicalRecordMapper medicalRecordMapper;

    @Override
    public int addRegistration(Registration registration) {
        if (registration.getPatientUserId() == null || registration.getDoctorId() == null) {
            throw new ServiceException("患者和医生不能为空");
        }
        // 查询当前队列最大序号
        List<Registration> queue = registrationMapper.getDoctorQueue(registration.getDoctorId());
        int maxNum = queue.stream().mapToInt(r -> r.getQueueNumber() != null ? r.getQueueNumber() : 0).max().orElse(0);
        registration.setQueueNumber(maxNum + 1);
        return registrationMapper.addRegistration(registration);
    }

    @Override
    public int startConsultation(Integer registrationId) {
        Registration r = registrationMapper.getById(registrationId);
        if (r == null) throw new ServiceException("挂号记录不存在");
        if (!"waiting".equals(r.getStatus())) throw new ServiceException("该记录已接诊或已结束");
        return registrationMapper.startConsultation(registrationId);
    }

    @Override
    public int completeConsultation(Integer registrationId) {
        Registration r = registrationMapper.getById(registrationId);
        if (r == null) throw new ServiceException("挂号记录不存在");
        // 检查是否已书写病历
        RecordRequest req = new RecordRequest();
        req.setPatientId(r.getPatientId());
        List<MedicalRecord> records = medicalRecordMapper.listPrescription(req);
        if (records.isEmpty()) {
            throw new ServiceException("请先为该患者书写病历后再完成就诊");
        }
        return registrationMapper.completeConsultation(registrationId);
    }

    @Override
    public int cancelRegistration(Integer registrationId) {
        return registrationMapper.cancelRegistration(registrationId);
    }

    @Override
    public Object listRegistrations(Registration params) {
        return registrationMapper.listRegistrations(params);
    }

    @Override
    public List<Registration> getDoctorQueue(Integer doctorId) {
        return registrationMapper.getDoctorQueue(doctorId);
    }
}
