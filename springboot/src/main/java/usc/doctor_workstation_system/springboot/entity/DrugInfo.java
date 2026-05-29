package usc.doctor_workstation_system.springboot.entity;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class DrugInfo {
    private Integer drugId;
    private String drugName;
    private String specification;
    private BigDecimal unitPrice;
    private String dosageHint;
    private String frequencyHint;
    private String indications;
    private String contraindications;
    private String adverseReactions;
    private String interactions;
}
