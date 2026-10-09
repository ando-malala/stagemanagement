package mg.mef.stagemanagement.dto.evaluation;

import java.math.BigDecimal;

public class CritereEvaluationResponse {

    private Integer id;
    private String code;
    private String libelle;
    private String description;
    private BigDecimal coefficient;
    private Boolean obligatoire;
    private Integer ordre;

    public CritereEvaluationResponse() {
    }

    public CritereEvaluationResponse(
            Integer id,
            String code,
            String libelle,
            String description,
            BigDecimal coefficient,
            Boolean obligatoire,
            Integer ordre
    ) {
        this.id = id;
        this.code = code;
        this.libelle = libelle;
        this.description = description;
        this.coefficient = coefficient;
        this.obligatoire = obligatoire;
        this.ordre = ordre;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getLibelle() {
        return libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getCoefficient() {
        return coefficient;
    }

    public void setCoefficient(BigDecimal coefficient) {
        this.coefficient = coefficient;
    }

    public Boolean getObligatoire() {
        return obligatoire;
    }

    public void setObligatoire(Boolean obligatoire) {
        this.obligatoire = obligatoire;
    }

    public Integer getOrdre() {
        return ordre;
    }

    public void setOrdre(Integer ordre) {
        this.ordre = ordre;
    }
}