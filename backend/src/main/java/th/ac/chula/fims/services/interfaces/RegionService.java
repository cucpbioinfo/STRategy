package th.ac.chula.fims.services.interfaces;

import th.ac.chula.fims.models.tables.Province;
import th.ac.chula.fims.models.tables.Region;
import th.ac.chula.fims.payload.dto.RegionDto;
import th.ac.chula.fims.payload.response.MessageResponse;

import java.util.List;

public interface RegionService {
    List<Region> getRegionsByCountryId(Integer countryId);

    MessageResponse addNewRegionByCountryId(int countryId, RegionDto regionDto);

    MessageResponse updateRegionById(RegionDto region);

    MessageResponse deleteRegionById(int regionId);

    List<Province> getProvincesByRegionId(Integer regionId);
}
