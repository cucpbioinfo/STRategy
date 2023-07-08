package th.ac.chula.fims.payload.response.summary;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SummaryDashboardResponse {
    private List<String> loci;
    private Integer numberOfSamples;
}
