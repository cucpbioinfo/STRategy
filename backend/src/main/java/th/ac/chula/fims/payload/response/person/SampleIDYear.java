package th.ac.chula.fims.payload.response.person;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SampleIDYear {
    private Long id;
    private String sampleId;
    private int sampleYear;
}
