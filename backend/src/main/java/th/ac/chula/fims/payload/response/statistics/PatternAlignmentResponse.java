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
public class PatternAlignmentResponse {
    private String sampleId;
    private int sampleYear;
    private String sequence;
    private int readCount;
    private String repeatMotif;
    private String forwardRepeatMotif;
    private List<ChangedBaseDto> changingBase;
}
