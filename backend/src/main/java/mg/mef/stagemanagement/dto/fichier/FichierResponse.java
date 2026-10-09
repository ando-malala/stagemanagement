package mg.mef.stagemanagement.dto.fichier;

import java.time.LocalDateTime;

public class FichierResponse {

    private Integer id;
    private String nomOriginal;
    private String typeMime;
    private Long tailleOctets;
    private String typePieceJointeCode;
    private String typePieceJointeLibelle;
    private LocalDateTime dateAjout;
    private LocalDateTime dateAssociation;

    public FichierResponse() {
    }

    public FichierResponse(
            Integer id,
            String nomOriginal,
            String typeMime,
            Long tailleOctets,
            String typePieceJointeCode,
            String typePieceJointeLibelle,
            LocalDateTime dateAjout,
            LocalDateTime dateAssociation
    ) {
        this.id = id;
        this.nomOriginal = nomOriginal;
        this.typeMime = typeMime;
        this.tailleOctets = tailleOctets;
        this.typePieceJointeCode = typePieceJointeCode;
        this.typePieceJointeLibelle = typePieceJointeLibelle;
        this.dateAjout = dateAjout;
        this.dateAssociation = dateAssociation;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNomOriginal() {
        return nomOriginal;
    }

    public void setNomOriginal(String nomOriginal) {
        this.nomOriginal = nomOriginal;
    }

    public String getTypeMime() {
        return typeMime;
    }

    public void setTypeMime(String typeMime) {
        this.typeMime = typeMime;
    }

    public Long getTailleOctets() {
        return tailleOctets;
    }

    public void setTailleOctets(Long tailleOctets) {
        this.tailleOctets = tailleOctets;
    }

    public String getTypePieceJointeCode() {
        return typePieceJointeCode;
    }

    public void setTypePieceJointeCode(String typePieceJointeCode) {
        this.typePieceJointeCode = typePieceJointeCode;
    }

    public String getTypePieceJointeLibelle() {
        return typePieceJointeLibelle;
    }

    public void setTypePieceJointeLibelle(String typePieceJointeLibelle) {
        this.typePieceJointeLibelle = typePieceJointeLibelle;
    }

    public LocalDateTime getDateAjout() {
        return dateAjout;
    }

    public void setDateAjout(LocalDateTime dateAjout) {
        this.dateAjout = dateAjout;
    }

    public LocalDateTime getDateAssociation() {
        return dateAssociation;
    }

    public void setDateAssociation(LocalDateTime dateAssociation) {
        this.dateAssociation = dateAssociation;
    }
}