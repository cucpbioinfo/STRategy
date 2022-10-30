package th.ac.chula.fims.payload.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import th.ac.chula.fims.payload.dto.CountryCountDto;
import th.ac.chula.fims.payload.projection.RaceCount;
import th.ac.chula.fims.payload.response.person.SampleIDYear;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MatchedSampleResponse {
    private List<SampleIDYear> sampleDetails;
    private int total;
    private int amount;
    private List<CountryCountDto> countryCount;
    private List<RaceCount> raceCount;
}
