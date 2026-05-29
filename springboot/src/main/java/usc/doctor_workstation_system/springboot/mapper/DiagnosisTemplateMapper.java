package usc.doctor_workstation_system.springboot.mapper;

import org.apache.ibatis.annotations.Mapper;
import usc.doctor_workstation_system.springboot.entity.DiagnosisTemplate;
import java.util.List;

@Mapper
public interface DiagnosisTemplateMapper {
    List<DiagnosisTemplate> listAll();
    List<DiagnosisTemplate> listByDepartment(String department);
}
