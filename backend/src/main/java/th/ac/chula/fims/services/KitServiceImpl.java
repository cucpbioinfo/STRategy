package th.ac.chula.fims.services;

import org.springframework.stereotype.Service;
import th.ac.chula.fims.models.tables.Kit;
import th.ac.chula.fims.payload.response.statistics.KitLocusListResponse;
import th.ac.chula.fims.repository.tables.KitRepository;
import th.ac.chula.fims.services.interfaces.KitService;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class KitServiceImpl implements KitService {
    private final KitRepository kitRepository;

    public KitServiceImpl(KitRepository kitRepository) {
        this.kitRepository = kitRepository;
    }

    @Override
    @Transactional
    public KitLocusListResponse getAllKits() {
        List<Kit> aKits = kitRepository.findByChromosomeType("a").stream()
                .map(e -> new Kit(e.getId(), e.getKit(), null, null)).collect(Collectors.toList());
        List<Kit> yKits = kitRepository.findByChromosomeType("y").stream()
                .map(e -> new Kit(e.getId(), e.getKit(), null, null)).collect(Collectors.toList());
        List<Kit> xKits = kitRepository.findByChromosomeType("x").stream()
                .map(e -> new Kit(e.getId(), e.getKit(), null, null)).collect(Collectors.toList());
        return new KitLocusListResponse(aKits, yKits, xKits);
    }
}
