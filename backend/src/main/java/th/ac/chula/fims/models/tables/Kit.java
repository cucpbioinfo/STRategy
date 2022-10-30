package th.ac.chula.fims.models.tables;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "kits")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"loci"})
public class Kit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "kit")
    private String kit;

    @Column(name = "chromosome_type")
    private String chromosomeType;

    @OneToMany(mappedBy = "kit", cascade = CascadeType.ALL)
    private List<Locus> loci;

    public Kit(String kit, String chromosome_type) {
        super();
        this.kit = kit;
        this.chromosomeType = chromosome_type;
    }

    @Override
    public String toString() {
        return "Kit [id=" + id + ", kit=" + kit + ", chromosome_type=" + chromosomeType + ", loci=" + loci + "]";
    }

}
