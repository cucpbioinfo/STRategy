package th.ac.chula.fims.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import th.ac.chula.fims.models.tables.configuration.Configuration;
import th.ac.chula.fims.services.interfaces.ConfigurationService;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/configuration")
public class ConfigurationController {
    final private ConfigurationService configurationService;

    public ConfigurationController(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    @GetMapping("/keys")
    public ResponseEntity<?> getConfigurationByConfigurationKey(@RequestParam(name = "keys") List<String> configurationKey) {
        return ResponseEntity.status(HttpStatus.OK).body(configurationService.getConfigurationByConfigurationKey(configurationKey));
    }

    @PatchMapping("/keys")
    public ResponseEntity<?> updateConfigurationByKeys(@RequestBody List<Configuration> configurations) throws Exception {
        return ResponseEntity.status(HttpStatus.OK).body(configurationService.updateConfigurationByKeys(configurations));
    }

    @PutMapping("/referenced-str-patterns")
    public ResponseEntity<?> updateSequenceGuide(@RequestParam("file") MultipartFile file) throws IOException {
        return ResponseEntity.status(HttpStatus.OK).body(configurationService.updateReferencePatterns(file));
    }
}
