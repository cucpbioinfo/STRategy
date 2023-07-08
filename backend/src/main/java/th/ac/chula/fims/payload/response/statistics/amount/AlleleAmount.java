package th.ac.chula.fims.payload.response.statistics.amount;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class AlleleAmount {
    private Float allele;
    private int amount;
    private float frequency;
    private List<SequenceAmount> sequence;

    public AlleleAmount(Float allele, int amount) {
        this.allele = allele;
        this.amount = amount;
    }
}
