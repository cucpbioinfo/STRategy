package th.ac.chula.fims.payload.response.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import th.ac.chula.fims.models.User;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AllUserResponse {
    private Long allUserSize;
    private List<User> usersList;
}
