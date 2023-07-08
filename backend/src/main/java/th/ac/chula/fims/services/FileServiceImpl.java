package th.ac.chula.fims.services;

import org.apache.commons.collections4.map.HashedMap;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import th.ac.chula.fims.constants.ConfigurationConstant;
import th.ac.chula.fims.constants.EntityConstant;
import th.ac.chula.fims.exceptions.ResourceNotFoundException;
import th.ac.chula.fims.models.Enum.EGender;
import th.ac.chula.fims.models.tables.*;
import th.ac.chula.fims.models.tables.configuration.Configuration;
import th.ac.chula.fims.payload.dto.ExcelPersonDataUploadResponse;
import th.ac.chula.fims.payload.response.person.SequenceDetail;
import th.ac.chula.fims.repository.configuration.ConfigurationRepository;
import th.ac.chula.fims.repository.tables.*;
import th.ac.chula.fims.services.interfaces.FileService;
import th.ac.chula.fims.utils.AlleleUtils;
import th.ac.chula.fims.utils.CSVUtils;

import javax.transaction.Transactional;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.Map.Entry;

@Service
public class FileServiceImpl implements FileService {

    private final PersonRepository personRepository;
    private final CountryRepository countryRepository;
    private final RegionRepository regionRepository;
    private final ProvinceRepository provinceRepository;
    private final RaceRepository raceRepository;
    private final SampleRepository sampleRepository;
    private final LocusRepository locusRepository;
    private final CEDataRepository cEDataRepository;
    private final ConfigurationRepository configurationRepository;

    public FileServiceImpl(PersonRepository personRepository, CountryRepository countryRepository,
                           RegionRepository regionRepository, ProvinceRepository provinceRepository,
                           RaceRepository raceRepository, SampleRepository sampleRepository,
                           LocusRepository locusRepository, CEDataRepository cEDataRepository,
                           ForenseqRepository forenseqRepository, ConfigurationRepository configurationRepository) {
        this.personRepository = personRepository;
        this.countryRepository = countryRepository;
        this.regionRepository = regionRepository;
        this.provinceRepository = provinceRepository;
        this.raceRepository = raceRepository;
        this.sampleRepository = sampleRepository;
        this.locusRepository = locusRepository;
        this.cEDataRepository = cEDataRepository;
        this.configurationRepository = configurationRepository;
    }

    private Sample createSampleWithPerson(Person person) {
        Sample sample = new Sample();
        if (person == null) {
            Person personInfo = new Person();
            personInfo.add(sample);
        } else {
            person.add(sample);
        }
        return sample;

    }

    @Override
    @Transactional
    public Boolean readExcelForenseqData(MultipartFile file, String sampleId) throws IOException {
        boolean isDirty = false;
        Workbook workbook = WorkbookFactory.create(file.getInputStream());
        int numberOfSheets = workbook.getNumberOfSheets();
        Sample sample;
        Person person = null;

        if (sampleId != null) {
            Optional<Sample> topSample = sampleRepository.findTopBySampleId(sampleId);
            if (topSample.isPresent()) {
                person = topSample.get().getPerson();
                person.setSamples(null);
                sampleRepository.delete(topSample.get());
            }
        }

        sample = createSampleWithPerson(person);
        sample.setForenseqList(null);

        for (int i = 0; i < numberOfSheets; i++) {
            Sheet sheet = workbook.getSheetAt(i);
            String sheetName = sheet.getSheetName();

            Map<String, SequenceDetail> locusAllele = new LinkedHashMap<>();

            switch (sheetName) {
                case "Autosomal STRs":
                    extractInfo(sample.getPerson(), sample, sheet);
                    extractForenseqData(sample, sheet, locusAllele, 13, 40, "Autosome");
                    break;
                case "Y STRs":
                    extractForenseqData(sample, sheet, locusAllele, 13, 36, "Y");
                    break;
                case "X STRs":
                    extractForenseqData(sample, sheet, locusAllele, 13, 19, "X");
                    break;
                case "iSNPs":
                    extractForenseqData(sample, sheet, locusAllele, 12, 105, "iSNP");
                    break;
            }
            sampleRepository.save(sample);

            isDirty = true;
        }

        if (isDirty) {
            Optional<Configuration> numberOfNewSampleSinceUpdate =
                    configurationRepository.findByConfigurationKey(ConfigurationConstant.NUMBER_OF_NEW_SAMPLES_SINCE_UPDATE_PATTERN_ALIGNMENT);
            Optional<Configuration> notificationEnabled =
                    configurationRepository.findByConfigurationKey(ConfigurationConstant.REQUIRED_UPDATE_PATTERN_NOTIFICATION_ENABLED);
            if (numberOfNewSampleSinceUpdate.isPresent()) {
                Configuration configuration = numberOfNewSampleSinceUpdate.get();
                int numberOfNewSamples = Integer.parseInt(configuration.getConfigurationValue());
                configuration.setConfigurationValue(String.valueOf(numberOfNewSamples + 1));
                configurationRepository.save(configuration);
            }
            if (notificationEnabled.isPresent()) {
                Configuration configuration = notificationEnabled.get();
                configuration.setConfigurationValue("1");
                configurationRepository.save(configuration);
            }
        }

        return isDirty;
    }

