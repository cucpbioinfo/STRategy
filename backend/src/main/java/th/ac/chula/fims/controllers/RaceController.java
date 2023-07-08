package th.ac.chula.fims.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import th.ac.chula.fims.models.tables.Race;
import th.ac.chula.fims.services.interfaces.RaceService;

import java.util.List;

@RestController
@RequestMapping("/api/v2/races")
public class RaceController {
    private final RaceService raceService;

    public RaceController(RaceService raceService) {
        this.raceService = raceService;
    }

    @GetMapping("/")
    public ResponseEntity<?> getAllRaces() {
        List<Race> raceList = raceService.getAllRaces();
        return ResponseEntity.status(HttpStatus.OK).body(raceList);
    }

    @PostMapping("/")
    public ResponseEntity<?> addRace(@RequestBody Race newRace) {
        System.out.println(newRace);
        Race race = raceService.addNewRace(newRace);
        return ResponseEntity.status(HttpStatus.OK).body(race);
    }

    @PutMapping("/{raceId}")
    public ResponseEntity<?> updateRaceById(@PathVariable int raceId,@RequestBody Race updateRace){
        Race race = raceService.updateRaceById(raceId, updateRace);
        return ResponseEntity.status(HttpStatus.OK).body(race);
    }

    @DeleteMapping("/{raceId}")
    public ResponseEntity<?> deleteRaceById(@PathVariable int raceId) {
        raceService.deleteRaceById(raceId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
