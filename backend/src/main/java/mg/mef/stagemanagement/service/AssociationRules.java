package mg.mef.stagemanagement.service;

import java.util.Objects;
import mg.mef.stagemanagement.model.Statut;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

/** Eligibility of new links; unchanged historical references remain readable and editable. */
public final class AssociationRules {
    private AssociationRules() {}
    public static boolean changed(Integer previous, Integer next) { return !Objects.equals(previous, next); }
    public static void active(Boolean active, String label) {
        if (!Boolean.TRUE.equals(active)) throw new ResponseStatusException(HttpStatus.CONFLICT, label + " inactif : nouvelle association impossible");
    }
    public static void activeStatus(Statut status, String label) {
        active(status != null && "ACTIF".equals(status.getCode()) && Boolean.TRUE.equals(status.getActif()), label);
    }
    public static String email(String value) {
        if (value != null && !value.matches("[^\\s@]+@[^\\s@]+"))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Adresse électronique invalide");
        return value;
    }
}
