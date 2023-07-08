package th.ac.chula.fims.models.tables;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "profile_search_alleles")
@Getter
@Setter
@NoArgsConstructor
public class ProfileSearchAllele {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "allele")
    private Float allele;

    @Column(name = "value", precision = 10, scale = 10)
    private BigDecimal value;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH})
    @JoinColumn(name = "locus_id")
    private ProfileSearchLocus locus;

    @Override
    public String toString() {
        return "ProfileSearchAllele{" +
                "id=" + id +
                ", allele=" + allele +
                ", value=" + value +
                '}';
    }
}
