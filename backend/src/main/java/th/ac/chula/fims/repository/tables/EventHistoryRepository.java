package th.ac.chula.fims.repository.tables;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import th.ac.chula.fims.models.Enum.EventType;
import th.ac.chula.fims.models.tables.history.EventHistory;

@Repository
public interface EventHistoryRepository extends JpaRepository<EventHistory, Integer> {
    EventHistory findTopByEventTypeAndFeatureOrderByTimeDesc(EventType eventType, String feature);
}
