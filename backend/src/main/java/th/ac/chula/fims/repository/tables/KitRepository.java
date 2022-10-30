package th.ac.chula.fims.repository.tables;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import th.ac.chula.fims.models.tables.Kit;

import java.util.List;

@Repository
public interface KitRepository extends JpaRepository<Kit, Integer> {
    List<Kit> findByChromosomeType(String chromosomeType);
}
