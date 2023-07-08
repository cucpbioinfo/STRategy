package th.ac.chula.fims.utils.mapper;

import org.mapstruct.Mapper;
import th.ac.chula.fims.models.tables.history.EventHistory;
import th.ac.chula.fims.payload.dto.EventHistoryDto;

@Mapper(componentModel = "spring")
public interface EventHistoryMapper {
    EventHistoryDto toEventHistoryDto(EventHistory eventHistory);

    EventHistory toEventHistory(EventHistoryDto eventHistoryDto);
}
