package th.ac.chula.fims.services.interfaces;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.web.multipart.MultipartFile;
import th.ac.chula.fims.models.Enum.EGender;
import th.ac.chula.fims.models.tables.CEData;
import th.ac.chula.fims.models.tables.Person;
import th.ac.chula.fims.models.tables.Sample;
import th.ac.chula.fims.payload.dto.ExcelPersonDataUploadResponse;
import th.ac.chula.fims.payload.response.person.SequenceDetail;

import javax.transaction.Transactional;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

public interface FileService {
    @Transactional
    Boolean readExcelForenseqData(MultipartFile file, String sampleId) throws IOException;

    Workbook openCompatibleWorkbook(String fileName, InputStream fis) throws IOException;

    void extractForenseqData(Sample sample, Sheet sheet,
                             Map<String, SequenceDetail> locusAllele, int startSummaryLine, int endSummaryLine, String chromosomeTag);

    void extractInfo(Person personInfo, Sample sample, Sheet sheet);

    ExcelPersonDataUploadResponse readExcelPersonData(MultipartFile file) throws IOException;

    void savePerson(Person person, List<String> data, List<List<String>> notSuccessPerson);

    @Transactional
    Boolean readTextFileCEData(MultipartFile file) throws IOException;

    @Transactional
    CEData extractCEData(String[] columns, String locus);

    void extractSample(Map<String, Sample> sampleMap, String[] columns);

    EGender deriveGender(String sampleId);
}
