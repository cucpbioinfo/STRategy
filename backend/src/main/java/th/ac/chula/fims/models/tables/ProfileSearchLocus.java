package th.ac.chula.fims.models.tables;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "profile_search_loci")
@Getter
@Setter
@NoArgsConstructor
public class ProfileSearchLocus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "locus")
    private String locus;

    @OneToMany(mappedBy = "locus", orphanRemoval = true)
    private List<ProfileSearchAllele> profileSearchAlleles;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH})
    @JoinColumn(name = "country_id")
    private Country country;

    public void add(ProfileSearchAllele profileSearchAllele) {
        if (profileSearchAlleles == null) {
            profileSearchAlleles = new ArrayList<>();
        }

        profileSearchAlleles.add(profileSearchAllele);
        profileSearchAllele.setLocus(this);
    }

    public void addProfileSearchAlleleAll(List<ProfileSearchAllele> profileSearchAlleleList) {
        if (profileSearchAlleles == null) {
            profileSearchAlleles = new ArrayList<>();
        }

        profileSearchAlleles.addAll(profileSearchAlleleList);
        profileSearchAlleleList.forEach(e -> e.setLocus(this));
    }

    @Override
    public String toString() {
        return "ProfileSearchLocus{" +
                "id=" + id +
                ", locus='" + locus + '\'' +
                ", profileSearchAlleles=" + profileSearchAlleles +
                '}';
    }
}
