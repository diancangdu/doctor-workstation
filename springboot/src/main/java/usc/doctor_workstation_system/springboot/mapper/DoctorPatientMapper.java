package usc.doctor_workstation_system.springboot.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import usc.doctor_workstation_system.springboot.entity.DoctorPatient;

import java.util.List;

@Mapper
public interface DoctorPatientMapper {
    // ============添加=================
    // 添加医生与患者对应关系到表doctor_patient
    int addDoctorPatient(@Param("doctorId") Integer doctorId, @Param("patientId") Integer patientId);

    // ============删除=================
    // 根据医生和患者的userId从表中删除关系
    int deleteByUserId(@Param("doctorId") Integer doctorId, @Param("patientId") Integer patientId);
    // 根据患者userId删除
    int deleteByPatientUserId(Integer patienId);

    // ==============查询=================
    // 根据医生和病人id查询出是否存在
    DoctorPatient getByDocAndPatId(@Param("doctorId") Integer doctorId, @Param("patientId") Integer patientId);
    // 根据医生id查询出所有患者
    List<DoctorPatient> getByDoctorId(Integer doctorId);
    // 根据医生id查出是否存在此医生
    int getByDoctorIdCount(Integer doctorId);

}
