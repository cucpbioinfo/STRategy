package th.ac.chula.fims.utils.mapper;

import org.mapstruct.Mapper;
import th.ac.chula.fims.models.tables.Country;
import th.ac.chula.fims.payload.dto.CountryDto;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CountryMapper {
    List<CountryDto> toCountryDtoList(List<Country> country);

    CountryDto toCountry(Country country);
}
