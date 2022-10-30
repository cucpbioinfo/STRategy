package th.ac.chula.fims.payload.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SearchProfileDto {
    private String locus;
    private Float allele1;
    private Float allele2;
}
