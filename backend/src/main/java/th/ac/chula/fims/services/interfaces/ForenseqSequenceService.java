package th.ac.chula.fims.services.interfaces;

import org.springframework.data.domain.Pageable;
import th.ac.chula.fims.models.tables.ForenseqSequence;
import th.ac.chula.fims.payload.dto.EventHistoryDto;
import th.ac.chula.fims.payload.response.isnp.ISNPResponseList;
import th.ac.chula.fims.payload.response.statistics.LocusInfoStatistic;
import th.ac.chula.fims.payload.response.statistics.PatternAlignmentResponse;
import th.ac.chula.fims.payload.response.statistics.ProvinceAlleleListResponse;

import javax.transaction.Transactional;
import java.util.List;

public interface ForenseqSequenceService {
    @Transactional
    LocusInfoStatistic getGraphInfoByLocus(String locus);

    List<ProvinceAlleleListResponse> getMapInfoByLocus(String locus);

    List<ForenseqSequence> calculatePatternAlignmentAndSaveToDatabase(String locus, String allele);

    void calculateAllLocusAndAlleleOfPatternAlignmentAndSaveToDatabase();

    EventHistoryDto getLastPatternAlignmentGeneration();

    List<PatternAlignmentResponse> getPatternAlignmentByLocusAndAllele(String locus, String allele, Boolean isFetch, Pageable pageable);

    @Transactional
    ISNPResponseList getStatsISNP(Integer size, Integer page, String searchWord);
}
