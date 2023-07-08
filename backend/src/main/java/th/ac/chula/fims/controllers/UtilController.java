package th.ac.chula.fims.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import th.ac.chula.fims.models.tables.PatternAlignmentMotif;
import th.ac.chula.fims.utils.SequenceUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/utils")
public class UtilController {

    @GetMapping("/seq/extract")
    public ResponseEntity<?> getSummariesDataByLocus(@RequestParam String sequence) {
        Map<String, List<PatternAlignmentMotif>> result = new HashMap<>();
        List<PatternAlignmentMotif> normalMtf = SequenceUtils.convertRepeatMotifsToListOfMotif(sequence);
        result.put("Normal", normalMtf);
        result.put("Reverse", SequenceUtils.reverseMotif(normalMtf));
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }
}
