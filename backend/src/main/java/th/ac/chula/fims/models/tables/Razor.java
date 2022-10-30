package th.ac.chula.fims.models.tables;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "razor")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Razor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "locus")
    private String locus;

    @Column(name = "genotype")
    private String genotype;

    @Column(name = "qc_indicator")
    private String qcIndicator;

    @Column(name = "chromosome_type")
    private String chromosomeType;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH})
    @JoinColumn(name = "sample_id")
    private Sample sample;

    @Override
    public String toString() {
        return "Razor [id=" + id + ", locus=" + locus + ", genotype=" + genotype + ", qcIndicator=" + qcIndicator
                + ", chromosomeType=" + chromosomeType + ", sample=" + sample + "]";
    }
}
