package th.ac.chula.fims.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import th.ac.chula.fims.models.Enum.UploadProfileMode;
import th.ac.chula.fims.payload.dto.SearchProfileDto;
import th.ac.chula.fims.services.interfaces.ProfileSearchService;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/profile-search")
public class ProfileSearchController {
    private final ProfileSearchService profileSearchService;

    public ProfileSearchController(ProfileSearchService profileSearchService) {
        this.profileSearchService = profileSearchService;
    }

    @PutMapping("/")
    public ResponseEntity<?> uploadProfileSearchAll(@RequestParam("file") MultipartFile file) {
        try {
            return ResponseEntity.ok(profileSearchService.uploadExcelSearchProfileAll(file));
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.ok(profileSearchService.uploadCSVSearchProfileAll(file));
        }
    }

    @PutMapping("/{country}")
    public ResponseEntity<?> uploadProfileSearchAllByCountry(@RequestParam("file") MultipartFile file, @PathVariable String country, @RequestParam(defaultValue = "REPLACE") UploadProfileMode mode) {
        String capitalize = StringUtils.capitalize(country.toLowerCase());
        try {
            return ResponseEntity.ok(profileSearchService.uploadExcelSearchProfileByCountry(file, capitalize, mode));
        } catch (IOException e) {
            return ResponseEntity.ok(profileSearchService.uploadCSVSearchProfileByCountry(file, capitalize, mode));
        }
    }

    @PostMapping("/search")
    public ResponseEntity<?> searchProfileSearchAllByCountry(@RequestBody List<SearchProfileDto> searchProfileDtoList, @RequestParam(name = "algorithm", defaultValue = "default") String algorithm) {
        return ResponseEntity.ok(profileSearchService.searchProfileAll(algorithm, searchProfileDtoList));
    }
}
