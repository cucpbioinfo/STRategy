package th.ac.chula.fims.utils;

import th.ac.chula.fims.models.tables.Forenseq;
import th.ac.chula.fims.payload.response.statistics.amount.AlleleAmount;
import th.ac.chula.fims.payload.response.statistics.amount.DiploidHaploidAlleleAmount;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AlleleUtils {
    public static DiploidHaploidAlleleAmount extractAlleleToCount(List<Forenseq> forenseqs) {
        List<AlleleAmount> diploidAllele = extractDiploidAllele(forenseqs);
        List<AlleleAmount> haploidAllele = extractHaploidAllele(forenseqs);

        return new DiploidHaploidAlleleAmount(diploidAllele, haploidAllele);
    }

    public static List<AlleleAmount> extractDiploidAllele(List<Forenseq> forenseqs) {
        ArrayList<AlleleAmount> diploidAllele = new ArrayList<>();
        Map<String, Integer> hashedAllele = new HashMap<>();

        for (Forenseq fs : forenseqs) {
            String genotype = fs.getGenotype();
            List<String> alleleList = List.of(genotype.split(","));
            if (alleleList.size() != 2) continue;
            for (String allele : alleleList) {
                if (hashedAllele.containsKey(allele)) {
                    hashedAllele.put(allele, hashedAllele.get(allele) + 1);
                } else {
                    hashedAllele.put(allele, 1);
                }
            }
        }

        return getAlleleAmounts(diploidAllele, hashedAllele);
    }

    public static List<AlleleAmount> extractHaploidAllele(List<Forenseq> forenseqs) {
        ArrayList<AlleleAmount> haploidAllele = new ArrayList<>();
        Map<String, Integer> hashedAllele = new HashMap<>();

        for (Forenseq fs : forenseqs) {
            String genotype = fs.getGenotype();
            List<String> alleleList = List.of(genotype.split(","));
            if (alleleList.size() != 1) continue;
            for (String allele : alleleList) {
                if (hashedAllele.containsKey(allele)) {
                    hashedAllele.put(allele, hashedAllele.get(allele) + 1);
                } else {
                    hashedAllele.put(allele, 1);
                }
            }
        }

        return getAlleleAmounts(haploidAllele, hashedAllele);
    }

    private static List<AlleleAmount> getAlleleAmounts(ArrayList<AlleleAmount> alleleAmounts,
                                                       Map<String, Integer> hashedAllele) {
        for (Map.Entry<String, Integer> entSet : hashedAllele.entrySet()) {
            try {
                alleleAmounts.add(new AlleleAmount(Float.parseFloat(entSet.getKey()), entSet.getValue()));
            } catch (NumberFormatException ex) {
                alleleAmounts.add(new AlleleAmount(0f, entSet.getValue()));
            }
        }

        return alleleAmounts;
    }

    public static boolean isValidGenotype(String genotype) {
        String[] alleles = genotype.split(",");
        return NumberUtils.isNumeric(alleles[0]);
    }

    public static String resolveGenotypeFromCsv(String genotype) {
        String[] alleles = genotype.split(",");
        if (alleles.length == 1 && NumberUtils.isNumeric(alleles[0])) {
            return genotype.replace(".0", "");
        }
        return genotype;
    }

    public static boolean isDiploid(String genotype) {
        String[] alleles = genotype.split(",");
        if (alleles.length != 2) {
            return false;
        }
        return NumberUtils.isNumeric(alleles[0]) && NumberUtils.isNumeric(alleles[1]);
    }

    public static boolean isHaploid(String genotype) {
        String[] alleles = genotype.split(",");
        if (alleles.length != 1) {
            return false;
        }
        return NumberUtils.isNumeric(alleles[0]);
    }
}
