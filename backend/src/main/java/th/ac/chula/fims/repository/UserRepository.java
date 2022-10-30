package th.ac.chula.fims.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import th.ac.chula.fims.models.Enum.ERole;
import th.ac.chula.fims.models.Enum.EStatus;
import th.ac.chula.fims.models.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends PagingAndSortingRepository<User, Long> {
    Optional<User> findByUsername(String username);

    List<User> findByUsernameLikeAndEmailLikeAndStatusIn(String Username, String email, List<EStatus> status, Pageable pageable);

    Long countByUsernameLikeAndEmailLikeAndStatusIn(String Username, String email, List<EStatus> status);

    List<User> findByUsernameLikeAndEmailLike(String Username, String email, Pageable pageable);

    Long countByUsernameLikeAndEmailLike(String Username, String email);

    Boolean existsByUsername(String username);

    Boolean existsByEmail(String email);

    List<User> findByStatus(EStatus status, Pageable pageable);

    Long countByStatus(EStatus status);

    List<User> findDistinctByUsernameLikeAndEmailLikeAndRoles_NameIn(String username, String email, List<ERole> roles, Pageable pageable);

    Long countDistinctByUsernameLikeAndEmailLikeAndRoles_NameIn(String username, String email, List<ERole> roles);

    List<User> findDistinctByUsernameLikeAndEmailLikeAndRoles_NameInAndStatusIn(String username, String email, List<ERole> roles, List<EStatus> status, Pageable pageable);

    Long countDistinctByUsernameLikeAndEmailLikeAndRoles_NameInAndStatusIn(String username, String email, List<ERole> roles, List<EStatus> status);
}
