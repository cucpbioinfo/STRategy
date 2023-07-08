package th.ac.chula.fims.payload.response.statistics.amount;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class AlleleGenotypeSeqAmount {
    private int fsId;
    private Float allele;
    private String genotype;
    private int amount;
    private String sequence;
    private String repeatMotif;
}
