package th.ac.chula.fims.repository.tables;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import th.ac.chula.fims.models.tables.CoreLocus;

import java.util.List;

@Repository
public interface CoreLociRepository extends JpaRepository<CoreLocus, Integer> {
    List<CoreLocus> findByCountry(String country);

    @Query(value = "SELECT distinct c.country as country FROM core_loci c", nativeQuery = true)
    List<String> findAllCountries();
}
