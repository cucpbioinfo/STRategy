package th.ac.chula.fims.utils;

import th.ac.chula.fims.models.tables.ChangedBase;
import th.ac.chula.fims.models.tables.ForenseqSequence;
import th.ac.chula.fims.models.tables.PatternAlignmentMotif;
import th.ac.chula.fims.payload.response.statistics.ChangedBaseDto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PatternAlignmentAlgorithmUtils {
    private static final String PATTERN_ALIGNMENT = "patternAlignment";

    /**
     * Precedence of algorithm
     * 1. Exact match with the current motif at the current index
     * 2. Match with Next referenced Motif (Indel's size must not be greater than or equals to size of the next
     * referenced motif)
     * 1st Example:
     * AAGG|CCATACATACATACA
     * currentIdx = 4
     * currentMotif = AAAG
     * nextMotif = TACA
     * System will mark CCA as an indel because this indel's size is not greater or equal 4 (next motif size)
     * <p>
     * 2nd Example:
     * AAGG|CCCATACATACATACA
     * currentIdx = 4
     * currentMotif = AAAG
     * nextMotif = TACA
     * System will not mark CCCA as an indel because this indel's size is greater than or equals 4 (next motif size)
     * <p>
     * 3. Insertion and deletion (Indel's size must be not greater than or equal to size of the current ref motif)
     * 4. One-base substitution
     *
     * @param motifs                  the referenced motifs
     * @param changedBaseListToAdd
     * @param changedBaseListToDelete
     * @param forenseqSequence        ForenSeq Sequence Object to analyze pattern alignment
     * @param alleleValue             Allele
     * @return
     */
    public static Map<String, Object> getPatternAlignment(List<PatternAlignmentMotif> motifs,
                                                          List<ChangedBase> changedBaseListToAdd,
                                                          List<ChangedBase> changedBaseListToDelete,
                                                          ForenseqSequence forenseqSequence, String alleleValue) {
        // Clear old change base list
        changedBaseListToDelete.addAll(forenseqSequence.getChangedBaseList());
        forenseqSequence.getChangedBaseList().clear();

        float allele = Float.parseFloat(alleleValue);

//        if (((int) allele) == allele) {
//            return analyzePatternAlignmentWithOutIndels(motifs, changedBaseListToAdd, forenseqSequence, (int) allele);
//        } else {
        return analyzePatternAlignment(motifs, changedBaseListToAdd, forenseqSequence, (int) allele,
                Integer.parseInt(String.valueOf(allele).split("\\.")[1]));
//        }
    }

    private static Map<String, Object> analyzePatternAlignment(List<PatternAlignmentMotif> motifs,
                                                               List<ChangedBase> changedBaseListToAdd,
                                                               ForenseqSequence forenseqSequence, int allele,
                                                               int indelSize) {
        Map<String, Object> result = new HashMap<>();
        int currentIndex = 0;
        int alleleCounter = 0;
        String sequence = forenseqSequence.getSequence();
        List<String> patternAlignList = new ArrayList<>();

        for (int i = 0; i < motifs.size(); i++) {
            if (alleleCounter >= allele) {
                break;
            }

            PatternAlignmentMotif curMotifObj = motifs.get(i);

            // Get current motif
            String curMtf = curMotifObj.getMotif();

            int curMtfSiz = curMtf.length();

            String nextMtf = null;

            // If they have next motif, assign its value nextMotif variables.
            if (i + 1 < motifs.size()) {
                PatternAlignmentMotif nextMotif = motifs.get(i + 1);
                nextMtf = nextMotif.getMotif();
            }

            // If the motif from reference STR repeat motifs is lowercase we won't count this as repeat unit
            // For example: [CCTT]n ccta [CCTT]n cttt [CCTT]n is reference STR repeat motifs of D19S433
            // When the current motif is ccta and cttt, shouldItCount will be false
            boolean shouldItCount = curMtf.toUpperCase().equals(curMtf);
            curMtf = curMtf.toUpperCase();

            // Handle with generic nucleotide motif
            // For example: [AGAGAT]n N42 [AGAGAT]n is reference STR repeat motifs of DYS448
            // This case will handle for N42
            if (curMtf.contains("N")) {
                patternAlignList.add(sequence.substring(currentIndex, currentIndex + curMtf.length()));
                currentIndex += curMtf.length();
                continue;
            }

            // This case handle for fixed motif with number of repeat
            // For example: [AAGA]n gaaagga [AAGA]n AAAG is reference STR repeat motifs of DXS10135
            // numberOfRepeats of AAGA and AAGA will be null
            // numberOfRepeats of gaaagga and AAAG will be 1
            Integer numberOfRepeats = curMotifObj.getAmount();

            // repeatCounter will be track that number of loop is more than numberOfRepeats or not
            int repeatCounter = 0;

            while (currentIndex + curMtfSiz <= sequence.length()) {
                if (numberOfRepeats != null && repeatCounter >= numberOfRepeats) {
                    break;
                }

                if (alleleCounter >= allele) {
                    break;
                }

                int matchedIndexNextMotif = -1;
                if (nextMtf != null) {
                    matchedIndexNextMotif = sequence.indexOf(nextMtf.toUpperCase(), currentIndex);
                }

                int matchedIndexCurMotif = sequence.indexOf(curMtf.toUpperCase(), currentIndex);

                // Matched Next Motif with an inDel
                if (matchedIndexCurMotif != currentIndex && matchedIndexNextMotif != -1 && matchedIndexNextMotif - currentIndex == indelSize) {
                    break;
                }

                // Matched Next motif without inDel
                if (matchedIndexCurMotif != currentIndex && matchedIndexNextMotif != -1 && matchedIndexNextMotif == currentIndex) {
                    break;
                }

                ChangedBaseDto cbDto = findSingleNucleotideSubstitution(sequence.substring(currentIndex,
                                currentIndex + curMtfSiz),
                        curMtf, currentIndex, shouldItCount);

                // If matched sub-sequence far from current motif size or not found
                // and doesn't have one-base substitution,
                // go to next motif.
                if (cbDto == null && matchedIndexCurMotif != currentIndex && matchedIndexCurMotif - currentIndex != indelSize) {
                    break;
                }

                // Check Insertion-Deletion
                // InDels will take precedence over one-base substitution
                // as long as indel's size is not greater than or equal current motif's size
                if (matchedIndexCurMotif != currentIndex && matchedIndexCurMotif - currentIndex == indelSize) {
                    patternAlignList.add(sequence.substring(currentIndex, matchedIndexCurMotif));
                    currentIndex = matchedIndexCurMotif;

                    // Remove one-base substitution if an inDel exist
                    cbDto = null;
                }

                // Check substitution for 1 base in this motif
                if (cbDto != null) {
                    ChangedBase cb = new ChangedBase(cbDto.getFrom(), cbDto.getTo(), cbDto.getPosition(),
                            forenseqSequence);
                    forenseqSequence.add(cb);
                    changedBaseListToAdd.add(cb);
                }

                // Add current motif to sequence
                String toAddSeq = sequence.substring(currentIndex, currentIndex + curMtfSiz);

                // that means sub-sequence will be added, but alleteCounter won't increase
                if (shouldItCount) {
                    patternAlignList.add(toAddSeq);
                    alleleCounter++;
                } else {
                    patternAlignList.add(toAddSeq.toLowerCase());
                }

                currentIndex += curMtfSiz;
                repeatCounter++;
            }
        }

        // Flanking
        if (currentIndex < sequence.length()) {
            String flanking = sequence.substring(currentIndex);
            patternAlignList.add(flanking);
        }
        result.put(PATTERN_ALIGNMENT, listToStringPattern(patternAlignList));

        return result;
    }

    private static boolean isMatchForNextMotif(int curSeqIdx, String sequence, String curMtf, int curMtfSiz,
                                               String nextMtf, int nextMtfSiz) {
        if (nextMtf == null) {
            return false;
        }

        if (sequence.substring(curSeqIdx, curSeqIdx + curMtfSiz).equals(curMtf.toUpperCase())) {
            return false;
        }

        int pos = sequence.indexOf(nextMtf.toUpperCase());

        if (pos == -1) {
            return false;
        }

        return pos - curSeqIdx < nextMtfSiz;
    }

    public static ChangedBaseDto findSingleNucleotideSubstitution(String sequence, String motif,
                                                                  Integer currentPosition,
                                                                  boolean shouldItCount) {
        char from = 'X';
        char to = 'X';
        int position = 0;
        int changingCount = 0;

        if (sequence.length() != motif.length() || motif.length() < 3) {
            return null;
        }

        for (int i = 0; i < sequence.length(); i++) {
            if (sequence.charAt(i) != motif.charAt(i)) {
                if (shouldItCount) {
                    from = motif.charAt(i);
                    to = sequence.charAt(i);
                } else {
                    from = motif.toLowerCase().charAt(i);
                    to = sequence.toLowerCase().charAt(i);
                }
                changingCount++;
                position = i;
            }
        }

        if (changingCount != 1) {
            return null;
        }

        return new ChangedBaseDto(from, to, currentPosition + position);
    }

    public static String listToStringPattern(List<String> patternList) {
        StringBuilder result = new StringBuilder();
        if (patternList.size() == 0) return "";
        if (patternList.size() < 2) return patternList.get(0);
        int patternCounter = 0;
        String curPattern;
        String nextPattern = "";

        for (int i = 0; i < patternList.size() - 1; i++) {
            curPattern = patternList.get(i);
            nextPattern = patternList.get(i + 1);
            patternCounter++;
            if (!curPattern.equals(nextPattern)) {
                if (patternCounter == 1) {
                    result.append(String.format("%s ", curPattern));
                } else {
                    result.append(String.format("[%s]%d ", curPattern, patternCounter));
                }
                patternCounter = 0;
            }
        }

        patternCounter += 1;

        if (patternCounter == 1) {
            result.append(String.format("%s ", nextPattern));
        } else {
            result.append(String.format("[%s]%d ", nextPattern, patternCounter));
        }

        return result.toString();
    }
}
