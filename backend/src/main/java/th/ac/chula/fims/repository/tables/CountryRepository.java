package th.ac.chula.fims.repository.tables;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import th.ac.chula.fims.models.tables.Country;

@Repository
public interface CountryRepository extends JpaRepository<Country, Integer> {

    Country findByCountry(String string);

}
