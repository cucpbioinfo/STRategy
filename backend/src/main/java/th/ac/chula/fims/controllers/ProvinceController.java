package th.ac.chula.fims.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import th.ac.chula.fims.payload.dto.ProvinceDto;
import th.ac.chula.fims.services.interfaces.ProvinceService;

import java.util.List;

@RestController
@RequestMapping("/api/provinces")
public class ProvinceController {
    private final ProvinceService provinceService;

    public ProvinceController(ProvinceService provinceService) {
        this.provinceService = provinceService;
    }

    @GetMapping("/")
    public ResponseEntity<?> getProvincesByRegionId(@RequestParam(name = "ids") List<Integer> ids) {
        List<ProvinceDto> provincesList = provinceService.getProvincesByIds(ids);
        return ResponseEntity.status(HttpStatus.OK).body(provincesList);
    }
}
