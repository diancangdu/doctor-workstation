package usc.emrsytem.springboot.mapper;

import org.apache.ibatis.annotations.Mapper;
import usc.emrsytem.springboot.entity.AuditLog;
import java.util.List;

@Mapper
public interface AuditLogMapper {
    int insert(AuditLog log);
    List<AuditLog> listByRecordId(Integer recordId);
}
