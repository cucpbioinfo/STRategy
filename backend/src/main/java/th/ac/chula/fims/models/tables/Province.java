package th.ac.chula.fims.models.tables;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "provinces")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"persons"})
public class Province {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "province")
    private String province;

    @Column(name = "native_name")
    private String nativeName;

    @Column(name = "latitude")
    private double latitude;

    @Column(name = "longitude")
    private double longitude;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH})
    @JoinColumn(name = "region_id")
    private Region region;

    @OneToMany(mappedBy = "province", cascade = CascadeType.ALL)
    private List<Person> persons;

    public Province(String province, Region region) {
        super();
        this.province = province;
        this.region = region;
    }

    @Override
    public String toString() {
        return "Province [id=" + id + ", province=" + province + ", region=" + region + ", persons=" + persons + "]";
    }
}
