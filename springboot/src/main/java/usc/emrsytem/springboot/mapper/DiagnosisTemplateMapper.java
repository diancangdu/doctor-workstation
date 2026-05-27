package usc.emrsytem.springboot.mapper;

import org.apache.ibatis.annotations.Mapper;
import usc.emrsytem.springboot.entity.DiagnosisTemplate;
import java.util.List;

@Mapper
public interface DiagnosisTemplateMapper {
    List<DiagnosisTemplate> listAll();
    List<DiagnosisTemplate> listByDepartment(String department);
}
