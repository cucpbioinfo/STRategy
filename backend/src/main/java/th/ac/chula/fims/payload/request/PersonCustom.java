package th.ac.chula.fims.payload.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import th.ac.chula.fims.models.Enum.EGender;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PersonCustom {
    private EGender gender;
    private int race_id;
    private int country_id;
    private int province_id;
    private int region_id;
}
