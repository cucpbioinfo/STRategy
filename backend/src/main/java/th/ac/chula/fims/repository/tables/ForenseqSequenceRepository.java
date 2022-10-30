package th.ac.chula.fims.repository.tables;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import th.ac.chula.fims.models.tables.ForenseqSequence;
import th.ac.chula.fims.payload.projection.ExportDetailDto;
import th.ac.chula.fims.payload.projection.ForenSequenceFull;

import java.util.List;
import java.util.Optional;

@Repository
public interface ForenseqSequenceRepository extends JpaRepository<ForenseqSequence, Integer> {
    @Query(value = "SELECT max(ffs.id) as id, allele, genotype, count(*) as amount, sequence, max(ffs.repeat_motif) as repeatMotif FROM forenseq_sequence ffs inner join forenseq ff on ffs.forenseq_id = ff.id where ff.locus = :locus GROUP BY ff.genotype, ffs.Allele, ffs.sequence ORDER BY ffs.Allele * 1, ffs.sequence;", nativeQuery = true)
    List<Object[]> findStatsByLocus(@Param("locus") String locus);

    @Query(value = "SELECT fs.sequence as sequence, s.id as sampleId, fs.id as forenseqId, f.genotype as genotype, f.chromosome_type as chromosome, fs.allele as allele, fs.repeat_motif as repeatMotif FROM samples s LEFT JOIN forenseq f on s.id = f.sample_id LEFT JOIN forenseq_sequence fs ON f.id = fs.forenseq_id WHERE f.locus = :locus ORDER BY s.id", nativeQuery = true)
    List<ForenSequenceFull> findAllForenseqAndForenseqSequenceByLocus(@Param("locus") String locus);

    @Query(value = "SELECT max(t.id) as id, t.allele as allele, count(*) as amount, t.sequence as sequence, max(t.repeat_motif) as repeatMotif FROM (SELECT ffs.id as id, allele, sequence, ffs.repeat_motif as repeat_motif FROM forenseq_sequence ffs inner join forenseq ff on ffs.forenseq_id = ff.id where ff.locus = :locus AND ff.sample_id IN (SELECT resolveId.sample_id FROM (SELECT ff.sample_id, ff.genotype, REGEXP_REPLACE(ff.genotype, \"([0123456789]+),([0123456789]+)\", \"$1\") AS allele1, REGEXP_REPLACE(ff.genotype, \"([0123456789]+),([0123456789]+)\", \"$2\") AS allele2 FROM forenseq ff where ff.locus = :locus and ff.genotype REGEXP \"([0123456789]+),([0123456789]+)\") resolveId where resolveId.allele1 = resolveId.allele2)) t GROUP BY t.Allele, t.sequence ORDER BY t.Allele * 1, t.sequence", nativeQuery = true)
    List<Object[]> findStatsHomoByLocus(@Param("locus") String locus);

    @Query(value = "SELECT max(t.id) as id, t.allele as allele, count(*) as amount, t.sequence as sequence, max(t.repeat_motif) as repeatMotif FROM (SELECT ffs.id as id, allele, sequence, ffs.repeat_motif as repeat_motif FROM forenseq_sequence ffs inner join forenseq ff on ffs.forenseq_id = ff.id where ff.locus = :locus AND ff.sample_id IN (SELECT resolveId.sample_id FROM (SELECT ff.sample_id, ff.genotype, REGEXP_REPLACE(ff.genotype, \"([0123456789]+),([0123456789]+)\", \"$1\") AS allele1, REGEXP_REPLACE(ff.genotype, \"([0123456789]+),([0123456789]+)\", \"$2\") AS allele2 FROM forenseq ff where ff.locus = :locus and ff.genotype REGEXP \"([0123456789]+),([0123456789]+)\") resolveId where resolveId.allele1 != resolveId.allele2)) t GROUP BY t.Allele, t.sequence ORDER BY t.Allele * 1, t.sequence", nativeQuery = true)
    List<Object[]> findStatsHeteroByLocus(@Param("locus") String locus);

    @Query(value = "SELECT s.sample_id as sample_id, s.sample_year as sample_year, allele, sequence, ffs.repeat_motif as repeatMotif FROM samples s inner join forenseq ff on s.id = ff.sample_id inner join forenseq_sequence ffs on ffs.forenseq_id = ff.id where ff.locus = :locus ORDER BY ffs.Allele * 1, repeatMotif, sample_year, sample_id", nativeQuery = true)
    List<ExportDetailDto> findExportDetailsByLocus(@Param("locus") String locus);

