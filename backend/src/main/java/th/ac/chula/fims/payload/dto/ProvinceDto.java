package th.ac.chula.fims.payload.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ProvinceDto {
    private int id;
    private String province;
    private String nativeName;
    private double latitude;
    private double longitude;
    private RegionDto region;
}
