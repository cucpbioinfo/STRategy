package th.ac.chula.fims.services;

import org.springframework.stereotype.Service;
import th.ac.chula.fims.models.tables.configuration.Configuration;
import th.ac.chula.fims.payload.dto.CountryTableDto;
import th.ac.chula.fims.payload.dto.LocusTableDto;
import th.ac.chula.fims.payload.dto.SearchProfileDto;
import th.ac.chula.fims.payload.projection.AFValueCountry;
import th.ac.chula.fims.repository.configuration.ConfigurationRepository;
import th.ac.chula.fims.repository.tables.CountryRepository;
import th.ac.chula.fims.repository.tables.ProfileSearchLocusRepository;
import th.ac.chula.fims.services.interfaces.searchprofile.ProfileSearchAlgorithm;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Service("defaultProfileSearchAlgorithm")
public class ProfileSearchAlgorithmDefault implements ProfileSearchAlgorithm {

    private final ProfileSearchLocusRepository profileSearchLocusRepository;
    private final CountryRepository countryRepository;
    private final ConfigurationRepository configurationRepository;

    public ProfileSearchAlgorithmDefault(ProfileSearchLocusRepository profileSearchLocusRepository, CountryRepository countryRepository, ConfigurationRepository configurationRepository) {
        this.profileSearchLocusRepository = profileSearchLocusRepository;
        this.countryRepository = countryRepository;
        this.configurationRepository = configurationRepository;
    }

    @Override
    public List<CountryTableDto> performProfileSearch(List<SearchProfileDto> searchProfileDtoList) {
        List<CountryTableDto> countryTableDtoList = new ArrayList<>();
        Map<String, BigDecimal> countryFinalValue = new HashMap<>();

        countryRepository.findAll().forEach(e -> {
            countryTableDtoList.add(new CountryTableDto(e.getCountry(), new BigDecimal("0"), new BigDecimal("0"), new ArrayList<>()));
            countryFinalValue.put(e.getCountry(), new BigDecimal("1"));
        });

        for (SearchProfileDto searchProfileDto : searchProfileDtoList) {
            String locus = searchProfileDto.getLocus();
            float allele1 = searchProfileDto.getAllele1();
            float allele2 = searchProfileDto.getAllele2();
            boolean isHomo = allele1 == allele2;
            Set<AFValueCountry> afValues1 = profileSearchLocusRepository.findByLocusAndAllele(locus, allele1);
            Set<AFValueCountry> afValues2 = null;
            Optional<Configuration> profileSearchTheta = configurationRepository.findByConfigurationKey("profile_search_theta");
            BigDecimal pValue = new BigDecimal(0);
            BigDecimal qValue = new BigDecimal(0);
            BigDecimal fValue = new BigDecimal(0);
            BigDecimal theta = new BigDecimal("0.01");

            if (profileSearchTheta.isPresent()) {
                theta = new BigDecimal(profileSearchTheta.get().getConfigurationValue());
            }

            if (!isHomo) {
                afValues2 = profileSearchLocusRepository.findByLocusAndAllele(locus, allele2);
            }

            for (CountryTableDto countryTableDto : countryTableDtoList) {
                String country = countryTableDto.getCountry();
                LocusTableDto locusTableDto = new LocusTableDto();
                locusTableDto.setLocus(locus);
                Optional<AFValueCountry> firstValue = afValues1.stream().filter(e -> e.getCountry().equals(country)).findFirst();
                if (isHomo) {
                    if (firstValue.isPresent()) {
                        qValue = pValue = firstValue.get().getAFValue();
                    }

                    fValue = qValue.multiply(qValue).add(qValue.multiply(new BigDecimal("1").subtract(qValue)).multiply(theta));
                } else {
                    Optional<AFValueCountry> secondValue = afValues2.stream().filter(e -> e.getCountry().equals(country)).findFirst();

                    if (firstValue.isPresent()) {
                        pValue = firstValue.get().getAFValue();
                    }

                    if (secondValue.isPresent()) {
                        qValue = secondValue.get().getAFValue();
                    }

                    fValue = pValue.multiply(qValue).multiply(new BigDecimal(2));
                }
                BigDecimal inverseFinalValue = fValue.compareTo(BigDecimal.ZERO) == 0 ? new BigDecimal("1") : new BigDecimal("1").divide(fValue, RoundingMode.HALF_UP);

                locusTableDto.setQValue(qValue);
                locusTableDto.setPValue(pValue);
                locusTableDto.setFinalValue(fValue);
                locusTableDto.setAllele1(allele1);
                locusTableDto.setAllele2(allele2);
                locusTableDto.setInverseFinalValue(inverseFinalValue);
                countryFinalValue.put(country, countryFinalValue.get(country).multiply(inverseFinalValue));
                countryTableDto.getAlleleList().add(locusTableDto);
            }
        }

        BigDecimal sum = new BigDecimal("0");

        for (CountryTableDto countryTableDto : countryTableDtoList) {
            String country = countryTableDto.getCountry();
            BigDecimal summaryValue = countryFinalValue.get(country);
            sum = sum.add(summaryValue);
            countryTableDto.setSummaryValue(summaryValue);
        }

        for (CountryTableDto countryTableDto : countryTableDtoList) {
            countryTableDto.setFinalValue(countryTableDto.getSummaryValue().divide(sum, RoundingMode.HALF_UP));
        }

        return countryTableDtoList;
    }
}
