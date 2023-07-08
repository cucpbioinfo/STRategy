package th.ac.chula.fims.payload.response.statistics;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import th.ac.chula.fims.payload.response.statistics.amount.AlleleAmount;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class LocusInfoStatistic {
    private LocusInfoStatisticDiploid diploid;
    private LocusInfoStatisticHaploid haploid;

    public LocusInfoStatistic(LocusInfoStatisticDiploid diploid, LocusInfoStatisticHaploid haploid) {
        this.diploid = diploid;
        this.haploid = haploid;
    }
}
