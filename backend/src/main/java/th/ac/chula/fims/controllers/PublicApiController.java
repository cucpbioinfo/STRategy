package th.ac.chula.fims.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import th.ac.chula.fims.payload.dto.SearchProfileDto;
import th.ac.chula.fims.services.interfaces.CoreLociService;
import th.ac.chula.fims.services.interfaces.ForenseqSequenceService;
import th.ac.chula.fims.services.interfaces.LocusService;
import th.ac.chula.fims.services.interfaces.ProfileSearchService;

import java.util.List;

@RestController
@RequestMapping("/public-api")
public class PublicApiController {

    private final CoreLociService coreLociService;

    private final ProfileSearchService profileSearchService;

    private final LocusService locusService;

    private final ForenseqSequenceService forenseqSequenceService;

    public PublicApiController(CoreLociService coreLociService, ProfileSearchService profileSearchService, LocusService locusService, ForenseqSequenceService forenseqSequenceService) {
        this.coreLociService = coreLociService;
        this.profileSearchService = profileSearchService;
        this.locusService = locusService;
        this.forenseqSequenceService = forenseqSequenceService;
    }

    @GetMapping("/loci/all")
    public ResponseEntity<?> getAllLocus() {
        return ResponseEntity.status(HttpStatus.OK).body(locusService.getAllLocus());
    }

    @GetMapping("/forenseq-sequences/graph")
    public ResponseEntity<?> getGraphInfoByChroAndLocus(@RequestParam String locus) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(forenseqSequenceService.getGraphInfoByLocus(locus));
    }

    @GetMapping("/forenseq-sequences/map")
    public ResponseEntity<?> getMapInfoByLocus(@RequestParam String locus) {
        return ResponseEntity.status(HttpStatus.OK).body(forenseqSequenceService.getMapInfoByLocus(locus));
    }

    @GetMapping("/core-loci")
    public ResponseEntity<?> getCoreLociList(@RequestParam(name = "country") String country) {
        return ResponseEntity.ok(coreLociService.getCoreLociByCountry(country));
    }

    @GetMapping("/core-loci/countries")
    public ResponseEntity<?> getAllCountryInCoreLoci() {
        return ResponseEntity.ok(coreLociService.getAllCountryInCoreLoci());
    }

    @PostMapping("/profile-search/search")
    public ResponseEntity<?> searchProfileSearchAllByCountry(@RequestBody List<SearchProfileDto> searchProfileDtoList, @RequestParam(name = "algorithm", defaultValue = "default") String algorithm) {
        return ResponseEntity.ok(profileSearchService.searchProfileAll(algorithm, searchProfileDtoList));
    }
}
