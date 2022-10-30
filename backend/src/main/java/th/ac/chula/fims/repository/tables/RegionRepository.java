package th.ac.chula.fims.repository.tables;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import th.ac.chula.fims.models.tables.Country;
import th.ac.chula.fims.models.tables.Region;

import java.util.List;

@Repository
public interface RegionRepository extends JpaRepository<Region, Integer> {

    List<Region> findAllByCountryId(Integer countryId);

    Region findByRegion(String string);

    Region findByRegionAndCountry(String region, Country country);
}
