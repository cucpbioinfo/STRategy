package th.ac.chula.fims.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import th.ac.chula.fims.services.interfaces.SummaryDataService;

@RestController
@RequestMapping("/api/summaries")
public class SummaryDataController {
    private final SummaryDataService sumDataService;

    public SummaryDataController(SummaryDataService sumDataService) {
        this.sumDataService = sumDataService;
    }

    @GetMapping("/locus/{locus}")
    public ResponseEntity<?> getSummariesDataByLocus(@PathVariable String locus) {
        return ResponseEntity.status(HttpStatus.OK).body(sumDataService.getSummaryDataByLocus(locus));
    }

    @GetMapping("/dashboard")
    public ResponseEntity<?> getDashboardSummariesData() {
        return ResponseEntity.status(HttpStatus.OK).body(sumDataService.getDashboardSummaries());
    }
}
