package th.ac.chula.fims.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import th.ac.chula.fims.models.tables.Province;
import th.ac.chula.fims.models.tables.Region;
import th.ac.chula.fims.payload.dto.RegionDto;
import th.ac.chula.fims.services.interfaces.RegionService;

import java.util.List;

@RestController
@RequestMapping("/api/regions")
public class RegionController {
    private final RegionService regionService;

    public RegionController(RegionService regionService) {
        this.regionService = regionService;
    }

    @GetMapping("/")
    public ResponseEntity<?> getAllRegionsByCountryId(@RequestParam(name = "country_id") Integer countryId) {
        List<Region> regionsList = regionService.getRegionsByCountryId(countryId);
        return ResponseEntity.status(HttpStatus.OK).body(regionsList);
    }

    @PostMapping("/{countryId}")
    public ResponseEntity<?> addNewRegionByCountryId(@PathVariable Integer countryId, @RequestBody RegionDto regionDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(regionService.addNewRegionByCountryId(countryId, regionDto));
    }

    @PutMapping("/")
    public ResponseEntity<?> updateRegionById(@RequestBody RegionDto region) {
        return ResponseEntity.status(HttpStatus.OK).body(regionService.updateRegionById(region));
    }

    @DeleteMapping("/{regionId}")
    public ResponseEntity<?> deleteRegionById(@PathVariable int regionId) {
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(regionService.deleteRegionById(regionId));
    }

    @GetMapping("/{regionId}/provinces")
    public ResponseEntity<?> getProvincesByRegionId(@PathVariable Integer regionId) {
        List<Province> provincesList = regionService.getProvincesByRegionId(regionId);
        return ResponseEntity.status(HttpStatus.OK).body(provincesList);
    }
}
