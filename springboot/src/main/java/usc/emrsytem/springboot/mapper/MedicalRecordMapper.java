package usc.emrsytem.springboot.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import usc.emrsytem.springboot.controller.request.RecordRequest;
import usc.emrsytem.springboot.entity.MedicalRecord;

import java.util.List;

@Mapper
public interface MedicalRecordMapper {
    // 添加
    int addMedicalRecord(MedicalRecord medicalRecord);

    List<MedicalRecord> listPrescription(RecordRequest request);

    int updateMedicalRecord(MedicalRecord medicalRecord);

    int deleteMedicalRecord(Integer recordId);

    int archiveMedicalRecord(Integer recordId);

    int restoreMedicalRecord(Integer recordId);

    MedicalRecord getById(Integer recordId);

    int confirmMedicalRecord(Integer recordId);

    int voidMedicalRecord(@Param("recordId") Integer recordId, @Param("voidReason") String voidReason);
}
