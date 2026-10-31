package mg.mef.stagemanagement.service;

import java.util.List;
import java.util.Comparator;
import mg.mef.stagemanagement.model.TypePieceJointe;
import mg.mef.stagemanagement.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional(readOnly = true)
public class DocumentCatalogueService {
    private final DossierAccessService access;
    public record TypeDocument(Integer id, String code, String libelle, boolean pourStagiaire,
            boolean pourParticipation, boolean obligatoireCloture, boolean multipleAutorise) {}
    public record DocumentObligatoire(Integer id, String code, String libelle, boolean present) {}
    private final TypePieceJointeRepository types;
    private final StageParticipantFichierRepository fichiers;
    private final StageParticipantRepository participations;

    public DocumentCatalogueService(TypePieceJointeRepository types, StageParticipantFichierRepository fichiers,
            StageParticipantRepository participations, DossierAccessService access) {
        this.access = access;
        this.types = types; this.fichiers = fichiers; this.participations = participations;
    }
    public List<TypeDocument> types() {
        return types.findAll().stream().filter(t -> Boolean.TRUE.equals(t.getActif()))
                .sorted(Comparator.comparing(TypePieceJointe::getLibelle))
                .map(t -> new TypeDocument(t.getId(), t.getCode(), t.getLibelle(), Boolean.TRUE.equals(t.getPourStagiaire()),
                        Boolean.TRUE.equals(t.getPourParticipation()), Boolean.TRUE.equals(t.getObligatoireCloture()),
                        Boolean.TRUE.equals(t.getMultipleAutorise()))).toList();
    }
    public List<DocumentObligatoire> obligatoires(Integer id) {
        access.participation(id, false);
        if (!participations.existsById(id)) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Participation introuvable");
        var presents = fichiers.findByStageParticipant_Id(id).stream().map(f -> f.getTypePieceJointe().getId()).toList();
        return types.findByPourParticipationTrueAndObligatoireClotureTrueAndActifTrue().stream()
                .sorted(Comparator.comparing(TypePieceJointe::getLibelle))
                .map(t -> new DocumentObligatoire(t.getId(), t.getCode(), t.getLibelle(), presents.contains(t.getId()))).toList();
    }
    public void verifierCloture(Integer id) {
        var manquants = obligatoires(id).stream().filter(d -> !d.present()).map(DocumentObligatoire::libelle).toList();
        if (!manquants.isEmpty()) throw new ResponseStatusException(HttpStatus.CONFLICT,
                "Documents obligatoires manquants : " + String.join(", ", manquants));
    }
}
