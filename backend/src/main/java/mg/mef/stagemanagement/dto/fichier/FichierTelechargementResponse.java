package mg.mef.stagemanagement.dto.fichier;

import org.springframework.core.io.Resource;

public class FichierTelechargementResponse {

    private Resource resource;
    private String nomOriginal;
    private String typeMime;

    public FichierTelechargementResponse() {
    }

    public FichierTelechargementResponse(
            Resource resource,
            String nomOriginal,
            String typeMime
    ) {
        this.resource = resource;
        this.nomOriginal = nomOriginal;
        this.typeMime = typeMime;
    }

    public Resource getResource() {
        return resource;
    }

    public void setResource(Resource resource) {
        this.resource = resource;
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
}