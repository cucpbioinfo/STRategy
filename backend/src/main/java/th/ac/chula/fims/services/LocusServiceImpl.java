package th.ac.chula.fims.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import th.ac.chula.fims.payload.response.statistics.ChromosomeLociResponse;
import th.ac.chula.fims.repository.tables.LocusRepository;
import th.ac.chula.fims.services.interfaces.LocusService;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class LocusServiceImpl implements LocusService {
    private final LocusRepository locusRepository;

    @Value("${locus.chart.exclude}")
    private Set<String> excludedLocus;

    public LocusServiceImpl(LocusRepository locusRepository) {
        this.locusRepository = locusRepository;
    }

    @Override
    public ChromosomeLociResponse getAllLocus() {
        List<Object[]> allList = locusRepository.findDistinctAllLocus();
        List<String> aList = new ArrayList<>();
        List<String> xList = new ArrayList<>();
        List<String> yList = new ArrayList<>();

        for (Object[] row : allList) {
            String locus = row[0].toString();
            String chromosome = row[1].toString();
            if (excludedLocus.contains(locus)) {
                continue;
            }
            switch (chromosome) {
                case "Autosome":
                    aList.add(locus);
                    break;
                case "X":
                    xList.add(locus);
                    break;
                case "Y":
                    yList.add(locus);
                    break;
                default:
                    break;
            }
        }

        return new ChromosomeLociResponse(aList, xList, yList);
    }

    @Override
    public List<String> getAllGlobalLocus() {
        return locusRepository.findAllGlobalLocus();
    }

    @Override
    public List<String> getAllIsnpLocus() {
        return locusRepository.findDistinctLocusByISNP();
    }
}
