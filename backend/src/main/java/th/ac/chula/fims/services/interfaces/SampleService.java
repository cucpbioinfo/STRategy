package th.ac.chula.fims.services.interfaces;

import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.web.multipart.MultipartFile;
import th.ac.chula.fims.models.Enum.ExcelExtension;
import th.ac.chula.fims.payload.request.ExcelExportParam;
import th.ac.chula.fims.payload.request.LocusAllele;
import th.ac.chula.fims.payload.response.MatchedSampleResponse;

import javax.transaction.Transactional;
import java.util.List;

public interface SampleService {
    @Transactional
    MatchedSampleResponse getPersonsByLocusAllele(List<LocusAllele> lAList, Boolean isAuth);

    List<Float> getAlleleByLocus(String locus);

    List<LocusAllele> convertFileToLocusAlleleList(MultipartFile file) throws Exception;

    void extractForenseqData(Sheet sheet, List<LocusAllele> lAList, int startSummaryLine, int endSummaryLine);

    byte[] exportAsExcelWithSpecificColumns(ExcelExportParam exportParam, ExcelExtension extension);
}
