package th.ac.chula.fims.payload.response.statistics;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChromosomeLociResponse {
    private List<String> aLoci;
    private List<String> xLoci;
    private List<String> yLoci;
}
