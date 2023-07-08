package th.ac.chula.fims.models.tables;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "samples")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"razorList", "cedataList", "forenseqList", "forenseqSequenceList", "person"})
public class Sample {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "sample_year")
    private int sampleYear;

    @Column(name = "sample_id")
    private String sampleId;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH})
    @JoinColumn(name = "person_id")
    private Person person;

    @OneToMany(mappedBy = "sample", cascade = CascadeType.ALL)
    private List<Razor> razorList;

    @OneToMany(mappedBy = "sample", cascade = CascadeType.ALL)
    private List<CEData> cEDataList;

    @OneToMany(mappedBy = "sample", cascade = CascadeType.ALL)
    private List<Forenseq> forenseqList;

    @OneToMany(mappedBy = "sample", cascade = CascadeType.ALL)
    private List<ForenseqSequence> forenseqSequenceList;

    public Sample(int sampleYear, String sampleId, Person person) {
        super();
        this.sampleYear = sampleYear;
        this.sampleId = sampleId;
        this.person = person;
    }

    public void add(Forenseq forenseq) {
        if (forenseqList == null) {
            forenseqList = new ArrayList<>();
        }

        forenseqList.add(forenseq);
        forenseq.setSample(this);
    }

    public void add(CEData ceData) {
        if (cEDataList == null) {
            cEDataList = new ArrayList<>();
        }

        cEDataList.add(ceData);
        ceData.setSample(this);
    }

    @Override
    public String toString() {
        return "Sample [id=" + id + ", sample_year=" + sampleYear + ", sample_id=" + sampleId + ", person=" + person
                + ", razorList=" + razorList + ", cEDataList=" + cEDataList + ", forenseqList=" + forenseqList + "]";
    }

}
