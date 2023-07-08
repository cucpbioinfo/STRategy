package th.ac.chula.fims.repository.tables;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import th.ac.chula.fims.models.Enum.EGender;
import th.ac.chula.fims.models.tables.Person;
import th.ac.chula.fims.payload.projection.CountryCount;
import th.ac.chula.fims.payload.projection.ProvinceCount;
import th.ac.chula.fims.payload.projection.RaceCount;
import th.ac.chula.fims.payload.projection.RegionCount;

import java.util.List;

@Repository
public interface PersonRepository extends PagingAndSortingRepository<Person, Integer> {
    @Query(value = "SELECT ct.country as country, count(*) as amount, max(ct.id) as countryId FROM persons ps left join countries ct on ct.id = ps.country_id where ps.id in :ids group by ct.country", nativeQuery = true)
    List<CountryCount> findByIdsAndGroupByCountry(List<Integer> ids);

    @Query(value = "SELECT pv.province as province, count(*) as amount FROM persons ps left join provinces pv on pv.id = ps.province_id where ps.id in :ids and ps.country_id = :countryId group by pv.province", nativeQuery = true)
    List<ProvinceCount> findByIdsAndCountryIdGroupByProvince(List<Integer> ids, Integer countryId);

    @Query(value = "SELECT rc.race as race, count(*) as amount FROM persons ps left join races rc on rc.id = ps.race_id where ps.id in :ids group by rc.race", nativeQuery = true)
    List<RaceCount> findByIdsAndGroupByRace(List<Integer> ids);

    @Query(value = "SELECT rg.region as region, count(*) as amount FROM persons ps left join regions rg on rg.id = ps.region_id where ps.id in :ids and ps.country_id = :countryId group by rg.region", nativeQuery = true)
    List<RegionCount> findByIdsAndCountryIdGroupByRegion(List<Integer> ids, Integer countryId);

    List<Person> findByRace_RaceLikeAndCountry_CountryLikeAndProvince_ProvinceLike(String race, String country, String province, Pageable pageable);

    List<Person> findByGenderInAndRace_RaceLikeAndCountry_CountryLikeAndProvince_ProvinceLike(List<EGender> gender, String race, String country, String province, Pageable pageable);

    Long countByRace_RaceLikeAndCountry_CountryLikeAndProvince_ProvinceLike(String race, String country, String province);

    Long countByGenderInAndRace_RaceLikeAndCountry_CountryLikeAndProvince_ProvinceLike(List<EGender> gender, String race, String country, String province);
}
