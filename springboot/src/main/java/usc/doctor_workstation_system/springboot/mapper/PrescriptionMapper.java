package usc.doctor_workstation_system.springboot.mapper;

import org.apache.ibatis.annotations.Mapper;
import usc.doctor_workstation_system.springboot.controller.request.PrescriptionRequest;
import usc.doctor_workstation_system.springboot.entity.Prescription;

import java.util.List;

@Mapper
public interface PrescriptionMapper {
    // 添加处方
    int addPrescription(Prescription prescription);

    List<Prescription> listPrescription(PrescriptionRequest request);

    int deletePrescription(Integer prescriptionId);
}
