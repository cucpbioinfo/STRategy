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

    public static Map<String, Object> getPatternAlignment(List<PatternAlignmentMotif> motifs,
                                                          List<ChangedBase> changedBaseListToAdd,
                                                          List<ChangedBase> changedBaseListToDelete,
                                                          ForenseqSequence forenseqSequence, String alleleValue) {
        // Clear old change base list
        changedBaseListToDelete.addAll(forenseqSequence.getChangedBaseList());
        forenseqSequence.getChangedBaseList().clear();

        Map<String, Object> result = new HashMap<>();

        int curSeqIdx = 0;
        int allele = (int) Float.parseFloat(alleleValue);
        int alleleCounter = 0;
        String sequence = forenseqSequence.getSequence();
        List<String> patternAlignList = new ArrayList<>();

        for (int i = 0; i < motifs.size(); i++) {
            if (alleleCounter >= allele) {
                break;
            }

            PatternAlignmentMotif curMotifObj = motifs.get(i);

            String curMtf = curMotifObj.getMotif();
            boolean shouldItCount = curMtf.toUpperCase().equals(curMtf);
            int curMtfSiz = curMtf.length();

            String nextMtf = null;
            int nextMtfSiz = 0;

            if (i + 1 < motifs.size()) {
                PatternAlignmentMotif nextMotif = motifs.get(i + 1);
                nextMtf = nextMotif.getMotif();
                nextMtfSiz = nextMtf.length();
            }

            Integer numberOfRepeats = curMotifObj.getAmount();

            // Handle with generic nucleotide motif
            if (curMtf.contains("N")) {
                patternAlignList.add(sequence.substring(curSeqIdx, curSeqIdx + curMtf.length()));
                curSeqIdx += curMtf.length();
                continue;
            }

            int repeatCounter = 0;

            while (curSeqIdx + curMtfSiz <= sequence.length()) {
                if (numberOfRepeats != null && repeatCounter >= numberOfRepeats || alleleCounter >= allele || (nextMtf != null && sequence.substring(curSeqIdx, curSeqIdx + nextMtfSiz).equals(nextMtf.toUpperCase()))) {
                    break;
                }

                ChangedBaseDto cbDto = null;
                if (shouldItCount) {
                    cbDto = extractChangedBase(sequence.substring(curSeqIdx, curSeqIdx + curMtfSiz), curMtf, curSeqIdx);
                }
                int fTargetIndex = sequence.indexOf(curMtf.toUpperCase(), curSeqIdx);

                // Check fTargetIndex not far more than curSeqIdx + Size of current Motif
                if (cbDto == null && (fTargetIndex - curSeqIdx >= curMtfSiz || fTargetIndex == -1)) {
                    break;
                }

                // Check Insertion-Deletion
                if (cbDto == null && fTargetIndex > curSeqIdx) {
                    patternAlignList.add(sequence.substring(curSeqIdx, fTargetIndex));
                    curSeqIdx = fTargetIndex;
                }

                if (cbDto != null) {
                    ChangedBase cb = new ChangedBase(cbDto.getFrom(), cbDto.getTo(), cbDto.getPosition(),
                            forenseqSequence);
                    forenseqSequence.add(cb);
                    changedBaseListToAdd.add(cb);
                }

                String toAddSeq = sequence.substring(curSeqIdx, curSeqIdx + curMtfSiz);
                if (shouldItCount) {
                    patternAlignList.add(toAddSeq);
                    alleleCounter++;
                } else {
                    patternAlignList.add(toAddSeq.toLowerCase());
                }

                curSeqIdx += curMtfSiz;
                repeatCounter++;
            }
        }

        // Flanking
        if (curSeqIdx < sequence.length()) {
            patternAlignList.add(sequence.substring(curSeqIdx));
        }
        result.put(PATTERN_ALIGNMENT, listToStringPattern(patternAlignList));

        return result;
    }

    public static ChangedBaseDto extractChangedBase(String sequence, String motif, Integer currentPosition) {
        char from = 'X';
        char to = 'X';
        int position = 0;
        int changingCount = 0;

        if (sequence.length() != motif.length() || motif.length() < 3) {
            return null;
        }

        for (int i = 0; i < sequence.length(); i++) {
            if (sequence.charAt(i) != motif.charAt(i)) {
                from = motif.charAt(i);
                to = sequence.charAt(i);
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
