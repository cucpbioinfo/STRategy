package th.ac.chula.fims.controllers;

import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import th.ac.chula.fims.payload.dto.EventHistoryDto;
import th.ac.chula.fims.repository.tables.PatternAlignmentMotifRepository;
import th.ac.chula.fims.services.interfaces.ForenseqSequenceService;

@RestController
@RequestMapping("/api/forenseq-sequences")
public class ForenseqSequenceController {
    private final ForenseqSequenceService forenseqSequenceService;

    public ForenseqSequenceController(ForenseqSequenceService forenseqSequenceService,
                                      PatternAlignmentMotifRepository patternAlignmentMotifRepository) {
        this.forenseqSequenceService = forenseqSequenceService;
    }

    @GetMapping("/graph")
    public ResponseEntity<?> getGraphInfoByChroAndLocus(@RequestParam String locus) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(forenseqSequenceService.getGraphInfoByLocus(locus));
    }

    @GetMapping("/map")
    public ResponseEntity<?> getMapInfoByLocus(@RequestParam String locus) {
        return ResponseEntity.status(HttpStatus.OK).body(forenseqSequenceService.getMapInfoByLocus(locus));
    }

    @PutMapping("/pattern-alignment/calculation")
    private ResponseEntity<?> generatePatternAlignment() {
        forenseqSequenceService.calculateAllLocusAndAlleleOfPatternAlignmentAndSaveToDatabase();
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("/pattern-alignment/calculation")
    private ResponseEntity<?> getLastGenerationDetail() {
        EventHistoryDto lastPatternAlignmentGeneration = forenseqSequenceService.getLastPatternAlignmentGeneration();
        return ResponseEntity.status(HttpStatus.OK).body(lastPatternAlignmentGeneration);
    }

    @GetMapping("/pattern-alignment")
    public ResponseEntity<?> getPatternAlignmentByLocusAndAllele(@RequestParam String locus,
                                                                 @RequestParam String allele,
                                                                 @RequestParam(
                                                                         name = "fetch",
                                                                         required = false,
                                                                         defaultValue = "false"
                                                                 ) Boolean isFetch,
                                                                 Pageable pageable) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(forenseqSequenceService
                        .getPatternAlignmentByLocusAndAllele(locus, allele, isFetch, pageable));
    }

    @GetMapping("/isnp")
    public ResponseEntity<?> getISNPStats(@RequestParam(name = "query", defaultValue = "rs") String searchWord,
                                          Integer page, Integer size) {
        return ResponseEntity.status(HttpStatus.OK).body(forenseqSequenceService.getStatsISNP(size, page, searchWord));
    }
}
