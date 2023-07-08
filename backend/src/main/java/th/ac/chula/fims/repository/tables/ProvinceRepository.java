package th.ac.chula.fims.repository.tables;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import th.ac.chula.fims.models.tables.Province;
import th.ac.chula.fims.models.tables.Region;

import java.util.List;

@Repository
public interface ProvinceRepository extends JpaRepository<Province, Integer> {

    List<Province> findAllByRegionId(Integer regionId);

    Province findByProvince(String name);

    Province findByProvinceAndRegion(String province, Region region);

    @Query("select p from Country c inner join Region r on r.country.id = c.id inner join Province p on p.region.id = r.id WHERE c.id = :countryId")
    List<Province> findAllByCountryId(Integer countryId);
}
