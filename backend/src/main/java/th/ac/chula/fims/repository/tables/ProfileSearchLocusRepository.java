package th.ac.chula.fims.repository.tables;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import th.ac.chula.fims.models.tables.ProfileSearchLocus;
import th.ac.chula.fims.payload.projection.AFValueCountry;

import java.util.Set;

@Repository
public interface ProfileSearchLocusRepository extends JpaRepository<ProfileSearchLocus, Integer> {
    @Query(value = "SELECT a.value as aFValue, c.country as country FROM profile_search_alleles a inner join profile_search_loci l on a.locus_id = l.id inner join countries c on l.country_id = c.id where l.locus = :locus and abs(a.allele-:allele) <= 1e-6;", nativeQuery = true)
    Set<AFValueCountry> findByLocusAndAllele(String locus, Float allele);
}