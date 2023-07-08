package th.ac.chula.fims.repository.tables;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import th.ac.chula.fims.models.tables.PatternAlignmentMotif;

import java.util.List;

@Repository
public interface PatternAlignmentMotifRepository extends JpaRepository<PatternAlignmentMotif, Integer> {
    @Query(value = "SELECT m.id, m.seq_no, m.motif, m.amount, m.allele_id FROM pattern_alignemnt_motif m INNER JOIN " +
            "pattern_alignment_allele a ON m.allele_id = a.id INNER JOIN pattern_alignment_loci l ON l.id = a" +
            ".seq_align_locus_id WHERE a.allele = :allele AND l.locus = :locus ORDER BY m.seq_no", nativeQuery = true)
    List<PatternAlignmentMotif> findMotifByLocusAndAllele(String locus, Float allele);

    @Query(value = "SELECT m FROM PatternAlignmentLocus l JOIN l.patternAlignmentAlleles a JOIN a.motifs m " +
            "WHERE l.locus = :locus AND a.allele like :allele ORDER BY m.seqNo")
    List<PatternAlignmentMotif> findByLocusAndAllele(String locus, Float allele);
}
