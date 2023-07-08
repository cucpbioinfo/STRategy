package th.ac.chula.fims.models.tables;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "forenseq_sequence")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"sample"})
public class ForenseqSequence {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "allele")
    private String allele;

    @Column(name = "read_count")
    private int readCount;

    @Size(max = 1000)
    @Column(name = "sequence")
    private String sequence;

    @Column(name = "repeat_motif")
    private String repeatMotif;

    @Column(name = "forward_repeat_motif")
    private String forwardRepeatMotif;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH})
    @JoinColumn(name = "forenseq_id")
    private Forenseq forenseq;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH})
    @JoinColumn(name = "sample_id")
    private Sample sample;

    @OneToMany(mappedBy = "forenseqSeq", orphanRemoval = true)
    private List<ChangedBase> changedBaseList;

    public ForenseqSequence(int id) {
        this.id = id;
    }

    public ForenseqSequence(String allele, int readCount, @Size(max = 1000) String sequence, Sample sample) {
        super();
        this.allele = allele;
        this.readCount = readCount;
        this.sequence = sequence;
        this.sample = sample;
        this.repeatMotif = null;
        this.forwardRepeatMotif = null;
    }

    public void add(ChangedBase cb) {
        if (changedBaseList == null) {
            changedBaseList = new ArrayList<>();
        }

        changedBaseList.add(cb);
        cb.setForenseqSeq(this);
    }

    @Override
    public String toString() {
        return "ForenseqSequence [id=" + id + ", allele=" + allele + ", readCount=" + readCount + ", sequence="
                + sequence + "]";
    }
}
