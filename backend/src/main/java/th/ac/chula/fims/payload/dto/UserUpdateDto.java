package th.ac.chula.fims.payload.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import th.ac.chula.fims.models.Enum.EStatus;
import th.ac.chula.fims.models.Role;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserUpdateDto {
    private long id;
    private String username;
    private String email;
    private String password;
    private EStatus status;
    private Set<Role> roles = new HashSet<>();
}
