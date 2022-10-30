package th.ac.chula.fims.services.interfaces;

import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;
import th.ac.chula.fims.models.Enum.ERole;
import th.ac.chula.fims.models.Enum.EStatus;
import th.ac.chula.fims.models.Role;
import th.ac.chula.fims.models.User;
import th.ac.chula.fims.models.tables.Kit;
import th.ac.chula.fims.models.tables.Locus;
import th.ac.chula.fims.payload.dto.ExistingSampleId;
import th.ac.chula.fims.payload.dto.KitDto;
import th.ac.chula.fims.payload.dto.UserUpdateDto;
import th.ac.chula.fims.payload.response.MessageResponse;
import th.ac.chula.fims.payload.response.user.AllUserResponse;

import java.io.IOException;
import java.util.List;

public interface AdminService {
    AllUserResponse getAllUser(String username, String email, List<EStatus> status, List<ERole> roles, Pageable pageable);

    User partialUpdateUserById(Long userId, UserUpdateDto user);

    Kit createKit(Kit kit);

    Locus createLocus(Locus locus, Integer kitId);

    MessageResponse uploadMap(MultipartFile map) throws IOException;

    List<Role> getAllRoles();

    List<KitDto> getAllKitsAndLoci(String chromosome);

    MessageResponse updateKitById(Integer kitId, Kit kit);

    MessageResponse deleteByKitId(Integer kitId);

    MessageResponse deleteLocusById(Integer locusId);

    MessageResponse updateLocusById(Integer locusId, Locus locus);

    List<ExistingSampleId> isSampleIdsExist(List<String> sampleIds);
}
