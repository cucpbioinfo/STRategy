package th.ac.chula.fims.payload.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class ProvinceUpdateDto {
    private List<Integer> deleteProvinceIds;
    private List<ProvinceDto> provinceToUpdateList;
}
