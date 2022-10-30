package th.ac.chula.fims.repository.tables;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import th.ac.chula.fims.models.tables.Forenseq;
import th.ac.chula.fims.payload.projection.GenotypeCount;

import java.util.List;

@Repository
public interface ForenseqRepository extends JpaRepository<Forenseq, Integer> {
    @Query(value = "SELECT fs.chromosome_type, fs.genotype, fs.locus, fs.qc_indicator FROM forenseq fs INNER JOIN samples sm ON fs.sample_id = sm.id INNER JOIN persons ps ON sm.person_id = ps.id where sm.id = :sampleId", nativeQuery = true)
    List<Object[]> findAllForenseqBySampleId(@Param("sampleId") int sampleId);

    @Query(value = "SELECT f.genotype as genotype, count(*) as count FROM forenseq f where f.locus = :locus group by f.genotype", nativeQuery = true)
    List<GenotypeCount> findByLocusAndGroupByGenotype(String locus);

    List<Forenseq> findAllByLocus(String locus);
}
