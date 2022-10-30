package th.ac.chula.fims.repository.tables;

import th.ac.chula.fims.payload.request.LocusAllele;

import java.util.List;

public interface SampleRepositoryCustom {
    List<Object[]> searchMatchedSample(List<LocusAllele> lAList);
}
