package th.ac.chula.fims.payload.response.person;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import th.ac.chula.fims.models.tables.ForenseqSequence;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SequenceDetail {
    private String genotype;

    private String qcIndicator;

    private List<ForenseqSequence> fsList = new ArrayList<ForenseqSequence>();

    public SequenceDetail(String genotype, String qcIndicator) {
        this.genotype = genotype;
        this.qcIndicator = qcIndicator;
    }

    @Override
    public String toString() {
        return "SequenceDetail [allele=" + genotype + ", fsList=" + fsList + "]";
    }
}