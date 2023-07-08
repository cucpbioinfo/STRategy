package th.ac.chula.fims.repository.tables;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import th.ac.chula.fims.models.tables.SummaryDataByCountry;

import java.util.List;

@Repository
public interface SummaryDataRepository extends JpaRepository<SummaryDataByCountry, Integer> {

    @Query(value = "SELECT COUNT(*) as total FROM (SELECT f.sample_id FROM loci l INNER JOIN forenseq f ON l.locus = f.locus WHERE l.kit_id = :kitId GROUP BY f.sample_id) sum;", nativeQuery = true)
    Integer findNumberOfSamplesByKidId(@Param(value = "kitId") Integer kitId);

    List<SummaryDataByCountry> findByLocusOrderByAllele(String locus);

    @Query(value = "SELECT COUNT(distinct fs.sample_id) as person_amount, ffs.allele FROM forenseq fs INNER JOIN forenseq_sequence ffs ON fs.id = ffs.forenseq_id WHERE fs.locus = :locus GROUP BY ffs.allele", nativeQuery = true)
    List<Object[]> findAmountPersonByLocusGroupByAllele(@Param(value = "locus") String locus);
}
