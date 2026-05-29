package usc.doctor_workstation_system.springboot.entity;

import lombok.Data;
import java.sql.Timestamp;

@Data
public class AuditLog {
    private Integer logId;
    private Integer recordId;
    private Integer userId;
    private String username;
    private String action;
    private String detail;
    private Timestamp createdAt;
}
