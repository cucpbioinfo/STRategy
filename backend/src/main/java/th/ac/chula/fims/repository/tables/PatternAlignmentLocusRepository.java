package th.ac.chula.fims.repository.tables;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import th.ac.chula.fims.models.tables.PatternAlignmentLocus;

import java.util.List;
import java.util.Optional;

@Repository
public interface PatternAlignmentLocusRepository extends JpaRepository<PatternAlignmentLocus, Integer> {
    Optional<PatternAlignmentLocus> findByLocus(String locus);

    @Query(value = "SELECT DISTINCT fs.allele as allele FROM forenseq_sequence fs INNER JOIN forenseq f ON fs.forenseq_id = f.id WHERE f.locus = :locus", nativeQuery = true)
    List<Float> findAlleleByLocus(String locus);
}
