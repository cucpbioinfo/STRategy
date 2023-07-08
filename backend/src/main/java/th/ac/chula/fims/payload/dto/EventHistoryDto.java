package th.ac.chula.fims.payload.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import th.ac.chula.fims.models.Enum.EventType;

import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EventHistoryDto {
    private EventType eventType;
    private Instant time;
    private String feature;
    private String note;
}
