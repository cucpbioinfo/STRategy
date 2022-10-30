package th.ac.chula.fims.services.interfaces;

import th.ac.chula.fims.payload.dto.CountryDto;
import th.ac.chula.fims.payload.dto.ProvinceDto;
import th.ac.chula.fims.payload.dto.ProvinceUpdateDto;
import th.ac.chula.fims.payload.response.MessageResponse;

import java.util.List;

public interface CountryService {
    List<CountryDto> getAllCountries();

    MessageResponse updateCountryById(CountryDto country);

    MessageResponse addNewCountry(CountryDto country);

    CountryDto getCountryById(int countryId);

    List<ProvinceDto> getProvincesByCountryId(int countryId);

    void updateProvincesByCountryId(int countryId, ProvinceUpdateDto provinceUpdateDto);
}
