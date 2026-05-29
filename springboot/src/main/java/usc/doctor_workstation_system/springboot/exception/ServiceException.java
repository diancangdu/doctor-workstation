package usc.doctor_workstation_system.springboot.exception;

public class ServiceException extends RuntimeException{

    private String code;

    public ServiceException(String message) {
        super(message);
    }

    public ServiceException(String code, String message) {
        super(message);
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
