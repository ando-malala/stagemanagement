package mg.mef.stagemanagement.repository;

import mg.mef.stagemanagement.model.Stagiaire;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StagiaireRepository extends JpaRepository<Stagiaire, Integer> {
    @org.springframework.data.jpa.repository.Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
    @org.springframework.data.jpa.repository.Query("select t from Stagiaire t where t.id = :id")
    java.util.Optional<Stagiaire> findForUpdate(@org.springframework.data.repository.query.Param("id") Integer id);
    java.util.Optional<Stagiaire> findByUtilisateur_Id(Integer utilisateurId);
}
