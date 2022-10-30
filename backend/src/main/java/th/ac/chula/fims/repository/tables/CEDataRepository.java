package th.ac.chula.fims.repository.tables;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import th.ac.chula.fims.models.tables.CEData;

@Repository
public interface CEDataRepository extends JpaRepository<CEData, Integer> {

}
