package th.ac.chula.fims.payload.response.statistics;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import th.ac.chula.fims.models.tables.Province;
import th.ac.chula.fims.payload.response.statistics.amount.AlleleAmount;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProvinceAlleleListResponse {
    private Province province;
    private List<AlleleAmount> alleleAmountList;
}
