package th.ac.chula.fims.utils;

import th.ac.chula.fims.models.tables.PatternAlignmentMotif;
import th.ac.chula.fims.payload.response.statistics.amount.AlleleSeqAmount;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SequenceUtils {
    public static List<String> convertRepeatMotifsToStringList(String seq) {
        if (seq == null) return new ArrayList<>();
        List<String> stringList = Arrays.stream(seq.split(" ")).filter(e -> !e.equals("")).collect(Collectors.toList());
        List<String> result = new ArrayList<>();

        for (String mtf : stringList) {
            result.addAll(extractSingleMotifList(mtf));
        }

        return result;
    }

    public static List<String> extractSingleMotifList(String motifStr) {
        List<String> result = new ArrayList<>();

        if (motifStr.startsWith("[")) {
            List<String> stringList = Arrays.stream(motifStr.replaceAll("\\[", "")
                    .split("]")).collect(Collectors.toList());
            int numberOfInt = Integer.parseInt(stringList.get(1));

            for (int i = 0; i < numberOfInt; i++) {
                result.add(stringList.get(0));
            }
        } else if (motifStr.startsWith("N")) {
            if (motifStr.length() == 1) {
                result.add(motifStr);
            } else {
                result.add("N".repeat(Math.max(0, Integer.parseInt(motifStr.substring(1)))));
            }
        } else {
            result.add(motifStr);
        }

        return result;
    }

//    public static List<MotifDto> convertRepeatMotifsToListOfMotif(String seq) {
//        List<String> stringList = Arrays.stream(seq.split(" ")).filter(e -> !e.equals("")).collect(Collectors
//        .toList());
//        List<MotifDto> result = new ArrayList<>();
//
//        for (String str : stringList) {
//            List<String> temp = extractSingleMotifToPair(str);
//            result.add(new MotifDto(temp.get(0), temp.get(1).equals("n") ? -1 : Integer.parseInt(temp.get(1))));
//        }
//
//        return result;
//    }

    public static List<PatternAlignmentMotif> convertRepeatMotifsToListOfMotif(String seq) {
        List<String> stringList = Arrays.stream(seq.split(" ")).filter(e -> !e.equals("")).collect(Collectors.toList());
        List<PatternAlignmentMotif> result = new ArrayList<>();

        for (String str : stringList) {
            List<String> temp = extractSingleMotifToPair(str);
            result.add(new PatternAlignmentMotif(temp.get(0), temp.get(1).equals("n") ? -1 :
                    Integer.parseInt(temp.get(1))));
        }

        return result;
    }

    public static String convertListOfMotifToRepeatMotifs(List<PatternAlignmentMotif> patternAlignmentMotifs) {
        StringBuilder result = new StringBuilder();

        for (PatternAlignmentMotif patternAlignmentMotif : patternAlignmentMotifs) {
            String motif = patternAlignmentMotif.getMotif();
            Integer amount = patternAlignmentMotif.getAmount();
            if (amount > 1) {
                result.append(String.format("[%s]%d ", motif, amount));
            } else if (amount == 1) {
                result.append(String.format("%s ", motif));
            } else {
                result.append(String.format("[%s]n ", motif));
            }
        }

        return result.toString();
    }

    public static List<String> extractSingleMotifToPair(String motifStr) {
        if (motifStr.startsWith("[")) {
            return Arrays.stream(motifStr.replaceAll("\\[", "")
                    .split("]")).collect(Collectors.toList());
        } else if (motifStr.startsWith("N")) {
            List<String> result = new ArrayList<>();
            if (motifStr.length() == 1) {
                result.add(motifStr);
            } else {
                result.add("N".repeat(Math.max(0, Integer.parseInt(motifStr.substring(1)))));
            }
            result.add("1");
            return result;
        } else {
            List<String> result = new ArrayList<>();
            result.add(motifStr);
            result.add("1");
            return result;
        }
    }

//    public static List<MotifDto> reverseMotif(List<MotifDto> original) {
//        List<MotifDto> result = new ArrayList<>();
//        for (int i = original.size() - 1; i >= 0; i--) {
//            MotifDto currentMotif = original.get(i);
//            String motif = currentMotif.getMotif();
//            Integer amount = currentMotif.getAmount();
//
//            StringBuilder newMotif = new StringBuilder();
//            for (String base : motif.split("")) {
//                newMotif.append(toggleBase(base));
//            }
//
//            newMotif.reverse();
//            result.add(new MotifDto(newMotif.toString(), amount));
//        }
//        return result;
//    }

    public static List<PatternAlignmentMotif> reverseMotif(List<PatternAlignmentMotif> original) {
        List<PatternAlignmentMotif> result = new ArrayList<>();
        for (int i = original.size() - 1; i >= 0; i--) {
            PatternAlignmentMotif currentMotif = original.get(i);
            String motif = currentMotif.getMotif();
            Integer amount = currentMotif.getAmount();

            StringBuilder newMotif = new StringBuilder();
            for (String base : motif.split("")) {
                newMotif.append(toggleBase(base));
            }

            newMotif.reverse();
            result.add(new PatternAlignmentMotif(currentMotif.getId(), currentMotif.getSeqNo(), newMotif.toString(),
                    amount, currentMotif.getPatternAlignmentAllele()));
        }
        return result;
    }

    public static List<AlleleSeqAmount> mergeAlleleSeqAmountList(List<AlleleSeqAmount> firstList,
                                                                 List<AlleleSeqAmount> secondList) {
        List<AlleleSeqAmount> ASAList =
                Stream.concat(firstList.stream(), secondList.stream()).collect(Collectors.toList());
        Map<String, AlleleSeqAmount> result = new HashMap<>();
        for (AlleleSeqAmount alleleSeqAmount : ASAList) {
            Float allele = alleleSeqAmount.getAllele();
            String sequence = alleleSeqAmount.getSequence();
            String repeatMotif = alleleSeqAmount.getRepeatMotif();
            int amount = alleleSeqAmount.getAmount();
            int fsId = alleleSeqAmount.getFsId();
            String key = allele + sequence;

            AlleleSeqAmount curASA = result.get(key);
            if (curASA == null) {
                AlleleSeqAmount newAlleleSeqAmount = new AlleleSeqAmount();
                newAlleleSeqAmount.setAmount(amount);
                newAlleleSeqAmount.setSequence(sequence);
                newAlleleSeqAmount.setFsId(fsId);
                newAlleleSeqAmount.setRepeatMotif(repeatMotif);
                newAlleleSeqAmount.setAllele(allele);
                result.put(key, newAlleleSeqAmount);
            } else {
                curASA.setAmount(curASA.getAmount() + amount);
            }
        }

        return new ArrayList<>(result.values());
    }

    public static String toggleBase(String original) {
        switch (original) {
            case "A":
                return "T";
            case "a":
                return "t";
            case "T":
                return "A";
            case "t":
                return "a";
            case "C":
                return "G";
            case "c":
                return "g";
            case "G":
                return "C";
            case "g":
                return "c";
            default:
                return original;
        }
    }

    public static String reverseSequence(String sequence) {
        StringBuilder result = new StringBuilder();
        for (int i = sequence.length() - 1; i >= 0; i--) {
            String currentBase = String.valueOf(sequence.charAt(i));
            result.append(toggleBase(currentBase));
        }
        return result.toString();
    }

    public static String reverseRepeatMotifs(String repeatMotif) {
        List<PatternAlignmentMotif> patternAlignmentMotifs = convertRepeatMotifsToListOfMotif(repeatMotif);
        List<PatternAlignmentMotif> reversedPatternAlignmentMotifs = reverseMotif(patternAlignmentMotifs);

        return convertListOfMotifToRepeatMotifs(reversedPatternAlignmentMotifs);
    }
}
