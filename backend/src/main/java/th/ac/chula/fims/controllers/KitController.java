package th.ac.chula.fims.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import th.ac.chula.fims.payload.response.statistics.KitLocusListResponse;
import th.ac.chula.fims.services.interfaces.KitService;

@RestController
@RequestMapping("/api/kits")
public class KitController {
    private final KitService kitService;

    public KitController(KitService kitService) {
        this.kitService = kitService;
    }

    @GetMapping("/all")
    public ResponseEntity<KitLocusListResponse> getAllKits() {
        return ResponseEntity.status(HttpStatus.OK).body(kitService.getAllKits());
    }
}
