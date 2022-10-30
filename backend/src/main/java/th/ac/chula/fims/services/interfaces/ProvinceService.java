package th.ac.chula.fims.services.interfaces;

import th.ac.chula.fims.payload.dto.ProvinceDto;

import java.util.List;

public interface ProvinceService {
    List<ProvinceDto> getProvincesByIds(List<Integer> ids);
}
