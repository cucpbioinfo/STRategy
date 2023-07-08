package th.ac.chula.fims.models.tables;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "loci")
@Getter
@Setter
@JsonIgnoreProperties({"kit"})
public class Locus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "locus")
    private String locus;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH})
    @JoinColumn(name = "kit_id")
    private Kit kit;

    public Locus() {
    }

    @Override
    public String toString() {
        return "Locus [id=" + id + ", locus=" + locus + ", kit=" + kit + "]";
    }
}
