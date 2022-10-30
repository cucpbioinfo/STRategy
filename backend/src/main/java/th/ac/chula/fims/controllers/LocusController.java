package th.ac.chula.fims.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import th.ac.chula.fims.services.interfaces.LocusService;

@RestController
@RequestMapping("/api/loci")
public class LocusController {
    private final LocusService locusService;

    public LocusController(LocusService locusService) {
        this.locusService = locusService;
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllLocus() {
        return ResponseEntity.status(HttpStatus.OK).body(locusService.getAllLocus());
    }

    @GetMapping("/global")
    public ResponseEntity<?> getAllGlobalLocus() {
        return ResponseEntity.status(HttpStatus.OK).body(locusService.getAllGlobalLocus());
    }

    @GetMapping("/isnp")
    public ResponseEntity<?> getAllIsnpLocus() {
        return ResponseEntity.status(HttpStatus.OK).body(locusService.getAllIsnpLocus());
    }
}
