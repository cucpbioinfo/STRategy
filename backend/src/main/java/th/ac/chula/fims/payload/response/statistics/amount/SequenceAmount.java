package th.ac.chula.fims.payload.response.statistics.amount;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import th.ac.chula.fims.payload.response.statistics.ChangedBaseDto;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SequenceAmount {
    private String sequence;
    private String repeatMotif;
    private List<ChangedBaseDto> changingBase;
    private int amount;
    private float frequency;

    public SequenceAmount(String repeatMotif, String sequence, int amount, List<ChangedBaseDto> changedBaseList) {
        this.repeatMotif = repeatMotif;
        this.sequence = sequence;
        this.amount = amount;
        this.changingBase = changedBaseList;
    }
}
