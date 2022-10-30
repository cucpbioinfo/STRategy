package th.ac.chula.fims.utils.mapper;

import org.mapstruct.Mapper;
import th.ac.chula.fims.models.tables.Kit;
import th.ac.chula.fims.payload.dto.KitDto;

@Mapper(componentModel = "spring")
public interface KitMapper {
    KitDto toKitDto(Kit kit);

    Kit toKit(KitDto kitDto);
}
