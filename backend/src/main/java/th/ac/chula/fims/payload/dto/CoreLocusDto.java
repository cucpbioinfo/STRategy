package th.ac.chula.fims.payload.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class CoreLocusDto {
    private int id;
    private String locus;
    private String country;
    private boolean isRequired;
}
