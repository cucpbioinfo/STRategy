package th.ac.chula.fims.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import th.ac.chula.fims.constants.EntityConstant;
import th.ac.chula.fims.exceptions.ResourceNotFoundException;
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
import th.ac.chula.fims.repository.RoleRepository;
import th.ac.chula.fims.repository.UserRepository;
import th.ac.chula.fims.repository.tables.KitRepository;
import th.ac.chula.fims.repository.tables.LocusRepository;
import th.ac.chula.fims.repository.tables.SampleRepository;
import th.ac.chula.fims.services.interfaces.AdminService;
import th.ac.chula.fims.utils.mapper.KitMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;
    private final KitRepository kitRepository;
    private final LocusRepository locusRepository;
    private final PasswordEncoder encoder;
    private final RoleRepository roleRepository;
    private final KitMapper kitMapper;
    private final SampleRepository sampleRepository;
    private final String MAP_FILE_NAME = "maps.json";

    @Value("${file.upload-dir}")
    private String dirUpload;

    public AdminServiceImpl(UserRepository userRepository, KitRepository kitRepository,
                            LocusRepository locusRepository, PasswordEncoder encoder, RoleRepository roleRepository,
                            KitMapper kitMapper, SampleRepository sampleRepository) {
        this.userRepository = userRepository;
        this.kitRepository = kitRepository;
        this.locusRepository = locusRepository;
        this.encoder = encoder;
        this.roleRepository = roleRepository;
        this.kitMapper = kitMapper;
        this.sampleRepository = sampleRepository;
    }

    @Override
    public AllUserResponse getAllUser(String username, String email, List<EStatus> status, List<ERole> roles,
                                      Pageable pageable) {
        AllUserResponse allUserResponse = new AllUserResponse();
        List<User> usersList;
        Long allUserSize;

        if (status == null && roles == null) {
            usersList = userRepository.findByUsernameLikeAndEmailLike(username, email, pageable);
            allUserSize = userRepository.countByUsernameLikeAndEmailLike(username, email);

        } else if (status != null && roles == null) {
            usersList = userRepository.findByUsernameLikeAndEmailLikeAndStatusIn(username, email, status, pageable);
            allUserSize = userRepository.countByUsernameLikeAndEmailLikeAndStatusIn(username, email, status);
        } else if (status == null) {
            usersList = userRepository.findDistinctByUsernameLikeAndEmailLikeAndRoles_NameIn(username, email, roles,
                    pageable);
            allUserSize = userRepository.countDistinctByUsernameLikeAndEmailLikeAndRoles_NameIn(username, email, roles);
        } else {
            usersList = userRepository.findDistinctByUsernameLikeAndEmailLikeAndRoles_NameInAndStatusIn(username,
                    email, roles, status, pageable);
            allUserSize = userRepository.countDistinctByUsernameLikeAndEmailLikeAndRoles_NameInAndStatusIn(username,
                    email, roles, status);
        }

        allUserResponse.setUsersList(usersList);
        allUserResponse.setAllUserSize(allUserSize);

        return allUserResponse;
    }

    @Override
    public User partialUpdateUserById(Long userId, UserUpdateDto userDto) {
        Optional<User> user = userRepository.findById(userId);

        if (user.isPresent()) {
            String dtoPassword = userDto.getPassword();
            String dtoEmail = userDto.getEmail();
            String dtoUsername = userDto.getUsername();
            Set<Role> dtoRoles = userDto.getRoles();
            EStatus dtoStatus = userDto.getStatus();

            if (dtoEmail != null) {
                user.get().setEmail(dtoEmail);
            }
            if (dtoRoles.size() != 0) {
                user.get().setRoles(dtoRoles);
            }
            if (dtoStatus != null) {
                user.get().setStatus(dtoStatus);
            }
            if (dtoUsername != null) {
                user.get().setUsername(dtoUsername);
            }
            if (dtoPassword != null) {
                String encodedPassword = encoder.encode(dtoPassword);
                user.get().setPassword(encodedPassword);
            }

            userRepository.save(user.get());

            return user.get();
        } else {
            throw new ResourceNotFoundException(EntityConstant.PERSON, userId);
        }
    }

    @Override
    public Kit createKit(Kit kit) {
        return kitRepository.save(kit);
    }

    @Override
    public Locus createLocus(Locus locus, Integer kitId) {
        Optional<Kit> kit = kitRepository.findById(kitId);
        if (kit.isPresent()) {
            locus.setKit(kit.get());
            locusRepository.save(locus);
            return locus;
        } else {
            throw new ResourceNotFoundException(EntityConstant.KIT, kitId);
        }
    }

    @Override
    public MessageResponse uploadMap(MultipartFile map) throws IOException {
        Files.copy(map.getInputStream(), Path.of(String.format("%s%s", dirUpload, MAP_FILE_NAME)),
                StandardCopyOption.REPLACE_EXISTING);
        return new MessageResponse("success");
    }

    @Override
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    @Override
    public List<KitDto> getAllKitsAndLoci(String chromosome) {
        return kitRepository.findByChromosomeType(chromosome).stream().map(kitMapper::toKitDto).collect(Collectors.toList());
    }

    @Override
    public MessageResponse updateKitById(Integer kitId, Kit kit) {
        Optional<Kit> targetKit = kitRepository.findById(kitId);
        if (targetKit.isPresent()) {
            Kit kitInstance = targetKit.get();
            kitInstance.setKit(kit.getKit());
            kitRepository.save(kitInstance);
            return new MessageResponse("Success");
        } else {
            throw new ResourceNotFoundException(EntityConstant.KIT, kitId);
        }
    }

    @Override
    public MessageResponse deleteByKitId(Integer kitId) {
        kitRepository.deleteById(kitId);
        return new MessageResponse("Success");
    }

    @Override
    public MessageResponse deleteLocusById(Integer locusId) {
        locusRepository.deleteById(locusId);
        return new MessageResponse("Success");
    }

    @Override
    public MessageResponse updateLocusById(Integer locusId, Locus locus) {
        Optional<Locus> targetLocus = locusRepository.findById(locusId);
        if (targetLocus.isPresent()) {
            Locus curLocus = targetLocus.get();
            curLocus.setLocus(locus.getLocus());
            locusRepository.save(curLocus);
        } else {
            throw new ResourceNotFoundException(EntityConstant.LOCUS, locusId);
        }
        return new MessageResponse("Success");
    }

    @Override
    @Transactional
    public List<ExistingSampleId> isSampleIdsExist(List<String> sampleIds) {
        List<ExistingSampleId> result = new ArrayList<>();
        for (String sampleId : sampleIds) {
            Boolean isExisted = sampleRepository.existsBySampleId(sampleId);
            result.add(new ExistingSampleId(sampleId, isExisted));
        }
        return result;
    }
}
