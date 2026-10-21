
package mg.mef.stagemanagement.service;

import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;

@Service
public class CalculPeriodesRapportService {

    public record PeriodeRapport(
            LocalDate debut,
            LocalDate fin,
            LocalDate dateLimite
    ) {}

    public List<PeriodeRapport> calculer(
            LocalDate dateDebutStage,
            LocalDate dateFinStage
    ) {
        if (dateDebutStage == null || dateFinStage == null
                || dateFinStage.isBefore(dateDebutStage)) {
            throw new IllegalArgumentException(
                    "Les dates du stage sont invalides"
            );
        }

        List<PeriodeRapport> periodes = new ArrayList<>();
        LocalDate debut = dateDebutStage;

        while (!debut.isAfter(dateFinStage)) {

            LocalDate dimanche = debut.with(
                    TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY)
            );

            LocalDate fin = dimanche.isBefore(dateFinStage)
                    ? dimanche
                    : dateFinStage;

            LocalDate dateLimite = dimanche.plusDays(1);

            periodes.add(new PeriodeRapport(
                    debut,
                    fin,
                    dateLimite
            ));

            debut = fin.plusDays(1);
        }

        return periodes;
    }
}
