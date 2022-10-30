package th.ac.chula.fims.payload.response.summary;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SummaryDataResponse {
    private int total;
    private List<HashMap<String, Float>> countryCountList;
    private HashMap<String, Float> countriesMap;
}
