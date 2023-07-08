package th.ac.chula.fims.payload.response.statistics;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import th.ac.chula.fims.payload.response.statistics.amount.AlleleAmount;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LocusInfoStatisticHaploid {
    private List<AlleleAmount> alleleList;
    private int numberOfTotal;
    private double powerOfDiscrimination;
    private double polymorphicInformationContent;
    private double matchProbability;
}
