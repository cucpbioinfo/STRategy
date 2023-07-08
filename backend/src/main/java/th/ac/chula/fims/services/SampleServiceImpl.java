package th.ac.chula.fims.services;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.WorkbookUtil;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import th.ac.chula.fims.models.Enum.ExcelExtension;
import th.ac.chula.fims.payload.dto.CountryCountDto;
import th.ac.chula.fims.payload.projection.*;
import th.ac.chula.fims.payload.request.ExcelExportParam;
import th.ac.chula.fims.payload.request.LocusAllele;
import th.ac.chula.fims.payload.response.MatchedSampleResponse;
import th.ac.chula.fims.payload.response.person.SampleIDYear;
import th.ac.chula.fims.repository.tables.ForenseqSequenceRepository;
import th.ac.chula.fims.repository.tables.PersonRepository;
import th.ac.chula.fims.repository.tables.SampleRepository;
import th.ac.chula.fims.services.interfaces.SampleService;
import th.ac.chula.fims.utils.CSVUtils;

import javax.transaction.Transactional;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SampleServiceImpl implements SampleService {
    private final SampleRepository sampleRepository;
    private final ForenseqSequenceRepository forenseqSequenceRepository;
    private final PersonRepository personRepository;

    public SampleServiceImpl(SampleRepository sampleRepository, ForenseqSequenceRepository forenseqSequenceRepository, PersonRepository personRepository) {
        this.sampleRepository = sampleRepository;
        this.forenseqSequenceRepository = forenseqSequenceRepository;
        this.personRepository = personRepository;
    }

    @Override
    @Transactional
    public MatchedSampleResponse getPersonsByLocusAllele(List<LocusAllele> lAList, Boolean isAuth) {
        List<Integer> personIdList = new ArrayList<>();
        List<SampleIDYear> matchedSP = sampleRepository.searchMatchedSample(lAList).stream()
                .map(obj -> {
                    Long id = Long.valueOf(String.valueOf(obj[0]));
                    String sampleId = String.valueOf(obj[1]);
                    int sampleYear = Integer.parseInt(String.valueOf(obj[2]));
                    int personId = Integer.parseInt(String.valueOf(obj[3]));
                    personIdList.add(personId);

                    return new SampleIDYear(id, sampleId, sampleYear);
                })
                .collect(Collectors.toList());

        List<CountryCount> amountGroupByCountry = personRepository.findByIdsAndGroupByCountry(personIdList);
        List<RaceCount> amountGroupByRace = personRepository.findByIdsAndGroupByRace(personIdList);
        List<CountryCountDto> countryCountDtos = new ArrayList<>();

        for (CountryCount countryCount : amountGroupByCountry) {
            List<ProvinceCount> amountGroupByProvince = personRepository.findByIdsAndCountryIdGroupByProvince(personIdList, countryCount.getCountryId());
            List<RegionCount> amountGroupByRegion = personRepository.findByIdsAndCountryIdGroupByRegion(personIdList, countryCount.getCountryId());

            CountryCountDto countryCountDto = new CountryCountDto();
            countryCountDto.setCountry(countryCount.getCountry());
            countryCountDto.setAmount(countryCount.getAmount());
            countryCountDto.setProvinceCounts(amountGroupByProvince);
            countryCountDto.setRegionCounts(amountGroupByRegion);

            countryCountDtos.add(countryCountDto);
        }

        if (isAuth) {
            return new MatchedSampleResponse(matchedSP, (int) sampleRepository.count(), matchedSP.size(), countryCountDtos,
                    amountGroupByRace);
        } else {
            return new MatchedSampleResponse(new ArrayList<>(), (int) sampleRepository.count(), matchedSP.size(), countryCountDtos,
                    amountGroupByRace);
        }
    }

    @Override
    public List<Float> getAlleleByLocus(String locus) {
        return sampleRepository.findAlleleByLocus(locus);
    }

    @Override
    public List<LocusAllele> convertFileToLocusAlleleList(MultipartFile file) throws Exception {
        Workbook workbook = WorkbookFactory.create(file.getInputStream());
        List<LocusAllele> lAList = new ArrayList<>();
        int numberOfSheets = workbook.getNumberOfSheets();
        for (int i = 0; i < numberOfSheets; i++) {
            Sheet sheet = workbook.getSheetAt(i);
            String sheetName = sheet.getSheetName();

            switch (sheetName) {
                case "Autosomal STRs":
                    extractForenseqData(sheet, lAList, 13, 40);
                    break;
                case "Y STRs":
                    extractForenseqData(sheet, lAList, 13, 36);
                    break;
                case "X STRs":
                    extractForenseqData(sheet, lAList, 13, 19);
                    break;
                case "iSNPs":
                    extractForenseqData(sheet, lAList, 12, 105);
                    break;
            }

        }
        return lAList;
    }

    @Override
    public void extractForenseqData(Sheet sheet, List<LocusAllele> lAList, int startSummaryLine, int endSummaryLine) {
        int line = 1;
        for (Row row : sheet) {
            List<String> data = CSVUtils.getCells(row);
            if (line >= endSummaryLine + 3) {
                if (data.get(2).equals("Yes")) {
                    lAList.add(new LocusAllele(data.get(0), data.get(1)));
                }
            }

            if (!data.get(0).equals("n/a") && !data.get(0).equals("")) {
                line++;
            }
        }
    }

    @Override
    public byte[] exportAsExcelWithSpecificColumns(ExcelExportParam exportParam, ExcelExtension extension) {
        List<String> lociList = exportParam.getLoci();
        List<String> columnList = exportParam.getColumns();

        Workbook wb;
        if (extension == ExcelExtension.XLSX) {
            wb = new XSSFWorkbook();
        } else {
            wb = new HSSFWorkbook();
        }

        for (String locus : lociList) {
            String sheetName = WorkbookUtil.createSafeSheetName(locus);
            Sheet sheet = wb.createSheet(sheetName);
            List<ExportDetailDto> exportDetails = forenseqSequenceRepository.findExportDetailsByLocus(locus);

            Row headerRow = sheet.createRow(0);
            List<String> headerByColumns = CSVUtils.getExportDetailHeaderByColumns(columnList);
            CSVUtils.addExportDetailDataToRow(headerRow, headerByColumns);

            int rowCounter = 1;
            for (ExportDetailDto edd : exportDetails) {
                Row curRow = sheet.createRow(rowCounter);
                List<String> dataByColumns = CSVUtils.getExportDetailDataByColumns(columnList, edd);
                CSVUtils.addExportDetailDataToRow(curRow, dataByColumns);
                rowCounter++;
            }
        }
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            wb.write(bos);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                bos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return bos.toByteArray();
    }

}
