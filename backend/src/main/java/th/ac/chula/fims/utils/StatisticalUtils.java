package th.ac.chula.fims.utils;

import th.ac.chula.fims.models.tables.Forenseq;
import th.ac.chula.fims.payload.projection.ForenSequenceFull;
import th.ac.chula.fims.payload.response.statistics.HomoHeteroValue;
import th.ac.chula.fims.payload.response.statistics.amount.AlleleAmount;
import th.ac.chula.fims.payload.response.statistics.amount.AlleleAmountContainer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StatisticalUtils {
    public static HomoHeteroValue calculateHomoHeteroValues(List<Forenseq> forenseqList) {
        int homozygous = 0;
        int heterozygous = 0;
        int total = 0;
        for (Forenseq forenseq : forenseqList) {
            String genotype = forenseq.getGenotype();
            if (!AlleleUtils.isValidGenotype(genotype)) continue;

            String[] alleles = genotype.split(",");
            if (alleles.length != 2) continue;

            if (alleles[0].equals(alleles[1])) {
                homozygous++;
            } else {
                heterozygous++;
            }
            total++;
        }
        return new HomoHeteroValue(homozygous, heterozygous, total);
    }
}
