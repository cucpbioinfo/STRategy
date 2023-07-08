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

    @Test
    public void testAlgorithm3() {
        ForenseqSequence forenseqSequence = new ForenseqSequence();
        List<PatternAlignmentMotif> motif = new ArrayList<>();
        motif.add(new PatternAlignmentMotif(0, 1, "ATCT", null, null));
        forenseqSequence.setSequence(
                "ATCTATCTATCTATCTATCTATGTATCTATCTATCATCTATCTATCTATCTATCTATCTATCTATCTATCTATCTATCTATCT");
        forenseqSequence.setChangedBaseList(new ArrayList<>());
        Map<String, Object> patternAlignment = PatternAlignmentAlgorithmUtils.getPatternAlignment(motif,
                new ArrayList<>(), new ArrayList<>(), forenseqSequence, "20.3");
        System.out.println(patternAlignment);
    }

    @Test
    public void testAlgorithm4() {
        ForenseqSequence forenseqSequence = new ForenseqSequence();
        List<PatternAlignmentMotif> motif = new ArrayList<>();
        motif.add(new PatternAlignmentMotif(0, 1, "TATC", null, null));
        forenseqSequence.setSequence(
                "TATCTATCTATCTATCTATCTATCTATCTATCTGTCTATCTATCTATCAATCATCTATCTATCTTTCTGTCTGTC");
        forenseqSequence.setChangedBaseList(new ArrayList<>());
        Map<String, Object> patternAlignment = PatternAlignmentAlgorithmUtils.getPatternAlignment(motif,
                new ArrayList<>(), new ArrayList<>(), forenseqSequence, "20.3");
        System.out.println(patternAlignment);
    }

    @Test
    public void testAlgorithm5() {
        ForenseqSequence forenseqSequence = new ForenseqSequence();
        List<PatternAlignmentMotif> motif = new ArrayList<>();
        motif.add(new PatternAlignmentMotif(1, 0, "TAGA", null, null));
        motif.add(new PatternAlignmentMotif(2, 1, "ctga", 1, null));
        motif.add(new PatternAlignmentMotif(3, 2, "CAGA", 1, null));
        motif.add(new PatternAlignmentMotif(4, 3, "TAGA", null, null));
        motif.add(new PatternAlignmentMotif(5, 4, "CAGA", null, null));
        motif.add(new PatternAlignmentMotif(6, 5, "TAGA", 1, null));
        forenseqSequence.setSequence(
                "TAGATTGACAGATAGATAGATAGATAGATAGATAGATAGATAGATAGATAGATAGATAGACAGACAGACAGACAGATAGA");
        forenseqSequence.setChangedBaseList(new ArrayList<>());
        Map<String, Object> patternAlignment = PatternAlignmentAlgorithmUtils.getPatternAlignment(motif,
                new ArrayList<>(), new ArrayList<>(), forenseqSequence, "20.3");
        System.out.println(patternAlignment);
    }

    @Test
    public void testAlgorithm6() {
        ForenseqSequence forenseqSequence = new ForenseqSequence();
        List<PatternAlignmentMotif> motif = new ArrayList<>();
        motif.add(new PatternAlignmentMotif(1, 0, "AGAGAT", null, null));
        motif.add(new PatternAlignmentMotif(2, 1, "NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN", 1, null));
        motif.add(new PatternAlignmentMotif(3, 2, "AGAGAT", null, null));
        forenseqSequence.setSequence(
                "AGAGATAGAGATAGAGATAGAGATAGAGATAGAGATAGAGATAGAGATAGAGATAGAGATATAGAGATAGAGAGATAGAGATAGAGATAGATAGATAGAGAAAGAGATAGAGATAGAGATAGAGATAGAGATAGAGATAGAGAT");
        forenseqSequence.setChangedBaseList(new ArrayList<>());
        Map<String, Object> patternAlignment = PatternAlignmentAlgorithmUtils.getPatternAlignment(motif,
                new ArrayList<>(), new ArrayList<>(), forenseqSequence, "17");
        System.out.println(patternAlignment);
    }

    @Test
    public void testAlgorithm7() {
        ForenseqSequence forenseqSequence = new ForenseqSequence();
        List<PatternAlignmentMotif> motif = new ArrayList<>();
        motif.add(new PatternAlignmentMotif(1, 0, "ATCT", null, null));
        forenseqSequence.setSequence(
                "ATCTATCTATCTATCTATCTATCTATCTATCTATCTATCTATCTATCT");
        forenseqSequence.setChangedBaseList(new ArrayList<>());
        Map<String, Object> patternAlignment = PatternAlignmentAlgorithmUtils.getPatternAlignment(motif,
                new ArrayList<>(), new ArrayList<>(), forenseqSequence, "12.0");
        System.out.println(patternAlignment);
    }

    @Test
    public void testAlgorithm8() {
        ForenseqSequence forenseqSequence = new ForenseqSequence();
        List<PatternAlignmentMotif> motif = new ArrayList<>();
        motif.add(new PatternAlignmentMotif(1, 0, "TCTA", null, null));
        forenseqSequence.setSequence(
                "ATCTATCTATCTATCTATCTATCTATCTATCTATCTA");
        forenseqSequence.setChangedBaseList(new ArrayList<>());
        Map<String, Object> patternAlignment = PatternAlignmentAlgorithmUtils.getPatternAlignment(motif,
                new ArrayList<>(), new ArrayList<>(), forenseqSequence, "9.1");
        System.out.println(patternAlignment);
    }

    @Test
    public void testAlgorithm9() {
        ForenseqSequence forenseqSequence = new ForenseqSequence();
        List<PatternAlignmentMotif> motif = new ArrayList<>();
        motif.add(new PatternAlignmentMotif(1, 0, "AAGA", null, null));
        motif.add(new PatternAlignmentMotif(1, 0, "gaaagga", 1, null));
        motif.add(new PatternAlignmentMotif(1, 0, "AAGA", null, null));
        motif.add(new PatternAlignmentMotif(1, 0, "AAAG", 1, null));
        forenseqSequence.setSequence(
                "AAGAAAGAAAGAGAAAGGAAAGAAAGAAAGAAAGAAAGAAAGAAAGAAAGAAAGAAAGAAAGAAAGAAAGAAAGAAAGAAAGAAAGAAAGAAAGAAAGGAAGGAAGGAAGAAAAGAGAATAGAAAAGAAGAGAAGAGAAAAGAGAAAAGAAAAAAGAAAAGAAA");
        forenseqSequence.setChangedBaseList(new ArrayList<>());
        Map<String, Object> patternAlignment = PatternAlignmentAlgorithmUtils.getPatternAlignment(motif,
                new ArrayList<>(), new ArrayList<>(), forenseqSequence, "27.0");
        System.out.println(patternAlignment);
    }
}