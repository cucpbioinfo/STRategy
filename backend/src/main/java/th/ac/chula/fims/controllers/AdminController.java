package th.ac.chula.fims.controllers;

import org.springframework.core.io.Resource;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import th.ac.chula.fims.models.Enum.ERole;
import th.ac.chula.fims.models.Enum.EStatus;
import th.ac.chula.fims.models.tables.Kit;
import th.ac.chula.fims.models.tables.Locus;
import th.ac.chula.fims.payload.dto.UserUpdateDto;
import th.ac.chula.fims.services.interfaces.AdminService;
import th.ac.chula.fims.utils.FileServiceUtils;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/admins")
public class AdminController {
    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/users")
    public ResponseEntity<?> getAllUser(@RequestParam(required = false, defaultValue = "%") String username,
                                        @RequestParam(required = false, defaultValue = "%") String email,
                                        @RequestParam(required = false) List<EStatus> status,
                                        @RequestParam(required = false) List<ERole> roles,
                                        Pageable pageable) {
        return ResponseEntity.status(HttpStatus.OK).body(adminService.getAllUser(username, email, status, roles, pageable));
    }

    @GetMapping("/samples/existing")
    public ResponseEntity<?> isSampleIdExist(@RequestParam(name = "ids") List<String> sampleIds) {
        return ResponseEntity.status(HttpStatus.OK).body(adminService.isSampleIdsExist(sampleIds));
    }

    @GetMapping("/kits")
    public ResponseEntity<?> getAllKitsAndLociByChromosome(@RequestParam String chromosome) {
        return ResponseEntity.status(HttpStatus.OK).body(adminService.getAllKitsAndLoci(chromosome));
    }

    @PostMapping("/kits")
    public ResponseEntity<?> createANewKit(@RequestBody Kit kit) {
        return ResponseEntity.status(HttpStatus.OK).body(adminService.createKit(kit));
    }

    @PutMapping("/kits/{kitId}")
    public ResponseEntity<?> updateAKit(@PathVariable Integer kitId, @RequestBody Kit kit) {
        return ResponseEntity.status(HttpStatus.OK).body(adminService.updateKitById(kitId, kit));
    }

    @DeleteMapping("/kits/{kitId}")
    public ResponseEntity<?> deleteKitById(@PathVariable Integer kitId) {
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(adminService.deleteByKitId(kitId));
    }

    @DeleteMapping("/loci/{locusId}")
    public ResponseEntity<?> deleteLocusById(@PathVariable Integer locusId) {
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(adminService.deleteLocusById(locusId));
    }

    @PutMapping("/loci/{locusId}")
    public ResponseEntity<?> updateLocusById(@PathVariable Integer locusId, @RequestBody Locus locus) {
        return ResponseEntity.status(HttpStatus.OK).body(adminService.updateLocusById(locusId, locus));
    }

    @PostMapping("/kits/{kit}/loci")
    public ResponseEntity<?> createLocusByAKit(@RequestBody Locus locus, @PathVariable(name = "kit") Integer kitId) {
        return ResponseEntity.status(HttpStatus.OK).body(adminService.createLocus(locus, kitId));
    }

    @PatchMapping("/users/{userId}")
    public ResponseEntity<?> partialUpdateUserById(@RequestBody UserUpdateDto user, @PathVariable Long userId) {
        return ResponseEntity.status(HttpStatus.OK).body(adminService.partialUpdateUserById(userId, user));
    }

    @GetMapping("/statuses")
    public ResponseEntity<?> getAllStatuses() {
        return ResponseEntity.status(HttpStatus.OK).body(EStatus.values());
    }

    @GetMapping("/roles")
    public ResponseEntity<?> getAllRoles() {
        return ResponseEntity.status(HttpStatus.OK).body(adminService.getAllRoles());
    }

    @PostMapping("/maps/upload")
    public ResponseEntity<?> uploadMap(@RequestParam("map") MultipartFile map) throws IOException {
        return ResponseEntity.status(HttpStatus.OK).body(adminService.uploadMap(map));
    }

    @GetMapping("/maps")
    public ResponseEntity<?> downloadMap() throws IOException {
        Resource targetFile = FileServiceUtils.downloadFileAsResource("uploads/maps.json");

        String contentType = "application/json";

        return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
                .body(targetFile);
    }
}
