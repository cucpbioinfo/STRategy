package th.ac.chula.fims.models.tables;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import th.ac.chula.fims.models.Enum.EGender;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "persons")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", length = 6)
    private EGender gender;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH})
    @JoinColumn(name = "race_id")
    private Race race;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH})
    @JoinColumn(name = "country_id")
    private Country country;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH})
    @JoinColumn(name = "region_id")
    private Region region;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH})
    @JoinColumn(name = "province_id")
    private Province province;

    @OneToMany(mappedBy = "person", cascade = CascadeType.ALL)
    private List<Sample> samples;

    public Person(EGender gender, Race race, Country country, Region region, Province province) {
        super();
        this.gender = gender;
        this.race = race;
        this.country = country;
        this.region = region;
        this.province = province;
    }

    public void add(Sample sample) {
        if (samples == null) {
            samples = new ArrayList<>();
        }

        samples.add(sample);
        sample.setPerson(this);
    }

    @Override
    public String toString() {
        return "Person [id=" + id + ", gender=" + gender
                + ", race=" + race + ", country=" + country
                + ", region=" + region + ", province=" + province + "]";
    }
}
