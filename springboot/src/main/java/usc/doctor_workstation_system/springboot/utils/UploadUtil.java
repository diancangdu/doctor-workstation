package usc.doctor_workstation_system.springboot.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileOutputStream;
import java.util.Objects;
import java.util.UUID;

@Component
public class UploadUtil {

    @Value("${upload.path}")
    public String basePath;

    @Value("${server.port}")
    public String port;

    public String upload(MultipartFile file){
        try {
            // 校验文件大小
            if (file.getSize() > 8 * 1024 * 1024) {
                return "error.file.size";
            }

            // 将文件保存为字节数组
            byte[] fileBytes = file.getBytes();

            String newFileName = UUID.randomUUID() + Objects.requireNonNull(file.getOriginalFilename()).substring(file.getOriginalFilename().lastIndexOf("."));

            String filePath = basePath + newFileName;


            FileOutputStream outputStream = new FileOutputStream( filePath);
            outputStream.write(fileBytes);
            outputStream.close();
            return "http://localhost:" + port + "/upload/" + newFileName;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
