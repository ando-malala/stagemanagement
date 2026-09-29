package mg.mef.stagemanagement.dto.utilisateur;

public class ReinitialiserMotDePasseRequest {

    private String nouveauMotDePasse;

    public ReinitialiserMotDePasseRequest() {
    }

    public String getNouveauMotDePasse() {
        return nouveauMotDePasse;
    }

    public void setNouveauMotDePasse(String nouveauMotDePasse) {
        this.nouveauMotDePasse = nouveauMotDePasse;
    }
}