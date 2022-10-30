package th.ac.chula.fims.models.tables;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "changed_bases")
@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties({"forenseqSeq"})
public class ChangedBase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "based_from")
    private char from;

    @Column(name = "based_to")
    private char to;

    @Column(name = "position")
    private int position;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH})
    @JoinColumn(name = "fs_id")
    private ForenseqSequence forenseqSeq;

    public ChangedBase(char from, char to, int position, ForenseqSequence forenseqSeq) {
        this.from = from;
        this.to = to;
        this.position = position;
        this.forenseqSeq = forenseqSeq;
    }
}
