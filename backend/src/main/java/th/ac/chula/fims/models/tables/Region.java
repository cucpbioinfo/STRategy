package th.ac.chula.fims.models.tables;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "regions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"persons", "provinces"})
public class Region {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "region")
    private String region;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH})
    @JoinColumn(name = "country_id")
    private Country country;

    @OneToMany(mappedBy = "region", cascade = CascadeType.ALL)
    private List<Province> provinces;

    @OneToMany(mappedBy = "region", cascade = CascadeType.ALL)
    private List<Person> persons;

    public Region(String region, Country country) {
        super();
        this.region = region;
        this.country = country;
    }

    @Override
    public String toString() {
        return "Region [id=" + id + ", region=" + region + ", provinces=" + provinces
                + ", persons=" + persons + "]";
    }
}
