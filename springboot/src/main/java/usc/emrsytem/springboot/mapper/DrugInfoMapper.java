package usc.emrsytem.springboot.mapper;

import org.apache.ibatis.annotations.Mapper;
import usc.emrsytem.springboot.entity.DrugInfo;
import java.util.List;

@Mapper
public interface DrugInfoMapper {
    List<DrugInfo> listAll();
    DrugInfo getByName(String drugName);
}
