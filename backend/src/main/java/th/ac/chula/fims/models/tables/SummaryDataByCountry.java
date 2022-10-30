package th.ac.chula.fims.models.tables;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "summary_data")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SummaryDataByCountry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "frequency")
    private Float frequency;

    @Column(name = "allele")
    private Float allele;

    @Column(name = "based")
    private String based;

    @Column(name = "locus")
    private String locus;

    @Column(name = "country")
    private String country;

    @Column(name = "date")
    @JsonFormat(pattern = "dd/MM/yyyy")
    private Date date;
} 
