package th.ac.chula.fims.services;

import org.springframework.stereotype.Service;
import th.ac.chula.fims.models.tables.Kit;
import th.ac.chula.fims.models.tables.Locus;
import th.ac.chula.fims.models.tables.SummaryDataByCountry;
import th.ac.chula.fims.payload.response.summary.SummaryDashboardResponse;
import th.ac.chula.fims.payload.response.summary.SummaryDataResponse;
import th.ac.chula.fims.repository.tables.KitRepository;
import th.ac.chula.fims.repository.tables.LocusRepository;
import th.ac.chula.fims.repository.tables.SummaryDataRepository;
import th.ac.chula.fims.services.interfaces.SummaryDataService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SummaryDataServiceImpl implements SummaryDataService {
    private final SummaryDataRepository sumDataRepository;

    private final LocusRepository locusRepository;

    private final KitRepository kitRepository;

    public SummaryDataServiceImpl(SummaryDataRepository sumDataRepository, LocusRepository locusRepository, KitRepository kitRepository) {
        this.sumDataRepository = sumDataRepository;
        this.locusRepository = locusRepository;
        this.kitRepository = kitRepository;
    }

    @Override
    public SummaryDataResponse getSummaryDataByLocus(String locus) {
        int totalSum = 0;
        HashMap<String, Float> totalCountries = new HashMap<>();
        List<SummaryDataByCountry> sumData = sumDataRepository.findByLocusOrderByAllele(locus);
        List<Object[]> localData = sumDataRepository.findAmountPersonByLocusGroupByAllele(locus);
        HashMap<Float, HashMap<String, Float>> sumDataMap = new HashMap<>();

        for (SummaryDataByCountry sd : sumData) {
            HashMap<String, Float> curAlleleMap = sumDataMap.get(sd.getAllele());
            String curCountry = sd.getCountry();
            Float curFreq = sd.getFrequency();
            if (curAlleleMap != null) {
                curAlleleMap.put(curCountry, curFreq);
                curAlleleMap.put("total", curAlleleMap.get("total") + (float) curFreq);
            } else {
                HashMap<String, Float> countryMap = new HashMap<>();

                countryMap.put("allele", sd.getAllele());
                countryMap.put("total", curFreq);
                countryMap.put(curCountry, curFreq);
                sumDataMap.put(sd.getAllele(), countryMap);
            }

            totalCountries.merge(curCountry, curFreq, Float::sum);
        }

        float totalLocalSum = 0.0f;

        for (Object[] lD : localData) {
            totalLocalSum += Float.parseFloat(lD[0].toString());
        }

        for (Object[] lD : localData) {
            Float curFreq = Float.parseFloat(lD[0].toString()) / totalLocalSum;
            Float curAllele = Float.parseFloat(lD[1].toString());

            HashMap<String, Float> curAlleleMap = sumDataMap.get(curAllele);
            if (curAlleleMap != null) {
                curAlleleMap.put("Local Database", curFreq);
                curAlleleMap.put("total", curAlleleMap.get("total") + (float) curFreq);
            } else {
                HashMap<String, Float> countryMap = new HashMap<>();

                countryMap.put("allele", curAllele);
                countryMap.put("total", curFreq);
                countryMap.put("Local Database", curFreq);
                sumDataMap.put(curAllele, countryMap);
            }

            totalCountries.merge("Local Database", curFreq, Float::sum);
        }

        List<HashMap<String, Float>> countriesList = new ArrayList<>(sumDataMap.values());

        return new SummaryDataResponse(totalSum, countriesList, totalCountries);
    }

    @Override
    public HashMap<String, SummaryDashboardResponse> getDashboardSummaries() {
        HashMap<String, SummaryDashboardResponse> result = new HashMap<>();
        List<Kit> allKits = kitRepository.findAll();

        for (Kit kit : allKits) {
            List<String> curLoci = locusRepository.findByKit_id(kit.getId()).stream().map(Locus::getLocus)
                    .collect(Collectors.toList());
            Integer curNumOfSamples = sumDataRepository.findNumberOfSamplesByKidId(kit.getId());
            result.put(kit.getKit(), new SummaryDashboardResponse(curLoci, curNumOfSamples));
        }

        return result;
    }

}
