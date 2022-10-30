package th.ac.chula.fims.utils.mapper;

import org.mapstruct.Mapper;
import th.ac.chula.fims.models.tables.Province;
import th.ac.chula.fims.payload.dto.ProvinceDto;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProvinceMapper {
    List<ProvinceDto> toProvinceDto(List<Province> provinces);
}
