package th.ac.chula.fims.models.tables;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "core_loci")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CoreLocus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "locus")
    private String locus;

    @Column(name = "country")
    private String country;
}
