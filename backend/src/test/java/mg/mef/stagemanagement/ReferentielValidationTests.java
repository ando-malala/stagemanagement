package mg.mef.stagemanagement;

import mg.mef.stagemanagement.dto.referentiel.ReferentielRequest;
import mg.mef.stagemanagement.repository.*;
import mg.mef.stagemanagement.service.ReferentielAcademiqueService;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReferentielValidationTests {
    final NiveauEtudeRepository levels = mock(NiveauEtudeRepository.class);
    final MoisStageRepository months = mock(MoisStageRepository.class);
    final ReferentielAcademiqueService service = new ReferentielAcademiqueService(mock(EtablissementRepository.class), mock(FiliereRepository.class), levels, months);
    ReferentielRequest request(int length) { var request = new ReferentielRequest(); request.setLibelle("A".repeat(length)); request.setActif(true); return request; }
    @Test void levelLengthMatchesSqlInsteadOfGeneric150Characters() {
        when(levels.save(any())).thenAnswer(i -> i.getArgument(0));
        assertEquals(100, service.creerNiveauEtude(request(100)).getLibelle().length());
        assertEquals(HttpStatus.BAD_REQUEST, assertThrows(ResponseStatusException.class, () -> service.creerNiveauEtude(request(101))).getStatusCode());
        verify(levels, times(1)).save(any());
    }
    @Test void monthLengthMatchesSqlInsteadOfGeneric150Characters() {
        when(months.save(any())).thenAnswer(i -> i.getArgument(0));
        assertEquals(50, service.creerMoisStage(request(50)).getLibelle().length());
        assertEquals(HttpStatus.BAD_REQUEST, assertThrows(ResponseStatusException.class, () -> service.creerMoisStage(request(51))).getStatusCode());
        verify(months, times(1)).save(any());
    }
}
