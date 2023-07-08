package th.ac.chula.fims.controllers;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import th.ac.chula.fims.models.Enum.ExcelExtension;
import th.ac.chula.fims.payload.request.ExcelExportParam;
import th.ac.chula.fims.payload.request.LocusAllele;
import th.ac.chula.fims.payload.response.MatchedSampleResponse;
import th.ac.chula.fims.payload.response.MessageResponse;
import th.ac.chula.fims.payload.response.person.PersonForenseq;
import th.ac.chula.fims.services.interfaces.PersonService;
import th.ac.chula.fims.services.interfaces.SampleService;

import java.time.Instant;
import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/api/samples")
public class SampleController {
    private final SampleService sampleService;

    private final PersonService personService;

    public SampleController(SampleService sampleService, PersonService personService) {
        this.sampleService = sampleService;
        this.personService = personService;
    }

    @PostMapping("/forenseq/export")
    public ResponseEntity<?> exportForenSeqAsExcelWithSpecificColumns(@RequestBody ExcelExportParam exportParam, @RequestParam(name = "version", defaultValue = "XLSX") ExcelExtension extension) {
        byte[] targetFile = sampleService.exportAsExcelWithSpecificColumns(exportParam, extension);

        String contentType = "application/octet-stream";
        return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\""
                        + Instant.now().toString() + (extension.equals(ExcelExtension.XLSX) ? ".xlsx" : ".xls")
                        + "\"")
                .body(targetFile);
    }

    @PostMapping("/person")
    public ResponseEntity<MatchedSampleResponse> getPersonsByLocusAllele(@RequestBody List<LocusAllele> lAlist) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Collection<? extends GrantedAuthority> rolesList = auth.getAuthorities();

        if (rolesList.stream().anyMatch(e -> e.getAuthority().equals("ROLE_ADMIN"))
                || rolesList.stream().anyMatch(e -> e.getAuthority().equals("ROLE_LAB_USER"))) {
            return ResponseEntity.status(HttpStatus.OK).body(sampleService.getPersonsByLocusAllele(lAlist, true));
        } else {
            return ResponseEntity.status(HttpStatus.OK).body(sampleService.getPersonsByLocusAllele(lAlist, false));
        }
    }

    @PostMapping("/person/excel")
    public ResponseEntity<?> getPersonByLocusAlleleExcel(@RequestParam("file") MultipartFile file) {
        try {
            List<LocusAllele> lAlist = sampleService.convertFileToLocusAlleleList(file);
            return ResponseEntity.status(HttpStatus.OK).body(getPersonsByLocusAllele(lAlist));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(new MessageResponse("Bad format file"));
        }
    }

    @GetMapping("/allele")
    public ResponseEntity<List<Float>> getAlleleByLocus(@RequestParam String locus) {
        return ResponseEntity.status(HttpStatus.OK).body(sampleService.getAlleleByLocus(locus));
    }

    @GetMapping("/{id}/forenseq")
    public ResponseEntity<?> getPersonForenseqById(@PathVariable String id) {
        PersonForenseq personForenseq = personService.getForenseqBySampleId(id);
        return ResponseEntity.status(HttpStatus.OK).body(personForenseq);
    }
}
