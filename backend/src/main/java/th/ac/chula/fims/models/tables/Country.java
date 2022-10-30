package th.ac.chula.fims.models.tables;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "countries")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"regions", "persons", "profileSearchLoci"})
public class Country {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "country")
    private String country;

    @OneToMany(mappedBy = "country", cascade = CascadeType.ALL)
    private List<Region> regions;

    @OneToMany(mappedBy = "country", cascade = CascadeType.ALL)
    private List<Person> persons;

    @OneToMany(mappedBy = "country", orphanRemoval = true)
    private List<ProfileSearchLocus> profileSearchLoci;

    public void add(ProfileSearchLocus profileSearchLocus) {
        if (profileSearchLoci == null) {
            profileSearchLoci = new ArrayList<>();
        }

        profileSearchLoci.add(profileSearchLocus);
        profileSearchLocus.setCountry(this);
    }

    public void addProfileSearchLocusAll(List<ProfileSearchLocus> profileSearchLocusList) {
        if (profileSearchLoci == null) {
            profileSearchLoci = new ArrayList<>();
        }

        profileSearchLoci.addAll(profileSearchLocusList);
        profileSearchLocusList.forEach(e -> e.setCountry(this));
    }

    @Override
    public String toString() {
        return "Country [id=" + id + ", country=" + country + ", regions=" + regions + ", persons=" + persons + "]";
    }
}
