package th.ac.chula.fims.services.interfaces;

import org.springframework.stereotype.Service;
import th.ac.chula.fims.models.tables.PatternAlignmentLocus;

public interface PatternAlignmentService {
    PatternAlignmentLocus getLociByName(String name);
}
