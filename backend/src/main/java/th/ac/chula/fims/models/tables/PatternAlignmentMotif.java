package th.ac.chula.fims.models.tables;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "pattern_alignemnt_motif")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"patternAlignmentAllele"})
public class PatternAlignmentMotif {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "seq_no")
    private Integer seqNo;

    @Column(name = "motif")
    private String motif;

    @Column(name = "amount")
    private Integer amount;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH})
    @JoinColumn(name = "allele_id")
    private PatternAlignmentAllele patternAlignmentAllele;

    public PatternAlignmentMotif(String motif, Integer amount) {
        this.motif = motif;
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "Motif{" +
                "id=" + id +
                ", seqNo=" + seqNo +
                ", pattern='" + motif + '\'' +
                ", rangeSeq='" + amount + '\'' +
                ", allele=" + patternAlignmentAllele.getAllele() +
                '}';
    }
}
