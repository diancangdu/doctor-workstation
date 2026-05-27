package usc.emrsytem.springboot.entity;

import lombok.Data;
import java.sql.Timestamp;

@Data
public class Registration {
    private Integer registrationId;
    private Integer patientId;
    private Integer patientUserId;
    private Integer doctorId;
    private String department;
    private String chiefComplaint;
    private Integer queueNumber;
    private String status;
    private Timestamp registrationTime;
    private Timestamp consultationTime;
    private Timestamp completionTime;
    private String patientName;
    private String doctorName;
}
