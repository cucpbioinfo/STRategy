package th.ac.chula.fims.services;

import org.springframework.stereotype.Service;
import th.ac.chula.fims.constants.EntityConstant;
import th.ac.chula.fims.exceptions.ResourceNotFoundException;
import th.ac.chula.fims.models.tables.PatternAlignmentLocus;
import th.ac.chula.fims.repository.tables.PatternAlignmentLocusRepository;
import th.ac.chula.fims.services.interfaces.PatternAlignmentService;

import java.util.Optional;

@Service
public class PatternAlignmentServiceImpl implements PatternAlignmentService {
    private final PatternAlignmentLocusRepository patternAlignmentLocusRepository;

    public PatternAlignmentServiceImpl(PatternAlignmentLocusRepository patternAlignmentLocusRepository) {
        this.patternAlignmentLocusRepository = patternAlignmentLocusRepository;
    }

    @Override
    public PatternAlignmentLocus getLociByName(String name) {
        Optional<PatternAlignmentLocus> locusInstance = patternAlignmentLocusRepository.findByLocus(name);
        if (locusInstance.isPresent()) {
            return locusInstance.get();
        }
        throw new ResourceNotFoundException(EntityConstant.LOCUS, "name", name);
    }
}
