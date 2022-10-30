package th.ac.chula.fims.payload.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LocusTableDto {
    private String locus;
    private BigDecimal pValue;
    private BigDecimal qValue;
    private BigDecimal finalValue;
    private BigDecimal inverseFinalValue;
    private Float allele1;
    private Float allele2;
}
