package th.ac.chula.fims.services;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import th.ac.chula.fims.exceptions.BadFormatFileException;
import th.ac.chula.fims.models.Enum.UploadProfileMode;
import th.ac.chula.fims.models.tables.Country;
import th.ac.chula.fims.models.tables.ProfileSearchAllele;
import th.ac.chula.fims.models.tables.ProfileSearchLocus;
import th.ac.chula.fims.payload.dto.CountryTableDto;
import th.ac.chula.fims.payload.dto.SearchProfileDto;
import th.ac.chula.fims.payload.response.MessageResponse;
import th.ac.chula.fims.repository.tables.CountryRepository;
import th.ac.chula.fims.repository.tables.ProfileSearchAlleleRepository;
import th.ac.chula.fims.repository.tables.ProfileSearchLocusRepository;
import th.ac.chula.fims.services.bean.BeanFactoryDynamicAutowireService;
import th.ac.chula.fims.services.interfaces.ProfileSearchService;
import th.ac.chula.fims.utils.CSVUtils;
import th.ac.chula.fims.utils.NumberUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class ProfileSearchServiceImpl implements ProfileSearchService {
    private final ProfileSearchLocusRepository profileSearchLocusRepository;
    private final ProfileSearchAlleleRepository profileSearchAlleleRepository;
    private final CountryRepository countryRepository;
    private final BeanFactoryDynamicAutowireService beanFactoryDynamicAutowireService;

    public ProfileSearchServiceImpl(ProfileSearchLocusRepository profileSearchLocusRepository, ProfileSearchAlleleRepository profileSearchAlleleRepository, CountryRepository countryRepository, BeanFactoryDynamicAutowireService beanFactoryDynamicAutowireService) {
        this.profileSearchLocusRepository = profileSearchLocusRepository;
        this.profileSearchAlleleRepository = profileSearchAlleleRepository;
        this.countryRepository = countryRepository;
        this.beanFactoryDynamicAutowireService = beanFactoryDynamicAutowireService;
    }

    @Override
    public MessageResponse uploadExcelSearchProfileAll(MultipartFile file) throws IOException {
        Workbook workbook = WorkbookFactory.create(file.getInputStream());
        int numberOfSheets = workbook.getNumberOfSheets();

        for (int i = 0; i < numberOfSheets; i++) {
            Sheet sheet = workbook.getSheetAt(i);

            Map<String, List<List<String>>> locusRows = new HashMap<>();
            List<List<String>> currentLocus = null;

            for (int j = 0; j <= sheet.getLastRowNum(); j++) {
                List<String> data = CSVUtils.getCellsReplaceEmptyCell(sheet.getRow(j), "0");
                currentLocus = getListsFromRows(locusRows, currentLocus, data);

            }

            performSaveToDatabase(locusRows);
        }


        return new MessageResponse("Success");
    }

    private List<List<String>> getListsFromRows(Map<String, List<List<String>>> locusRows, List<List<String>> currentLocus, List<String> data) {
        if (data.size() == 1) {
            String locus = data.get(0);
            currentLocus = locusRows.get(locus);
            if (currentLocus == null) {
                locusRows.put(locus, new ArrayList<>());
                currentLocus = locusRows.get(locus);
            }
        } else {
            if (currentLocus == null) {
                throw new BadFormatFileException();
            }
            if (data.size() != 0 && !data.get(0).equals("0")) {
                currentLocus.add(data);
            }
        }
        return currentLocus;
    }

    private void performSaveToDatabase(Map<String, List<List<String>>> rows) {
        Map<String, List<ProfileSearchLocus>> countryMap = new HashMap<>();

        for (Map.Entry<String, List<List<String>>> entry : rows.entrySet()) {
            String key = entry.getKey();
            List<List<String>> value = entry.getValue();

            if (value.size() == 0) {
                continue;
            }

            List<String> headers = value.get(0);

            for (int i = 1; i < headers.size(); i++) {
                String country = StringUtils.capitalize(value.get(0).get(i).toLowerCase());

                List<ProfileSearchAllele> searchAlleles = new ArrayList<>();

                for (int j = 1; j < value.size(); j++) {
                    String allele = value.get(j).get(0);
                    String aFValue = value.get(j).get(i);
                    if (aFValue.equals("0")) {
                        continue;
                    }
                    ProfileSearchAllele newProfileSearchAllele = new ProfileSearchAllele();
                    newProfileSearchAllele.setAllele(Float.valueOf(allele));
                    newProfileSearchAllele.setValue(new BigDecimal(aFValue));
                    searchAlleles.add(newProfileSearchAllele);
                }

                ProfileSearchLocus profileSearchLocus = new ProfileSearchLocus();
                profileSearchLocus.setLocus(key);
                profileSearchLocus.addProfileSearchAlleleAll(searchAlleles);

                List<ProfileSearchLocus> curCountryMap = countryMap.get(country);
                if (curCountryMap == null) {
                    ArrayList<ProfileSearchLocus> newProfileSearchLocus = new ArrayList<>();
                    newProfileSearchLocus.add(profileSearchLocus);
                    countryMap.put(country, newProfileSearchLocus);
                } else {
                    curCountryMap.add(profileSearchLocus);
                }
            }
        }

        List<Country> toSavedCountry = new ArrayList<>();
        List<ProfileSearchAllele> toSavedProfileSearchAllele = new ArrayList<>();
        List<ProfileSearchLocus> toSavedProfileSearchLocus = new ArrayList<>();
        List<ProfileSearchAllele> toDeletedProfileSearchAllele = new ArrayList<>();
        List<ProfileSearchLocus> toDeletedProfileSearchLocus = new ArrayList<>();
        for (Map.Entry<String, List<ProfileSearchLocus>> entry : countryMap.entrySet()) {
            String country = StringUtils.capitalize(entry.getKey().toLowerCase());
            List<ProfileSearchLocus> value = entry.getValue();
            Country countryInstance = countryRepository.findByCountry(country);
            if (countryInstance == null) {
                Country newCountry = new Country();
                newCountry.setCountry(country);
                newCountry.addProfileSearchLocusAll(value);
                toSavedCountry.add(newCountry);
            } else {
                countryInstance.getProfileSearchLoci().forEach(e -> {
                    toDeletedProfileSearchLocus.add(e);
                    toDeletedProfileSearchAllele.addAll(e.getProfileSearchAlleles());
                });
                countryInstance.getProfileSearchLoci().clear();
                countryInstance.addProfileSearchLocusAll(value);
                toSavedCountry.add(countryInstance);
            }
            value.forEach(e -> {
                toSavedProfileSearchLocus.add(e);
                toSavedProfileSearchAllele.addAll(e.getProfileSearchAlleles());
            });
        }
        profileSearchLocusRepository.deleteAll(toDeletedProfileSearchLocus);
        profileSearchAlleleRepository.deleteAll(toDeletedProfileSearchAllele);

        countryRepository.saveAll(toSavedCountry);
        profileSearchLocusRepository.saveAll(toSavedProfileSearchLocus);
        profileSearchAlleleRepository.saveAll(toSavedProfileSearchAllele);
    }

    @Override
    public MessageResponse uploadCSVSearchProfileAll(MultipartFile file) {
        byte[] bytes;
        try {
            bytes = file.getBytes();
        } catch (IOException e) {
            e.printStackTrace();
            throw new BadFormatFileException();
        }

        Map<String, List<List<String>>> locusRows = new HashMap<>();
        List<List<String>> currentLocus = null;

        String completeData = new String(bytes);
        String[] rows = completeData.split("\\r?\\n");
        for (String row : rows) {
            List<String> columns = Stream.of(row.split(",")).map(e -> e.equals("") ? "0" : e).collect(Collectors.toList());
            currentLocus = getListsFromRows(locusRows, currentLocus, columns);
        }

        performSaveToDatabase(locusRows);

        return new MessageResponse("success");
    }

    @Override
    public MessageResponse uploadExcelSearchProfileByCountry(MultipartFile file, String country, UploadProfileMode mode) throws IOException {
        Workbook workbook = WorkbookFactory.create(file.getInputStream());
        int numberOfSheets = workbook.getNumberOfSheets();

        for (int i = 0; i < numberOfSheets; i++) {
            Sheet sheet = workbook.getSheetAt(i);

            List<List<String>> data = new ArrayList<>();

            for (int j = 0; j <= sheet.getLastRowNum(); j++) {
                List<String> columns = CSVUtils.getCellsReplaceEmptyCell(sheet.getRow(j), "0");
                if (columns.get(0).equals("allele") || NumberUtils.isNumeric(columns.get(0))) {
                    data.add(columns);
                }
            }

            if (data.size() > 0) {
                List<List<String>> appliedList = appliedSizeOfColumnsToSizeWith(data, data.get(0).size(), "0");
                performSaveToDatabase(appliedList, country, mode);
            }
        }

        return new MessageResponse("Success");
    }

    @Override
    public MessageResponse uploadCSVSearchProfileByCountry(MultipartFile file, String country, UploadProfileMode mode) {
        byte[] bytes;
        try {
            bytes = file.getBytes();
        } catch (IOException e) {
            e.printStackTrace();
            throw new BadFormatFileException();
        }

        List<List<String>> data = new ArrayList<>();
        String completeData = new String(bytes);
        String[] rows = completeData.split("\\r?\\n");

        for (String row : rows) {
            List<String> columns = Stream.of(row.split(",")).map(e -> e.equals("") ? "0" : e).collect(Collectors.toList());
            if (columns.get(0).equals("allele") || NumberUtils.isNumeric(columns.get(0))) {
                data.add(columns);
            }
        }

        if (data.size() > 0) {
            List<List<String>> appliedList = appliedSizeOfColumnsToSizeWith(data, data.get(0).size(), "0");
            performSaveToDatabase(appliedList, country, mode);
        }

        return new MessageResponse("success");
    }

    @Override
    public List<CountryTableDto> searchProfileAll(String algorithm, List<SearchProfileDto> searchProfileDtoList) {
        return beanFactoryDynamicAutowireService.searchByProfile(algorithm, searchProfileDtoList);
    }

    private List<List<String>> appliedSizeOfColumnsToSizeWith(List<List<String>> data, int size, String word) {
        List<List<String>> result = new ArrayList<>();
        for (List<String> d : data) {
            if (d.size() < size) {
                List<String> newList = new ArrayList<>(d);
                for (int i = 0; i < size - d.size(); i++) {
                    newList.add(word);
                }
                result.add(newList);
            } else {
                result.add(d);
            }
        }
        return result;
    }

    private void performSaveToDatabase(List<List<String>> data, String country, UploadProfileMode mode) {
        List<ProfileSearchAllele> toSavedProfileSearchAllele = new ArrayList<>();
        List<ProfileSearchLocus> toSavedProfileSearchLocus = new ArrayList<>();
        List<ProfileSearchAllele> toDeletedProfileSearchAllele = new ArrayList<>();
        List<ProfileSearchLocus> toDeletedProfileSearchLocus = new ArrayList<>();
        Country countryInstance = countryRepository.findByCountry(country);
        Set<String> locusSet = new HashSet<>(data.get(0));

        if (countryInstance == null) {
            countryInstance = new Country();
            countryInstance.setProfileSearchLoci(new ArrayList<>());
        } else {
            if (mode == UploadProfileMode.REPLACE) {
                countryInstance.getProfileSearchLoci().forEach(e -> {
                    toDeletedProfileSearchLocus.add(e);
                    toDeletedProfileSearchAllele.addAll(e.getProfileSearchAlleles());
                });
                countryInstance.getProfileSearchLoci().clear();
            } else if (mode == UploadProfileMode.MERGE) {
                List<ProfileSearchLocus> restLocus = new ArrayList<>();
                countryInstance.getProfileSearchLoci().forEach(e -> {
                    if (locusSet.contains(e.getLocus())) {
                        toDeletedProfileSearchLocus.add(e);
                        toDeletedProfileSearchAllele.addAll(e.getProfileSearchAlleles());
                    } else {
                        restLocus.add(e);
                    }
                });
                countryInstance.setProfileSearchLoci(restLocus);
            } else {
                throw new EnumConstantNotPresentException(UploadProfileMode.class, "Cannot solve upload mode");
            }
        }

        if (data.size() == 0) {
            return;
        }

        for (int i = 1; i < data.get(0).size(); i++) {
            String locus = data.get(0).get(i);
            ProfileSearchLocus newProfileSearchLocus = new ProfileSearchLocus();
            newProfileSearchLocus.setLocus(locus);
            newProfileSearchLocus.setProfileSearchAlleles(new ArrayList<>());
            countryInstance.add(newProfileSearchLocus);
            toSavedProfileSearchLocus.add(newProfileSearchLocus);

            for (int j = 1; j < data.size(); j++) {
                String allele = data.get(j).get(0);
                String aFValue = data.get(j).get(i);
                if (aFValue.equals("0")) {
                    continue;
                }
                ProfileSearchAllele newProfileSearchAllele = new ProfileSearchAllele();
                newProfileSearchAllele.setAllele(Float.valueOf(allele));
                newProfileSearchAllele.setValue(new BigDecimal(aFValue));
                newProfileSearchLocus.add(newProfileSearchAllele);
                toSavedProfileSearchAllele.add(newProfileSearchAllele);
            }
        }

        profileSearchLocusRepository.deleteAll(toDeletedProfileSearchLocus);
        profileSearchAlleleRepository.deleteAll(toDeletedProfileSearchAllele);

        countryRepository.save(countryInstance);
        profileSearchLocusRepository.saveAll(toSavedProfileSearchLocus);
        profileSearchAlleleRepository.saveAll(toSavedProfileSearchAllele);
    }
}
