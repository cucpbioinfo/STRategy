package th.ac.chula.fims.payload.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import th.ac.chula.fims.payload.projection.ProvinceCount;
import th.ac.chula.fims.payload.projection.RegionCount;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CountryCountDto {
    private String country;
    private Integer amount;
    private List<ProvinceCount> provinceCounts;
    private List<RegionCount> regionCounts;
}
