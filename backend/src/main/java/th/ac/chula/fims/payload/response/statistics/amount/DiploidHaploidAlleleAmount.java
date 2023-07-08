package th.ac.chula.fims.payload.response.statistics.amount;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class DiploidHaploidAlleleAmount {
    private List<AlleleAmount> diploid = new ArrayList<>();
    private List<AlleleAmount> haploid = new ArrayList<>();
}
