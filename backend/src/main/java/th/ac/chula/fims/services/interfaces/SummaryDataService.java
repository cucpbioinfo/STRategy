package th.ac.chula.fims.services.interfaces;

import th.ac.chula.fims.payload.response.summary.SummaryDashboardResponse;
import th.ac.chula.fims.payload.response.summary.SummaryDataResponse;

import java.util.HashMap;

public interface SummaryDataService {
    SummaryDataResponse getSummaryDataByLocus(String locus);

    HashMap<String, SummaryDashboardResponse> getDashboardSummaries();
}
