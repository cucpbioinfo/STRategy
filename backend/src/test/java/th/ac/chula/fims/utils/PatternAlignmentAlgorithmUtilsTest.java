package th.ac.chula.fims.utils;

import org.junit.Test;
import th.ac.chula.fims.models.tables.ForenseqSequence;
import th.ac.chula.fims.models.tables.PatternAlignmentMotif;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PatternAlignmentAlgorithmUtilsTest {
    @Test
    public void testAlgorithm() {
        ForenseqSequence forenseqSequence = new ForenseqSequence();
        List<PatternAlignmentMotif> motif = new ArrayList<>();
        motif.add(new PatternAlignmentMotif(0, 0, "ATCG", null, null));
        motif.add(new PatternAlignmentMotif(1, 1, "GGGG", 1, null));
        motif.add(new PatternAlignmentMotif(2, 2, "ATCC", null, null));
        forenseqSequence.setSequence("ATCGATCGATCGATCCATCCATCCAAGGTT");
        forenseqSequence.setChangedBaseList(new ArrayList<>());
        Map<String, Object> patternAlignment = PatternAlignmentAlgorithmUtils.getPatternAlignment(motif,
                new ArrayList<>(), new ArrayList<>(), forenseqSequence, "5");
        System.out.println(patternAlignment);
    }

    @Test
    public void testAlgorithm2() {
        ForenseqSequence forenseqSequence = new ForenseqSequence();
        List<PatternAlignmentMotif> motif = new ArrayList<>();
        motif.add(new PatternAlignmentMotif(0, 0,
                "NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN", 1, null));
        motif.add(new PatternAlignmentMotif(1, 1, "GAAA", null, null));
        forenseqSequence.setSequence(
                "AAGGAAGGAAGGAAGGAGAAAGAAAGTAAAAAAGAAAGAAAGAGAAAAAGAGAAAAAGAAAGAAAGAGAAGAAAGAGAAAGAGGAAAGAGAAAGAAAGGAAGGAAGGAAGGAAGGAAGGGAAAGAAAGAAAGAAAGAAAGAAAGAAAGAAAGAAAGAAAGAAAGAAAGAAAGAAAGAGAAAAAGAAA");
        forenseqSequence.setChangedBaseList(new ArrayList<>());
        Map<String, Object> patternAlignment = PatternAlignmentAlgorithmUtils.getPatternAlignment(motif,
                new ArrayList<>(), new ArrayList<>(), forenseqSequence, "14");
        System.out.println(patternAlignment);
    }
}