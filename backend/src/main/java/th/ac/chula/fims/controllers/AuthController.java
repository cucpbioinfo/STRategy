package th.ac.chula.fims.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import th.ac.chula.fims.models.Enum.ERole;
import th.ac.chula.fims.models.Enum.EStatus;
import th.ac.chula.fims.models.Role;
import th.ac.chula.fims.models.User;
import th.ac.chula.fims.payload.request.ChangePasswordRequest;
import th.ac.chula.fims.payload.request.LoginRequest;
import th.ac.chula.fims.payload.request.SignupRequest;
import th.ac.chula.fims.payload.response.MessageResponse;
import th.ac.chula.fims.payload.response.auth.JwtResponse;
import th.ac.chula.fims.repository.RoleRepository;
import th.ac.chula.fims.repository.UserRepository;
import th.ac.chula.fims.security.CurrentUser;
import th.ac.chula.fims.security.jwt.JwtUtils;
import th.ac.chula.fims.security.services.UserDetailsImpl;

import javax.validation.Valid;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthenticationManager authenticationManager;

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final PasswordEncoder encoder;

    private final JwtUtils jwtUtils;

    public AuthController(AuthenticationManager authenticationManager, UserRepository userRepository,
                          RoleRepository roleRepository, PasswordEncoder encoder, JwtUtils jwtUtils) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.encoder = encoder;
        this.jwtUtils = jwtUtils;
    }

    @GetMapping("/status")
    public ResponseEntity<?> checkToken() {
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        EStatus userStatus = userDetails.getStatus();

        if (userStatus.equals(EStatus.BLOCK)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new MessageResponse("Contact an admin."));
        } else if (userStatus.equals(EStatus.DELETE)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponse("Username not found."));
        } else if (userStatus.equals(EStatus.NOT_ACCEPT)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new MessageResponse("Contact an admin to " +
                    "granting login access."));
        }

        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        return ResponseEntity.ok(new JwtResponse(jwt,
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getEmail(),
                roles));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Username is already taken!"));
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Email is already in use!"));
        }

        // Create new user's account
        String encodedPwd = encoder.encode(signUpRequest.getPassword());
        User user =
                User.builder().username(signUpRequest.getUsername()).password(encodedPwd).email(signUpRequest.getEmail()).status(EStatus.NOT_ACCEPT).build();

        Set<String> strRoles = signUpRequest.getRole();
        Set<Role> roles = new HashSet<>();

        if (strRoles == null) {
            Role userRole = roleRepository.findByName(ERole.ROLE_LAB_USER)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(userRole);
        } else {
            strRoles.forEach(role -> {
                switch (role) {
                    case "admin":
                        Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                                .orElseThrow(() -> new RuntimeException("Error: Role ADMIN is not found."));
                        roles.add(adminRole);

                        break;
                    case "lab_user":
                        Role modRole = roleRepository.findByName(ERole.ROLE_LAB_USER)
                                .orElseThrow(() -> new RuntimeException("Error: Role LAB_USER is not found."));
                        roles.add(modRole);

                        break;
                    default:
                        Role userRole = roleRepository.findByName(ERole.ROLE_LAB_USER)
                                .orElseThrow(() -> new RuntimeException("Error: Role USER is not found."));
                        roles.add(userRole);
                }
            });
        }

        user.setRoles(roles);
        userRepository.save(user);

        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }

    @GetMapping("/me")
    public ResponseEntity<?> getMyProfile(@CurrentUser UserDetailsImpl currentUser) {
        return ResponseEntity.ok(currentUser);
    }

    @PostMapping("/change-pwd")
    public ResponseEntity<?> changePasswordUser(@CurrentUser UserDetailsImpl currentUser,
                                                @Valid @RequestBody ChangePasswordRequest changePasswordRequest) {
        String oldPassword = changePasswordRequest.getOldPassword();
        String newPassword = changePasswordRequest.getNewPassword();

        boolean isValidPwd = encoder.matches(oldPassword, currentUser.getPassword());
        if (isValidPwd) {
            Optional<User> user = userRepository.findById(currentUser.getId());
            if (user.isPresent()) {
                User userUpdate = user.get();
                String encodedPwd = encoder.encode(newPassword);

                userUpdate.setPassword(encodedPwd);
                userRepository.save(userUpdate);

                return ResponseEntity.ok(new MessageResponse("Password has changed"));
            }

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponse("User not found"));
        }

        return ResponseEntity.badRequest().body(new MessageResponse("Old password is not correct."));
    }
}
