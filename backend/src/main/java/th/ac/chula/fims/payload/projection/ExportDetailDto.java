package th.ac.chula.fims.payload.projection;

public interface ExportDetailDto {

    String getSample_id();

    String getSample_year();

    String getAllele();

    String getSequence();

    String getRepeatMotif();

    default String getSampleId() {
        return getSample_id();
    }

    default String getSampleYear() {
        return getSample_year();
    }

    default String getToString() {
        return String.format("%s/%s/%s/%s/%s", getSample_id(), getSample_year(), getAllele(), getSequence(), getRepeatMotif());
    }
}
