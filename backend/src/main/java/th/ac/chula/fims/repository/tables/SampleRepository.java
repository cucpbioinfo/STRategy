package th.ac.chula.fims.repository.tables;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import th.ac.chula.fims.models.tables.Sample;

import java.util.List;
import java.util.Optional;

@Repository
public interface SampleRepository extends JpaRepository<Sample, Integer>, SampleRepositoryCustom {

    @Query(value = "SELECT DISTINCT allele FROM forenseq fs INNER JOIN forenseq_sequence fss ON fs.id = fss.forenseq_id WHERE fs.locus = :locus ORDER BY cast(fss.allele as unsigned);", nativeQuery = true)
    List<Float> findAlleleByLocus(@Param(value = "locus") String locus);

    Optional<Sample> findTopBySampleId(String sampleId);

    Boolean existsBySampleId(String sampleId);
}
