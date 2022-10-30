package th.ac.chula.fims.services.interfaces.searchprofile;

import th.ac.chula.fims.payload.dto.CountryTableDto;
import th.ac.chula.fims.payload.dto.SearchProfileDto;

import java.util.List;

public interface ProfileSearchAlgorithm {
    List<CountryTableDto> performProfileSearch(List<SearchProfileDto> searchProfileDtoList);
}
