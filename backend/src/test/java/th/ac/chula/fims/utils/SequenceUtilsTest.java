package th.ac.chula.fims.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.Test;

public class SequenceUtilsTest {

    @Test
    public void reverseSequence() {
        String expectedSequence = "ATGCCGAT";
        String actualSequence = "ATCGGCAT";
        String reverseSequence = SequenceUtils.reverseSequence(actualSequence);
        Assertions.assertEquals(expectedSequence, reverseSequence);
    }

    @Test
    public void reverseRepeatMotifs() {
        String expectedSequence = "ATCG ATTT [ATGC]n GGAA cctt CTTA ";
        String actualSequence = "TAAG aagg TTCC [GCAT]n AAAT CGAT";
        String reverseSequence = SequenceUtils.reverseRepeatMotifs(actualSequence);
        Assertions.assertEquals(expectedSequence, reverseSequence);
    }

    @Test
    public void reverseRepeatMotifsPattern2() {
        String expectedSequence = "[ATGC]n GGAA cctt CTTA ";
        String actualSequence = "TAAG aagg TTCC [GCAT]n";
        String reverseSequence = SequenceUtils.reverseRepeatMotifs(actualSequence);
        Assertions.assertEquals(expectedSequence, reverseSequence);
    }
}