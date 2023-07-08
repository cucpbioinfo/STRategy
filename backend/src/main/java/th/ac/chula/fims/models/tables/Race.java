package th.ac.chula.fims.models.tables;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "races")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"summaryData", "persons"})
public class Race {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "race")
    private String race;

    @OneToMany(mappedBy = "race", cascade = CascadeType.ALL)
    private List<Person> persons;

    public Race(String race) {
        super();
        this.race = race;
    }

    @Override
    public String toString() {
        return "Race [id=" + id + ", race=" + race + ", persons=" + persons + "]";
    }

}