    @Override
    public Workbook openCompatibleWorkbook(String fileName, InputStream fis) throws IOException {
        return WorkbookFactory.create(fis);
    }

    @Override
    public void extractForenseqData(Sample sample, Sheet sheet,
                                    Map<String, SequenceDetail> locusAllele, int startSummaryLine, int endSummaryLine
            , String chromosomeTag) {

        int line = 1;
        for (Row row : sheet) {
            List<String> data = CSVUtils.getCells(row);
            if (line >= startSummaryLine && line <= endSummaryLine) {
                String genotype = AlleleUtils.resolveGenotypeFromCsv(data.get(1));
                if (data.size() > 2) {
                    locusAllele.put(data.get(0), new SequenceDetail(genotype, data.get(2)));
                } else {
                    locusAllele.put(data.get(0), new SequenceDetail(genotype, ""));
                }
            } else if (line >= endSummaryLine + 3) {
                if (data.get(0).equals("n/a")) {
                    break;
                }

                if (data.get(2).equals("Yes")) {
                    ForenseqSequence tempFS = new ForenseqSequence(data.get(1), (int) Double.parseDouble(data.get(3)),
                            data.size() < 5 ? "n/a" : data.get(4), sample);
                    locusAllele.get(data.get(0)).getFsList().add(tempFS);
                }
            }

            if (!data.get(0).equals("n/a") && !data.get(0).equals("")) {
                line++;
            }
        }

        for (Entry<String, SequenceDetail> entry : locusAllele.entrySet()) {
            Forenseq forenseq = new Forenseq(entry.getKey(),
                    entry.getValue().getGenotype(),
                    entry.getValue().getQcIndicator(),
                    chromosomeTag);
            sample.add(forenseq);
            for (ForenseqSequence curFS : entry.getValue().getFsList()) {
                forenseq.add(curFS);
            }
        }
    }

    @Override
    public void extractInfo(Person personInfo, Sample sample, Sheet sheet) {
        int line = 1;
        for (Row row : sheet) {
            List<String> data = CSVUtils.getCells(row);

            if (line >= 1 && line <= 7) {
                switch (data.get(0)) {
                    case "Created":
                        int sampleYear = Integer.parseInt(data.get(1).split(" ")[2]);
                        sample.setSampleYear(sampleYear);
                        break;
                    case "Sample":
                        String sampleId = data.get(1);
                        sample.setSampleId(sampleId);
                        break;
                    case "Gender":
                        EGender gender = data.get(1).equals("XY") ? EGender.MALE : EGender.FEMALE;
                        personInfo.setGender(gender);
                        break;
                }
            } else {
                break;
            }

            if (!data.get(0).equals("n/a")) {
                line++;
            }
        }
    }

    @Override
    public ExcelPersonDataUploadResponse readExcelPersonData(MultipartFile file) throws IOException {
        boolean isDirty = false;
        List<List<String>> notSuccessPerson = new ArrayList<>();
        Workbook workbook = WorkbookFactory.create(file.getInputStream());
        Sheet sheet = workbook.getSheetAt(0);
        int line = 1;
        for (Row row : sheet) {
            if (line == 1) {
                line++;
                continue;
            }
            List<String> data = CSVUtils.getCells(row);
            String sampleId = data.get(5);
            Optional<Sample> sample = sampleRepository.findTopBySampleId(sampleId);
            if (sample.isEmpty()) {
                throw new ResourceNotFoundException(EntityConstant.SAMPLE, sampleId);
            }
            savePerson(sample.get().getPerson(), data, notSuccessPerson);
            isDirty = true;
            line++;
        }

        return new ExcelPersonDataUploadResponse(isDirty, notSuccessPerson);
    }

