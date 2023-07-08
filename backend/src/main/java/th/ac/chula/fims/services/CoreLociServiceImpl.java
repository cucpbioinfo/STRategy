package th.ac.chula.fims.services;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import th.ac.chula.fims.models.tables.CoreLocus;
import th.ac.chula.fims.payload.dto.CoreLocusDto;
import th.ac.chula.fims.payload.response.MessageResponse;
import th.ac.chula.fims.repository.tables.CoreLociRepository;
import th.ac.chula.fims.services.interfaces.CoreLociService;
import th.ac.chula.fims.utils.CSVUtils;

import java.io.IOException;
import java.util.*;

@Service
public class CoreLociServiceImpl implements CoreLociService {
    private final CoreLociRepository coreLociRepository;
    private final String DEFAULT_COUNTRY = "default";

    public CoreLociServiceImpl(CoreLociRepository coreLociRepository) {
        this.coreLociRepository = coreLociRepository;
    }

    @Override
    public List<CoreLocusDto> getCoreLociByCountry(String country) {
        List<CoreLocus> byCountry = coreLociRepository.findByCountry(country);
        List<CoreLocus> byDefault = coreLociRepository.findByCountry(DEFAULT_COUNTRY);

        Set<String> locusSet = new HashSet<>();
        List<CoreLocusDto> result = new ArrayList<>();

        for (CoreLocus coreLocus : byCountry) {
            locusSet.add(coreLocus.getLocus());
        }

        for (CoreLocus coreLocus : byDefault) {
            CoreLocusDto coreLocusDto = new CoreLocusDto();
            coreLocusDto.setId(coreLocus.getId());
            coreLocusDto.setLocus(coreLocus.getLocus());
            coreLocusDto.setCountry(coreLocus.getCountry());
            coreLocusDto.setRequired(false);
            result.add(coreLocusDto);
        }

        for (CoreLocusDto coreLocusDto : result) {
            String locus = coreLocusDto.getLocus();
            if (locusSet.contains(locus)) {
                coreLocusDto.setRequired(true);
            }
        }

        return result;
    }

    @Override
    public MessageResponse uploadCoreLociByCountry(MultipartFile file) throws IOException {
        Workbook workbook = WorkbookFactory.create(file.getInputStream());
        int numberOfSheets = workbook.getNumberOfSheets();
        Map<String, List<CoreLocus>> coreLociMap = new HashMap<>();

        for (int i = 0; i < numberOfSheets; i++) {
            Sheet sheet = workbook.getSheetAt(i);

            // Skip header
            for (int j = 1; j <= sheet.getLastRowNum(); j++) {
                List<String> data = CSVUtils.getCells(sheet.getRow(j));
                String locus = data.get(0);
                String country = data.get(1);

                List<CoreLocus> curCoreLoci = coreLociMap.get(country);
                if (curCoreLoci == null) {
                    List<CoreLocus> newCoreLociList = new ArrayList<>();
                    CoreLocus newCoreLocus = new CoreLocus();
                    newCoreLocus.setLocus(locus);
                    newCoreLocus.setCountry(country);
                    newCoreLociList.add(newCoreLocus);
                    coreLociMap.put(country, newCoreLociList);
                } else {
                    CoreLocus newCoreLocus = new CoreLocus();
                    newCoreLocus.setLocus(locus);
                    newCoreLocus.setCountry(country);
                    curCoreLoci.add(newCoreLocus);
                }
            }
        }

        performSaveToDatabase(coreLociMap);
        return new MessageResponse("Success");
    }

    @Override
    public List<String> getAllCountryInCoreLoci() {
        return coreLociRepository.findAllCountries();
    }

    private void performSaveToDatabase(Map<String, List<CoreLocus>> coreLociMap) {
        List<CoreLocus> deleteList = new ArrayList<>();
        List<CoreLocus> addList = new ArrayList<>();

        for (Map.Entry<String, List<CoreLocus>> entry : coreLociMap.entrySet()) {
            String country = entry.getKey();
            List<CoreLocus> coreLocusList = entry.getValue();
            List<CoreLocus> byCountry = coreLociRepository.findByCountry(country);
            if (!byCountry.isEmpty()) {
                deleteList.addAll(byCountry);
            }
            addList.addAll(coreLocusList);
        }

        coreLociRepository.deleteAll(deleteList);
        coreLociRepository.saveAll(addList);
    }
}
