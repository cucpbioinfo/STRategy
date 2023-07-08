package th.ac.chula.fims.payload.response.statistics.amount;

import lombok.*;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class AlleleAmountContainer {
    private AlleleAmount alleleAmount;
    private Map<String, SequenceAmount> sequence = new HashMap<>();
}

