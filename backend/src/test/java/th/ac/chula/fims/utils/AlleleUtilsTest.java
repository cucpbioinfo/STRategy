package th.ac.chula.fims.utils;

import org.junit.Test;
import org.junit.jupiter.api.Assertions;

public class AlleleUtilsTest {
    @Test
    public void IsValidGenotypeWithHaploid() {
        boolean isValidGenotype = AlleleUtils.isValidGenotype("5");
        Assertions.assertTrue(isValidGenotype);
    }

    @Test
    public void IsValidGenotypeWithDiploid() {
        boolean isValidGenotype = AlleleUtils.isValidGenotype("5,7");
        Assertions.assertTrue(isValidGenotype);
    }

    @Test
    public void IsValidGenotypeWithInvalidCharacter() {
        boolean isValidGenotype = AlleleUtils.isValidGenotype("INC");
        Assertions.assertFalse(isValidGenotype);
    }


    @Test
    public void IsValidGenotypeWithInvalidDiploid() {
        boolean isValidGenotype = AlleleUtils.isValidGenotype("G,A");
        Assertions.assertFalse(isValidGenotype);
    }

    @Test
    public void resolveGenotypeWithDecimalEndWithZero() {
        String genotype = AlleleUtils.resolveGenotypeFromCsv("10.0");
        Assertions.assertEquals("10", genotype);
    }

    @Test
    public void resolveGenotypeWithDecimalEndWithOtherNumber() {
        String genotype = AlleleUtils.resolveGenotypeFromCsv("10.2");
        Assertions.assertEquals("10.2", genotype);
    }

    @Test
    public void resolveGenotypeWithDiploid() {
        String genotype = AlleleUtils.resolveGenotypeFromCsv("10,15");
        Assertions.assertEquals("10,15", genotype);
    }


    @Test
    public void resolveGenotypeWithDiploidDecimalFirst() {
        String genotype = AlleleUtils.resolveGenotypeFromCsv("10.2,15");
        Assertions.assertEquals("10.2,15", genotype);
    }

    @Test
    public void resolveGenotypeWithDiploidDecimalSecond() {
        String genotype = AlleleUtils.resolveGenotypeFromCsv("10,15.3");
        Assertions.assertEquals("10,15.3", genotype);
    }

    @Test
    public void resolveGenotypeWithDiploidDecimalBith() {
        String genotype = AlleleUtils.resolveGenotypeFromCsv("10.2,15.3");
        Assertions.assertEquals("10.2,15.3", genotype);
    }

    @Test
    public void resolveGenotypeWithINC() {
        String genotype = AlleleUtils.resolveGenotypeFromCsv("INC");
        Assertions.assertEquals("INC", genotype);
    }
}