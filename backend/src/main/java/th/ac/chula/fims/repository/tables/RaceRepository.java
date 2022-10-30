package th.ac.chula.fims.repository.tables;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import th.ac.chula.fims.models.tables.Race;

@Repository
public interface RaceRepository extends JpaRepository<Race, Integer> {

    Race findByRace(String string);

}
