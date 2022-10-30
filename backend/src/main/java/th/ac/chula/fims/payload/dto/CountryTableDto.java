package th.ac.chula.fims.payload.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CountryTableDto {
    private String country;
    private BigDecimal summaryValue;
    private BigDecimal finalValue;
    private List<LocusTableDto> alleleList;
}
