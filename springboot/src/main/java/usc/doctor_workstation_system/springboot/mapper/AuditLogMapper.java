package usc.doctor_workstation_system.springboot.mapper;

import org.apache.ibatis.annotations.Mapper;
import usc.doctor_workstation_system.springboot.entity.AuditLog;
import java.util.List;

@Mapper
public interface AuditLogMapper {
    int insert(AuditLog log);
    List<AuditLog> listByRecordId(Integer recordId);
}
