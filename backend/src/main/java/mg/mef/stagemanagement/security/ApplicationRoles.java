package mg.mef.stagemanagement.security;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/** Three application roles; legacy assignments remain readable without a SQL migration. */
public final class ApplicationRoles {
    public static final Set<String> SUPPORTED = Set.of("ADMIN", "ENCADREUR", "STAGIAIRE");

    private ApplicationRoles() { }

    public static String effective(String code) {
        return "RESPONSABLE_STAGE".equals(code) ? "ADMIN" : code;
    }

    public static List<String> effective(Collection<String> codes) {
        return codes.stream().map(ApplicationRoles::effective).filter(SUPPORTED::contains).distinct().toList();
    }
}
