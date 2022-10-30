package th.ac.chula.fims.utils.mapper;

import org.mapstruct.Mapper;
import th.ac.chula.fims.models.tables.ChangedBase;
import th.ac.chula.fims.payload.response.statistics.ChangedBaseDto;

@Mapper(componentModel = "spring")
public interface ChangedBaseMapper {
    ChangedBaseDto toChangedBaseDto(ChangedBase changedBase);

    ChangedBase toChangedBase(ChangedBaseDto changedBasedto);
}
