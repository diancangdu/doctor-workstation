package usc.doctor_workstation_system.springboot.mapper;

import org.apache.ibatis.annotations.Mapper;
import usc.doctor_workstation_system.springboot.entity.DrugInfo;
import java.util.List;

@Mapper
public interface DrugInfoMapper {
    List<DrugInfo> listAll();
    DrugInfo getByName(String drugName);
}
