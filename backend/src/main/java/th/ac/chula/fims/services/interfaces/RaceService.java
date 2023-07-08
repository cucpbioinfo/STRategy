package th.ac.chula.fims.services.interfaces;

import th.ac.chula.fims.models.tables.Race;

import java.util.List;

public interface RaceService {
    List<Race> getAllRaces();

    Race addNewRace(Race race);

    Race updateRaceById(int raceId, Race updateRace);

    void deleteRaceById(int raceId);
}
