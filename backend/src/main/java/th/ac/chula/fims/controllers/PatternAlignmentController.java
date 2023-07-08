package th.ac.chula.fims.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import th.ac.chula.fims.services.interfaces.PatternAlignmentService;

@RestController
@RequestMapping("/api/pattern-alignment")
public class PatternAlignmentController {
    private final PatternAlignmentService patternAlignmentService;

    public PatternAlignmentController(PatternAlignmentService patternAlignmentService) {
        this.patternAlignmentService = patternAlignmentService;
    }

    @GetMapping("/loci/{name}")
    public ResponseEntity<?> getAllPatternAlignmentLoci(@PathVariable String name) {
        return ResponseEntity.status(HttpStatus.OK).body(patternAlignmentService.getLociByName(name));
    }
}
