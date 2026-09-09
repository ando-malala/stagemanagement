package mg.mef.stagemanagement.repository;

import mg.mef.stagemanagement.model.StagiaireAcademique;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StagiaireAcademiqueRepository extends JpaRepository<StagiaireAcademique, Integer> {
}
