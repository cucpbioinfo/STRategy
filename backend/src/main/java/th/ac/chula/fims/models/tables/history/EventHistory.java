package th.ac.chula.fims.models.tables.history;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import th.ac.chula.fims.models.Enum.EventType;
import th.ac.chula.fims.models.User;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "event_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EventHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "event_type")
    private EventType eventType;

    @Column(name = "event_time")
    private Instant time;

    @Column(name = "feature")
    private String feature;

    @Column(name = "note")
    private String note;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "actor_id")
    private User actor;
}
