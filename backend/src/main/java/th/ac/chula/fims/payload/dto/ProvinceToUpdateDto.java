package th.ac.chula.fims.payload.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProvinceToUpdateDto {
    private int id;
    private String province;
    private String nativeName;
    private double latitude;
    private double longitude;
    private int regionId;
}