    @Override
    public void savePerson(Person person, List<String> data, List<List<String>> notSuccessPerson) {
        if (!data.get(0).equals("-")) {
            person.setGender(EGender.valueOf(data.get(0)));
        }

        if (!data.get(1).equals("-") && !data.get(2).equals("-") && !data.get(3).equals("-")) {
            Country country = countryRepository.findByCountry(data.get(3));
            boolean isError = false;

            if (country == null) {
                isError = true;
                List<String> error = new ArrayList<>(data);
                error.add(String.format("Country: %s not found", data.get(3)));
                notSuccessPerson.add(error);
            }

            Region region = regionRepository.findByRegionAndCountry(data.get(2), country);
            if (region == null) {
                isError = true;
                List<String> error = new ArrayList<>(data);
                error.add(String.format("Region: %s not found", data.get(2)));
                notSuccessPerson.add(error);
            }

            Province province = provinceRepository.findByProvinceAndRegion(data.get(1), region);
            if (province == null) {
                isError = true;
                List<String> error = new ArrayList<>(data);
                error.add(String.format("Province: %s not found", data.get(1)));
                notSuccessPerson.add(error);
            }

            if (!isError) {
                person.setCountry(country);
                person.setRegion(region);
                person.setProvince(province);
            }
        }

        if (!data.get(4).equals("-")) {
            Race race = raceRepository.findByRace(data.get(4));
            boolean isError = false;

            if (race == null) {
                isError = true;
                List<String> error = new ArrayList<>(data);
                error.add(String.format("Race: %s not found", data.get(4)));
                notSuccessPerson.add(error);
            }

            if (!isError) {
                person.setRace(race);
            }
        }

        personRepository.save(person);
    }

    @Override
    @Transactional
    public Boolean readTextFileCEData(MultipartFile file) throws IOException {
        boolean isDirty = false;
        String contents = new String(file.getBytes());
        Map<String, Sample> sampleMap = new HashedMap<>();
        String[] rows = contents.split("\n|\r\n");
        for (int i = 1; i < contents.split("\n|\r\n").length; i++) {
            String[] columns = rows[i].split("\t");
            String sampleId = columns[0];
            String locus = columns[1];
            if (sampleMap.get(sampleId) == null) {
                extractSample(sampleMap, columns);
            }
            CEData ceData = extractCEData(columns, locus);
            sampleMap.get(sampleId).add(ceData);
            cEDataRepository.save(ceData);
            isDirty = true;
        }
        return isDirty;
    }

    @Override
    @Transactional
    public CEData extractCEData(String[] columns, String locus) {
        StringBuilder genotype = new StringBuilder(columns[2]);
        String chromosomeType = null;
        Locus targetLocus = locusRepository.findTopByLocus(locus);

        for (int i = 3; i < columns.length; i++) {
            genotype.append(",").append(columns[i]);
        }

        if (targetLocus != null) {
            chromosomeType = targetLocus.getKit().getChromosomeType();
        }

        return new CEData(locus, genotype.toString(), null, chromosomeType, null);
    }

    @Override
    public void extractSample(Map<String, Sample> sampleMap, String[] columns) {
        String sampleId = columns[0];
        Optional<Sample> sample = sampleRepository.findTopBySampleId(sampleId);

        if (sample.isPresent()) {
            sampleMap.put(sampleId, sample.get());
        } else {
            Person person = new Person();
            EGender gender = sampleId.split("-").length > 1 ? deriveGender(sampleId) : null;
            if (gender != null) {
                person.setGender(gender);
            }
            Sample newSample = new Sample(0, sampleId, person);
            person.add(newSample);
            sampleMap.put(sampleId, newSample);
            personRepository.save(person);
        }
    }

    @Override
    public EGender deriveGender(String sampleId) {
        String derivedGender = sampleId.split("-")[1];
        if (derivedGender.equals("F")) {
            return EGender.MALE;
        } else if (derivedGender.equals("M")) {
            return EGender.FEMALE;
        } else {
            return null;
        }
    }
}
