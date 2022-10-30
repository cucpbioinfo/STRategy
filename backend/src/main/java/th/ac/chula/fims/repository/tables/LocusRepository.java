package th.ac.chula.fims.repository.tables;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import th.ac.chula.fims.models.tables.Locus;

import java.util.List;

@Repository
public interface LocusRepository extends JpaRepository<Locus, Integer> {

    @Query(value = "SELECT DISTINCT locus, chromosome_type FROM forenseq"
            + " fq WHERE fq.chromosome_type = 'Autosome' OR fq.chromosome_type = 'x' "
            + "OR fq.chromosome_type = 'y';", nativeQuery = true)
    List<Object[]> findDistinctAllLocus();

    @Query(value = "SELECT distinct sd.locus FROM summary_data sd;", nativeQuery = true)
    List<String> findAllGlobalLocus();

    Locus findTopByLocus(String locus);

    List<Locus> findByKit_id(Integer id);

    @Query(value = "SELECT distinct locus FROM forenseq where chromosome_type = 'isnp'", nativeQuery = true)
    List<String> findDistinctLocusByISNP();
}
