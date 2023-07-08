package th.ac.chula.fims.services;

import org.springframework.stereotype.Service;
import th.ac.chula.fims.constants.EntityConstant;
import th.ac.chula.fims.exceptions.ResourceNotFoundException;
import th.ac.chula.fims.models.tables.Race;
import th.ac.chula.fims.repository.tables.RaceRepository;
import th.ac.chula.fims.services.interfaces.RaceService;

import java.util.List;
import java.util.Optional;

@Service
public class RaceServiceImpl implements RaceService {
    private final RaceRepository raceRepository;

    public RaceServiceImpl(RaceRepository raceRepository) {
        this.raceRepository = raceRepository;
    }

    @Override
    public List<Race> getAllRaces() {
        return raceRepository.findAll();
    }

    @Override
    public Race addNewRace(Race race) {
        Race newRace = new Race();
        System.out.println(race.getRace());
        newRace.setRace(race.getRace());
        return raceRepository.save(newRace);
    }

    @Override
    public Race updateRaceById(int raceId, Race updateRace) {
        Optional<Race> optionalRace = raceRepository.findById(raceId);
        if (optionalRace.isPresent()) {
            Race race = optionalRace.get();
            race.setRace(updateRace.getRace());
            return raceRepository.save(race);
        }
        throw new ResourceNotFoundException(EntityConstant.RACE, raceId);
    }

    @Override
    public void deleteRaceById(int raceId) {
        raceRepository.deleteById(raceId);
    }
}
