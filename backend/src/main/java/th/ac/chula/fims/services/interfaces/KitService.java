package th.ac.chula.fims.services.interfaces;

import th.ac.chula.fims.payload.response.statistics.KitLocusListResponse;

import javax.transaction.Transactional;

public interface KitService {
    @Transactional
    KitLocusListResponse getAllKits();
}
