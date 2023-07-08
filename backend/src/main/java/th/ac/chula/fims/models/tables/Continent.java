package th.ac.chula.fims.models.tables;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "continents")
@Getter
@Setter
@JsonIgnoreProperties({"summaryData"})
public class Continent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "continent")
    private String continent;

    public Continent() {
    }

    public Continent(String continent) {
        this.continent = continent;
    }

    @Override
    public String toString() {
        return "Continent [id=" + id + ", continent=" + continent + "]";
    }
}
