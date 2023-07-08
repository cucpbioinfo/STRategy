package th.ac.chula.fims.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import th.ac.chula.fims.payload.dto.CountryDto;
import th.ac.chula.fims.payload.dto.ProvinceDto;
import th.ac.chula.fims.payload.dto.ProvinceUpdateDto;
import th.ac.chula.fims.services.interfaces.CountryService;

import java.util.List;

@RestController
@RequestMapping("/api/countries")
public class CountryController {
    private final CountryService countryService;

    public CountryController(CountryService countryService) {
        this.countryService = countryService;
    }

    @GetMapping("/")
    public ResponseEntity<?> getAllCountries() {
        List<CountryDto> countriesList = countryService.getAllCountries();
        return ResponseEntity.status(HttpStatus.OK).body(countriesList);
    }

    @GetMapping("/{countryId}")
    public ResponseEntity<?> getCountryById(@PathVariable int countryId) {
        CountryDto countriesList = countryService.getCountryById(countryId);
        return ResponseEntity.status(HttpStatus.OK).body(countriesList);
    }

    @GetMapping("/{countryId}/provinces")
    public ResponseEntity<?> getProvincesByCountryId(@PathVariable int countryId) {
        List<ProvinceDto> provinceDto = countryService.getProvincesByCountryId(countryId);
        return ResponseEntity.status(HttpStatus.OK).body(provinceDto);
    }

    @PutMapping("/{countryId}/provinces")
    public ResponseEntity<?> updateProvincesByCountryId(@PathVariable int countryId, @RequestBody ProvinceUpdateDto provinceUpdateDto) {
        countryService.updateProvincesByCountryId(countryId, provinceUpdateDto);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping("/")
    public ResponseEntity<?> addNewCountry(@RequestBody CountryDto country) {
        return ResponseEntity.status(HttpStatus.CREATED).body(countryService.addNewCountry(country));
    }

    @PutMapping("/")
    public ResponseEntity<?> updateCountryById(@RequestBody CountryDto country) {
        return ResponseEntity.status(HttpStatus.OK).body(countryService.updateCountryById(country));
    }
}
