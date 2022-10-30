package th.ac.chula.fims.payload.response.person;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import th.ac.chula.fims.models.tables.Person;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class PersonForenseq {
    private Person person;
    private List<ForenseqRow> frA;
    private List<ForenseqRow> frX;
    private List<ForenseqRow> frY;
    private List<ForenseqRow> frI;
    private List<ForenseqSequenceRow> fsrA;
    private List<ForenseqSequenceRow> fsrX;
    private List<ForenseqSequenceRow> fsrY;
    private List<ForenseqSequenceRow> fsrI;
}
