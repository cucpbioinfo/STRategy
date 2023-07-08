package th.ac.chula.fims.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import th.ac.chula.fims.services.interfaces.CoreLociService;

import java.io.IOException;

@RestController
@RequestMapping("/api/core-loci")
public class CoreLociController {
    private final CoreLociService coreLociService;

    public CoreLociController(CoreLociService coreLociService) {

        this.coreLociService = coreLociService;
    }

    @GetMapping("/")
    public ResponseEntity<?> getCoreLociList(@RequestParam(name = "country") String country) {
        return ResponseEntity.ok(coreLociService.getCoreLociByCountry(country));
    }

    @GetMapping("/countries")
    public ResponseEntity<?> getAllCountryInCoreLoci() {
        return ResponseEntity.ok(coreLociService.getAllCountryInCoreLoci());
    }

    @PutMapping("/")
    public ResponseEntity<?> uploadCoreLociList(@RequestParam("file") MultipartFile file) throws IOException {
        return ResponseEntity.ok(coreLociService.uploadCoreLociByCountry(file));
    }
}
