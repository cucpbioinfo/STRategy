package th.ac.chula.fims.models.tables;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "pattern_alignment_allele")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"patternAlignmentLocus"})
public class PatternAlignmentAllele {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "allele")
    private Float allele;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH})
    @JoinColumn(name = "seq_align_locus_id")
    private PatternAlignmentLocus patternAlignmentLocus;

    @OneToMany(mappedBy = "patternAlignmentAllele", orphanRemoval = true)
    private List<PatternAlignmentMotif> motifs;
}
