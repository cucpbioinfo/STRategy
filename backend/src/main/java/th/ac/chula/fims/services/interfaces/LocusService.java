package th.ac.chula.fims.services.interfaces;

import th.ac.chula.fims.payload.response.statistics.ChromosomeLociResponse;

import java.util.List;

public interface LocusService {
    ChromosomeLociResponse getAllLocus();

    List<String> getAllGlobalLocus();

    List<String> getAllIsnpLocus();
}
