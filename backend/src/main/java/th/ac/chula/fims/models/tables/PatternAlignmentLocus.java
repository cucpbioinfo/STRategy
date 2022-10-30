package th.ac.chula.fims.models.tables;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pattern_alignment_loci")
@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties({"kit"})
public class PatternAlignmentLocus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "locus")
    private String locus;

    @Column(name = "is_reverse")
    private Boolean isReverse;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH})
    @JoinColumn(name = "kit_id")
    private Kit kit;

    @OneToMany(mappedBy = "patternAlignmentLocus", cascade = CascadeType.ALL)
    private List<PatternAlignmentAllele> patternAlignmentAlleles;

    @Override
    public String toString() {
        return "Locus [id=" + id + ", locus=" + locus + ", kit=" + kit + "]";
    }

    public void add(PatternAlignmentAllele patternAlignmentAllele) {
        if (patternAlignmentAlleles == null) {
            patternAlignmentAlleles = new ArrayList<>();
        }

        patternAlignmentAlleles.add(patternAlignmentAllele);
        patternAlignmentAllele.setPatternAlignmentLocus(this);
    }
}
