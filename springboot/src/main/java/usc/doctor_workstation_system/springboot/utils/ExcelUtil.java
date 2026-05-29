package usc.doctor_workstation_system.springboot.utils;

import org.springframework.stereotype.Component;
import usc.doctor_workstation_system.springboot.entity.MedicalRecord;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class ExcelUtil {

    private static final String FILE_NAME = "archives.csv";

    public String exportToCsv(MedicalRecord record, String patientName, String doctorName) {
        return exportToCsv(record, patientName, doctorName, "归档");
    }

    public String restoreToCsv(MedicalRecord record, String patientName, String doctorName) {
        return exportToCsv(record, patientName, doctorName, "恢复");
    }

    private String exportToCsv(MedicalRecord record, String patientName, String doctorName, String action) {
        String dir = System.getProperty("user.dir") + "/../docs/archives/";
        new File(dir).mkdirs();
        String filePath = dir + FILE_NAME;
        boolean exists = new File(filePath).exists();

        try (PrintWriter pw = new PrintWriter(new OutputStreamWriter(
                new FileOutputStream(filePath, true), "UTF-8"))) {
            if (!exists) {
                pw.write("﻿"); // BOM
                pw.println("操作,病历编号,患者,主治医生,主诉,现病史,既往史,体格检查,诊断,症状,处方,备注,就诊时间,操作时间");
            }
            pw.println(
                csv(action) + "," +
                csv(record.getRecordId()) + "," +
                csv(patientName) + "," +
                csv(doctorName) + "," +
                csv(record.getChiefComplaint()) + "," +
                csv(record.getPresentIllness()) + "," +
                csv(record.getPastHistory()) + "," +
                csv(record.getPhysicalExam()) + "," +
                csv(record.getDiagnosis()) + "," +
                csv(record.getSymptoms()) + "," +
                csv(record.getPrescription()) + "," +
                csv(record.getRemarks()) + "," +
                csv(record.getCreatedAt()) + "," +
                csv(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
            );
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return filePath;
    }

    private String csv(Object val) {
        if (val == null) return "";
        String s = val.toString().replace("\"", "\"\"");
        if (s.contains(",") || s.contains("\"") || s.contains("\n")) {
            return "\"" + s + "\"";
        }
        return s;
    }
}