    @Query(value = "SELECT allele, sum(frequency) as freq FROM summary_data where locus = :locus GROUP BY allele ORDER BY cast(allele as unsigned)", nativeQuery = true)
    List<Object[]> findStatsByLocusIncludeOtherDB(@Param("locus") String locus);

    @Query(value = "SELECT COUNT(*) FROM (SELECT COUNT(*) FROM forenseq_sequence ffs inner join forenseq ff on ffs.forenseq_id = ff.id where ff.locus = :locus GROUP BY ff.genotype, ff.sample_id HAVING count(*) = 2) sum", nativeQuery = true)
    int findNumberOfHetero(@Param("locus") String locus);

    @Query(value = "SELECT COUNT(*) FROM (SELECT COUNT(*) FROM forenseq_sequence ffs inner join forenseq ff on ffs.forenseq_id = ff.id where ff.locus = :locus GROUP BY ff.genotype, ff.sample_id HAVING count(*) = 1) sum", nativeQuery = true)
    int findNumberOfHomo(@Param("locus") String locus);

    @Query(value = "SELECT pattern FROM pattern_alignment pa WHERE pa.locus = :locus ORDER BY pa.seq_no", nativeQuery = true)
    List<String> findMotifLocus(@Param("locus") String locus);

    @Query(value = "SELECT sp.sample_id, sp.sample_year, ffs.sequence, ffs.read_count FROM forenseq fs INNER JOIN forenseq_sequence ffs ON fs.id = ffs.forenseq_id INNER JOIN samples sp ON fs.sample_id = sp.id WHERE fs.locus = :locus AND ffs.allele = :allele", nativeQuery = true)
    List<Object[]> findAllForenseqTable(@Param("locus") String locus, @Param("allele") float allele);

    @Query(value = "SELECT fs FROM Forenseq f JOIN f.forenseqSequences fs WHERE f.locus = :locus AND fs.allele = :allele")
    List<ForenseqSequence> findForenseqSequencesByLocusAndAllele(String locus, String allele);

    @Query(value = "SELECT fs.locus, fs.genotype, count(distinct fs.sample_id) as amount FROM forenseq fs INNER JOIN forenseq_sequence ffs ON fs.id = ffs.forenseq_id INNER JOIN (SELECT distinct locus from forenseq f WHERE f.chromosome_type = \"iSNP\" AND f.locus LIKE :search LIMIT :limit OFFSET :offset) t2 ON t2.locus = fs.locus WHERE fs.chromosome_type = \"iSNP\" AND fs.locus LIKE :search GROUP BY fs.locus, fs.genotype ;", nativeQuery = true)
    List<Object[]> findStatByISNPGroupByLocusAndAllele(@Param("limit") Integer limit,
                                                       @Param("offset") Integer offset, @Param("search") String searchWord);

    @Query(value = "SELECT count(distinct locus) from forenseq f WHERE f.chromosome_type = \"iSNP\" AND f.locus LIKE :search", nativeQuery = true)
    Optional<Integer> countStatByISNPGroupByLocusAndAllele(@Param("search") String searchWord);

    @Query(value = "SELECT pv.id, pv.province, pv.native_name, pv.latitude, pv.longitude, ffs.allele, count(*), rg.region FROM forenseq fs INNER JOIN forenseq_sequence ffs ON fs.id = ffs.forenseq_id INNER JOIN samples sp ON sp.id = fs.sample_id INNER JOIN persons ps ON ps.id = sp.person_id INNER JOIN provinces pv ON pv.id = ps.province_id INNER JOIN regions rg ON rg.id = pv.region_id WHERE fs.locus = :locus GROUP BY ffs.allele, pv.id ORDER BY pv.id, cast(ffs.allele as unsigned);", nativeQuery = true)
    List<Object[]> findStatsMapByLocus(@Param("locus") String locus);

    @Query(value = "SELECT fs.chromosome_type, fs.locus, ffs.allele, ffs.sequence, ffs.read_count FROM forenseq fs INNER JOIN samples sm ON fs.sample_id = sm.id INNER JOIN persons ps ON sm.person_id = ps.id INNER JOIN forenseq_sequence ffs ON ffs.forenseq_id = fs.id where sm.id = :sampleId", nativeQuery = true)
    List<Object[]> findAllForenseqSequenceBySampleId(@Param("sampleId") int sampleId);

    @Query(value = "SELECT count(distinct locus) from forenseq f WHERE f.chromosome_type = \"iSNP\";", nativeQuery = true)
    Optional<Integer> statByISNPGroupByLocusAndAlleleCount();
}
