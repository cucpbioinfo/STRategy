package th.ac.chula.fims.services;

import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import th.ac.chula.fims.constants.ConfigurationConstant;
import th.ac.chula.fims.models.Enum.EventType;
import th.ac.chula.fims.models.User;
import th.ac.chula.fims.models.tables.*;
import th.ac.chula.fims.models.tables.configuration.Configuration;
import th.ac.chula.fims.models.tables.history.EventHistory;
import th.ac.chula.fims.payload.dto.EventHistoryDto;
import th.ac.chula.fims.payload.projection.ForenSequenceFull;
import th.ac.chula.fims.payload.projection.GenotypeCount;
import th.ac.chula.fims.payload.response.isnp.GenotypeAmount;
import th.ac.chula.fims.payload.response.isnp.ISNPDetails;
import th.ac.chula.fims.payload.response.isnp.ISNPResponseList;
import th.ac.chula.fims.payload.response.statistics.*;
import th.ac.chula.fims.payload.response.statistics.amount.AlleleAmount;
import th.ac.chula.fims.payload.response.statistics.amount.AlleleAmountContainer;
import th.ac.chula.fims.payload.response.statistics.amount.SequenceAmount;
import th.ac.chula.fims.repository.UserRepository;
import th.ac.chula.fims.repository.configuration.ConfigurationRepository;
import th.ac.chula.fims.repository.tables.*;
import th.ac.chula.fims.security.services.UserDetailsImpl;
import th.ac.chula.fims.services.interfaces.ForenseqSequenceService;
import th.ac.chula.fims.utils.AlleleUtils;
import th.ac.chula.fims.utils.PatternAlignmentAlgorithmUtils;
import th.ac.chula.fims.utils.SequenceUtils;
import th.ac.chula.fims.utils.StatisticalUtils;
import th.ac.chula.fims.utils.mapper.ChangedBaseMapper;
import th.ac.chula.fims.utils.mapper.EventHistoryMapper;

import javax.transaction.Transactional;
import java.time.Instant;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

@Service
public class ForenseqSequenceServiceImpl implements ForenseqSequenceService {

    private static final String PATTERN_ALIGNMENT = "patternAlignment";
    private final ConfigurationRepository configurationRepository;
    private final ForenseqSequenceRepository forenseqSequenceRepository;
    private final ChangedBaseRepository changedBaseRepository;
    private final PatternAlignmentMotifRepository patternAlignmentMotifRepository;
    private final ChangedBaseMapper changedBaseMapper;
    private final PatternAlignmentLocusRepository patternAlignLocusRepository;
    private final ChangingBaseRepository changingBaseRepository;
    private final EventHistoryRepository eventHistoryRepository;
    private final UserRepository userRepository;
    private final EventHistoryMapper eventHistoryMapper;
    private final ForenseqRepository forenseqRepository;

    public ForenseqSequenceServiceImpl(ConfigurationRepository configurationRepository,
                                       ForenseqSequenceRepository forenseqSequenceRepository,
                                       ChangedBaseRepository changedBaseRepository,
                                       PatternAlignmentMotifRepository patternAlignmentMotifRepository,
                                       ChangedBaseMapper changedBaseMapper,
                                       PatternAlignmentLocusRepository patternAlignLocusRepository,
                                       ChangingBaseRepository changingBaseRepository,
                                       EventHistoryRepository eventHistoryRepository, UserRepository userRepository,
                                       EventHistoryMapper eventHistoryMapper, ForenseqRepository forenseqRepository) {
        this.configurationRepository = configurationRepository;
        this.forenseqSequenceRepository = forenseqSequenceRepository;
        this.changedBaseRepository = changedBaseRepository;
        this.patternAlignmentMotifRepository = patternAlignmentMotifRepository;
        this.changedBaseMapper = changedBaseMapper;
        this.patternAlignLocusRepository = patternAlignLocusRepository;
        this.changingBaseRepository = changingBaseRepository;
        this.eventHistoryRepository = eventHistoryRepository;
        this.userRepository = userRepository;
        this.eventHistoryMapper = eventHistoryMapper;
        this.forenseqRepository = forenseqRepository;
    }

    private String resolveCountKey(Integer sampleId, String genotype) {
        return String.format("%d=%s", sampleId, genotype);
    }

    private List<AlleleAmount> resolveAlleleFrequencies(List<ForenSequenceFull> forenseqSeqFullList) {
        Map<Float, AlleleAmountContainer> containerMap = new HashMap<>();
        Map<String, Integer> countMap = new HashMap<>();
        for (ForenSequenceFull forenSequenceFull : forenseqSeqFullList) {
            String key = resolveCountKey(forenSequenceFull.getSampleId(), forenSequenceFull.getGenotype());
            countMap.merge(key, 1, Integer::sum);
        }
        int sum = 0;

        for (ForenSequenceFull forenSequenceFull : forenseqSeqFullList) {
            String genotype = forenSequenceFull.getGenotype();
            if (!AlleleUtils.isValidGenotype(genotype)) {
                continue;
            }
            Float allele = forenSequenceFull.getAllele();
            String sequence = forenSequenceFull.getSequence();
            String repeatMotif = forenSequenceFull.getRepeatMotif();
            Integer forenseqId = forenSequenceFull.getForenseqId();
            AlleleAmountContainer amountContainer = containerMap.get(allele);
            String key = resolveCountKey(forenSequenceFull.getSampleId(), forenSequenceFull.getGenotype());
            int amount = resolveAlleleAmount(genotype, countMap.get(key));
            sum += amount;
            List<ChangedBaseDto> changedBaseList =
                    changingBaseRepository.findAllByForenseqSeq(new ForenseqSequence(forenseqId)).stream().
                            map(changedBaseMapper::toChangedBaseDto).collect(Collectors.toList());
            String sequenceCBKey = resolveSequenceCBKey(repeatMotif, changedBaseList, sequence);

            if (amountContainer == null) {
                AlleleAmountContainer newContainer = new AlleleAmountContainer();

                newContainer.setAlleleAmount(new AlleleAmount(allele, amount));
                newContainer.getSequence().put(sequenceCBKey, new SequenceAmount(repeatMotif, sequence, amount,
                        changedBaseList));

                containerMap.put(allele, newContainer);
            } else {
                int prevAmount = amountContainer.getAlleleAmount().getAmount();
                amountContainer.getAlleleAmount().setAmount(prevAmount + amount);
                SequenceAmount sequenceAmount = amountContainer.getSequence().get(sequenceCBKey);
                if (sequenceAmount == null) {
                    amountContainer.getSequence().put(sequenceCBKey, new SequenceAmount(repeatMotif, sequence, amount
                            , changedBaseList));
                } else {
                    sequenceAmount.setAmount(sequenceAmount.getAmount() + amount);
                }
            }
        }

        List<AlleleAmount> result = new ArrayList<>();
        for (var container : containerMap.entrySet()) {
            AlleleAmountContainer alleleAmountContainer = container.getValue();
            AlleleAmount currentAlleleAmount = alleleAmountContainer.getAlleleAmount();
            float alleleFreq = currentAlleleAmount.getAmount() / (float) sum;
            currentAlleleAmount.setFrequency(alleleFreq);
            List<SequenceAmount> sequence = new ArrayList<>();
            for (Entry<String, SequenceAmount> sequenceAmountEntry :
                    alleleAmountContainer.getSequence().entrySet()) {
                SequenceAmount sequenceAmount = sequenceAmountEntry.getValue();
                float seqAlleleFreq = sequenceAmount.getAmount() / (float) sum;
                sequenceAmount.setFrequency(seqAlleleFreq);
                sequence.add(sequenceAmount);
            }
            currentAlleleAmount.setSequence(sequence);
            result.add(currentAlleleAmount);
        }

        return result;
    }

    private String resolveSequenceCBKey(String repeatMotif, List<ChangedBaseDto> changedBaseList, String sequence) {
        if (repeatMotif == null) return sequence;
        StringBuilder key = new StringBuilder(repeatMotif);
        for (ChangedBaseDto changedBaseDto : changedBaseList) {
            key.append(String.format("|%c,%c,%d", changedBaseDto.getFrom(), changedBaseDto.getTo(),
                    changedBaseDto.getPosition()));
        }
        return key.toString();
    }

    private int resolveAlleleAmount(String genotype, Integer count) {
        String[] alleles = genotype.split(",");
        if (alleles.length == 1) {
            return 1;
        }
        if (alleles[0].equals(alleles[1]) && count == 1) {
            return 2;
        }
        return 1;
    }


    @Override
    @Transactional
    public LocusInfoStatistic getGraphInfoByLocus(String locus) {
        List<ForenSequenceFull> forenseqSeqFullList =
                forenseqSequenceRepository.findAllForenseqAndForenseqSequenceByLocus(locus);
        List<AlleleAmount> diploidResult = resolveAlleleFrequencies(forenseqSeqFullList.stream()
                .filter(e -> AlleleUtils.isDiploid(e.getGenotype())).collect(Collectors.toList()));
        List<AlleleAmount> haploidResult = resolveAlleleFrequencies(forenseqSeqFullList.stream()
                .filter(e -> AlleleUtils.isHaploid(e.getGenotype())).collect(Collectors.toList()));
        List<Forenseq> forenseqs = forenseqRepository.findAllByLocus(locus);
        HomoHeteroValue homoHeteroValue = StatisticalUtils.calculateHomoHeteroValues(forenseqs);
        int numberOfDiploid = (int) forenseqs.stream().filter(e -> AlleleUtils.isDiploid(e.getGenotype())).count();
        int numberOfHaploid = (int) forenseqs.stream().filter(e -> AlleleUtils.isHaploid(e.getGenotype())).count();
        int numberOfHetero = homoHeteroValue.getHeterozygous();
        List<GenotypeCount> genotypeCounts = forenseqRepository.findByLocusAndGroupByGenotype(locus);

        double heterogosity = (double) numberOfHetero / (double) numberOfDiploid;
        if (numberOfDiploid == 0) {
            heterogosity = 0;
        }

        LocusInfoStatisticDiploid locusInfoStatisticDiploid = null;
        LocusInfoStatisticHaploid locusInfoStatisticHaploid = null;

        if (diploidResult.size() > 0) {
            double expectedHeterogosity = calculateExpectedHeterogosity(diploidResult);
            double matchProbability = calculateMatchProbabilityDiploid(genotypeCounts);
            double powerOfDiscrimination = 1 - matchProbability;
            double polymorphicInformationContent = calculatePolymorphicInformationContent(diploidResult);
            double powerOfExclusion = Math.pow(heterogosity, 2) *
                    (1 - 2 * heterogosity * Math.pow(1.0 - heterogosity, 2));

            locusInfoStatisticDiploid = new LocusInfoStatisticDiploid(diploidResult, numberOfDiploid, heterogosity,
                    expectedHeterogosity, powerOfDiscrimination, polymorphicInformationContent, matchProbability,
                    powerOfExclusion);
        }

        if (haploidResult.size() > 0) {
            double matchProbability = calculateMatchProbabilityHaploid(genotypeCounts);
            double powerOfDiscrimination = 1 - matchProbability;
            double polymorphicInformationContent = calculatePolymorphicInformationContent(haploidResult);

            locusInfoStatisticHaploid = new LocusInfoStatisticHaploid(haploidResult, numberOfHaploid,
                    powerOfDiscrimination, polymorphicInformationContent, matchProbability);
        }

        return new LocusInfoStatistic(locusInfoStatisticDiploid, locusInfoStatisticHaploid);
    }

    private double calculateMatchProbabilityDiploid(List<GenotypeCount> genotypeCountList) {
        double sum = 0;
        int n = 0;
        List<GenotypeCount> genotypeCounts =
                genotypeCountList.stream().filter(genotypeCount -> genotypeCount.getGenotype().contains(",")).collect(Collectors.toList());

        for (GenotypeCount genotypeCount : genotypeCounts) {
            n += genotypeCount.getCount();
        }

        for (GenotypeCount genotypeCount : genotypeCounts) {
            sum += Math.pow(genotypeCount.getCount() / (float) n, 2);
        }
        return sum;
    }

    private double calculateMatchProbabilityHaploid(List<GenotypeCount> genotypeCountList) {
        double sum = 0;
        int n = 0;
        List<GenotypeCount> genotypeCounts =
                genotypeCountList.stream().filter(genotypeCount -> AlleleUtils.isHaploid(genotypeCount.getGenotype())).collect(Collectors.toList());

        for (GenotypeCount genotypeCount : genotypeCounts) {
            n += genotypeCount.getCount();
        }

        for (GenotypeCount genotypeCount : genotypeCounts) {
            sum += Math.pow(genotypeCount.getCount() / (float) n, 2);
        }
        return sum;
    }

    private double calculatePolymorphicInformationContent(List<AlleleAmount> alleleAmountList) {
        double expectedHeterogosity = calculateExpectedHeterogosity(alleleAmountList);
        double n = getNumberOfActualAllele(alleleAmountList);
        double summation = 0;
        for (int i = 0; i < alleleAmountList.size() - 1; i++) {
            for (int j = i + 1; j < alleleAmountList.size(); j++) {
                double currentP = Math.pow(alleleAmountList.get(i).getAmount() / (float) n, 2);
                double nextP = Math.pow(alleleAmountList.get(j).getAmount() / (float) n, 2);
                summation += 2 * currentP * nextP;
            }
        }
        return expectedHeterogosity - summation;
    }

    private double calculateExpectedHeterogosity(List<AlleleAmount> alleleAmountList) {
        int n = getNumberOfActualAllele(alleleAmountList);

        double summation = 0;

        for (AlleleAmount alleleAmount : alleleAmountList) {
            summation += Math.pow(alleleAmount.getAmount() / (float) n, 2);
        }

        return 1 - summation;
    }

    private int getNumberOfActualAllele(List<AlleleAmount> alleleAmountList) {
        int n = 0;
        for (AlleleAmount alleleAmount : alleleAmountList) {
            n += alleleAmount.getAmount();
        }
        return n;
    }

    private int getNumberOfTotal(Map<Float, Integer> alleleMap, int numberOfTotal, List<Object[]> otherDB) {
        for (Object[] alleleAmount : otherDB) {
            float curAllele = Float.parseFloat(String.valueOf(alleleAmount[0]));
            int curAmount = Integer.parseInt(String.valueOf(alleleAmount[1]));
            numberOfTotal += curAmount;
            alleleMap.merge(curAllele, curAmount, Integer::sum);
        }
        return numberOfTotal;
    }

    @Override
    public List<ProvinceAlleleListResponse> getMapInfoByLocus(String locus) {
        List<Object[]> statMap = forenseqSequenceRepository.findStatsMapByLocus(locus);
        Map<Integer, ProvinceAlleleListResponse> provinceMap = new HashMap<>();

        for (Object[] row : statMap) {
            Integer provinceId = Integer.parseInt(row[0].toString());
            ProvinceAlleleListResponse targetProvinceMap = provinceMap.get(provinceId);
            if (targetProvinceMap != null) {
                targetProvinceMap.getAlleleAmountList().add(
                        new AlleleAmount(Float.parseFloat(row[5].toString()), Integer.parseInt(row[6].toString())));
            } else {
                Region region = new Region();
                region.setRegion(row[7].toString());
                Province province = new Province(provinceId, row[1].toString(), row[2].toString(),
                        Double.parseDouble(row[3].toString()), Double.parseDouble(row[4].toString()), region, null);
                List<AlleleAmount> alleleAmountList = new ArrayList<>();
                alleleAmountList.add(
                        new AlleleAmount(Float.parseFloat(row[5].toString()), Integer.parseInt(row[6].toString())));
                provinceMap.put(provinceId, new ProvinceAlleleListResponse(province, alleleAmountList));
            }
        }

        return new ArrayList<>(provinceMap.values());
    }

    @Override
    public List<ForenseqSequence> calculatePatternAlignmentAndSaveToDatabase(String locus, String allele) {
        List<ForenseqSequence> forenseqSequences =
                forenseqSequenceRepository.findForenseqSequencesByLocusAndAllele(locus, allele);
        Optional<PatternAlignmentLocus> patternAlignmentLocus = patternAlignLocusRepository.findByLocus(locus);

        if (patternAlignmentLocus.isEmpty()) {
            return new ArrayList<>();
        }

        List<PatternAlignmentMotif> motifs = patternAlignmentMotifRepository.findByLocusAndAllele(locus,
                Float.parseFloat(allele));
        if (motifs.isEmpty()) {
            motifs = patternAlignmentMotifRepository.findMotifByLocusAndAllele(locus, 0.0f);
        }

        List<ChangedBase> changedBaseListToAdd = new ArrayList<>();
        List<ChangedBase> changedBaseListToDelete = new ArrayList<>();
        for (ForenseqSequence forenseqSequence : forenseqSequences) {
            String originalSequence = forenseqSequence.getSequence();

            if (patternAlignmentLocus.get().getIsReverse()) {
                String forwardSequence = SequenceUtils.reverseSequence(originalSequence);
                forenseqSequence.setSequence(forwardSequence);
            }

            try {
                Map<String, Object> alignmentObj = PatternAlignmentAlgorithmUtils.getPatternAlignment(motifs,
                        changedBaseListToAdd, changedBaseListToDelete, forenseqSequence, allele);

                forenseqSequence.setSequence(originalSequence);

                String repeatMotif = alignmentObj.get(PATTERN_ALIGNMENT).toString();
                String forwardRepeatMotif = alignmentObj.get(PATTERN_ALIGNMENT).toString();

                if (patternAlignmentLocus.get().getIsReverse()) {
                    repeatMotif = SequenceUtils.reverseRepeatMotifs(repeatMotif);
                }

                forenseqSequence.setRepeatMotif(repeatMotif);
                forenseqSequence.setForwardRepeatMotif(forwardRepeatMotif);
            } catch (Exception ex) {
                forenseqSequence.setSequence(originalSequence);
                System.out.println("Exception: " + ex);
            }
        }
        changedBaseRepository.deleteAll(changedBaseListToDelete);
        changedBaseRepository.saveAll(changedBaseListToAdd);
        forenseqSequenceRepository.saveAll(forenseqSequences);
        return forenseqSequences;
    }

    /*
    @Override
    public List<ForenseqSequence> calculatePatternAlignmentAndSaveToDatabase(String locus, String allele) {
        List<ForenseqSequence> forenseqSequences =
                forenseqSequenceRepository.findForenseqSequencesByLocusAndAllele(locus, allele);
        Optional<PatternAlignmentLocus> patternAlignmentLocus = patternAlignLocusRepository.findByLocus(locus);

        if (patternAlignmentLocus.isEmpty()) {
            return new ArrayList<>();
        }

        List<PatternAlignmentMotif> motifs = patternAlignmentMotifRepository.findByLocusAndAllele(locus,
                Float.parseFloat(allele));
        if (motifs.isEmpty()) {
            motifs = patternAlignmentMotifRepository.findMotifByLocusAndAllele(locus, 0.0f);
        }

        if (patternAlignmentLocus.get().getIsReverse()) {
            motifs = SequenceUtils.reverseMotif(motifs);
        }

        List<ChangedBase> changedBaseListToAdd = new ArrayList<>();
        List<ChangedBase> changedBaseListToDelete = new ArrayList<>();
        for (ForenseqSequence forenseqSequence : forenseqSequences) {
            try {
                Map<String, Object> alignmentObj = PatternAlignmentAlgorithmUtils.getPatternAlignment(motifs,
                        changedBaseListToAdd, changedBaseListToDelete, forenseqSequence, allele);
                forenseqSequence.setRepeatMotif(alignmentObj.get(PATTERN_ALIGNMENT).toString());
            } catch (Exception ex) {
                System.out.println("Exception: " + ex);
            }
        }
        changedBaseRepository.deleteAll(changedBaseListToDelete);
        changedBaseRepository.saveAll(changedBaseListToAdd);
        forenseqSequenceRepository.saveAll(forenseqSequences);
        return forenseqSequences;
    }
     */

    @Override
    @Async
    public void calculateAllLocusAndAlleleOfPatternAlignmentAndSaveToDatabase() {
        List<PatternAlignmentLocus> salList = patternAlignLocusRepository.findAll();
        int alleleCount = 0;
        int locusCount = 0;

        for (PatternAlignmentLocus sal : salList) {
            String locus = sal.getLocus().replaceAll(" ", "");
            System.out.println("Processing All alleles of Locus: " + locus);
            List<Float> alleleList = patternAlignLocusRepository.findAlleleByLocus(locus);
            locusCount++;
            for (Float allele : alleleList) {
                System.out.printf("Locus: %s, Allele: %s%n", locus, allele);
                calculatePatternAlignmentAndSaveToDatabase(locus, allele.toString());
                alleleCount++;
            }
        }
        String logMessage = String.format("Total number of Loci: %d, Total number of Alleles: %d", locusCount,
                alleleCount);

        // Get current user
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        Long currentUserId = userDetails.getId();
        Optional<User> currentUser = userRepository.findById(currentUserId);

        EventHistory eventHistory = new EventHistory();
        eventHistory.setTime(Instant.now());
        currentUser.ifPresent(eventHistory::setActor);
        eventHistory.setEventType(EventType.PROCESS_DATA);
        eventHistory.setFeature("ALL_SEQUENCE_ALIGNMENT");
        eventHistory.setNote(logMessage);

        eventHistoryRepository.save(eventHistory);

        Optional<Configuration> thereNewReferencedPattern =
                configurationRepository.findByConfigurationKey(ConfigurationConstant.IS_THERE_NEW_REFERENCED_PATTERN);
        Optional<Configuration> numberOfNewSampleSinceUpdate =
                configurationRepository.findByConfigurationKey(ConfigurationConstant.NUMBER_OF_NEW_SAMPLES_SINCE_UPDATE_PATTERN_ALIGNMENT);
        if (thereNewReferencedPattern.isPresent()) {
            Configuration configuration = thereNewReferencedPattern.get();
            configuration.setConfigurationValue("0");
            configurationRepository.save(configuration);
        }
        if (numberOfNewSampleSinceUpdate.isPresent()) {
            Configuration configuration = numberOfNewSampleSinceUpdate.get();
            configuration.setConfigurationValue("0");
            configurationRepository.save(configuration);
        }

        System.out.println(logMessage);
    }

    @Override
    public EventHistoryDto getLastPatternAlignmentGeneration() {
        EventHistory eventHistory =
                eventHistoryRepository.findTopByEventTypeAndFeatureOrderByTimeDesc(EventType.PROCESS_DATA,
                        "ALL_SEQUENCE_ALIGNMENT");
        return eventHistoryMapper.toEventHistoryDto(eventHistory);
    }

    @Override
    public List<PatternAlignmentResponse> getPatternAlignmentByLocusAndAllele(String locus, String allele,
                                                                              Boolean isFetch, Pageable pageable) {
        List<ForenseqSequence> forenseqSequences;
        if (isFetch) {
            forenseqSequences = calculatePatternAlignmentAndSaveToDatabase(locus, allele);
        } else {
            forenseqSequences = forenseqSequenceRepository.findForenseqSequencesByLocusAndAllele(locus, allele);
        }

        return getPatternAlignmentReponseList(forenseqSequences);
    }

    private List<PatternAlignmentResponse> getPatternAlignmentReponseList(List<ForenseqSequence> forenseqSequences) {
        return forenseqSequences.stream().map(fs -> new PatternAlignmentResponse(
                fs.getSample().getSampleId(), fs.getSample().getSampleYear(),
                fs.getSequence(), fs.getReadCount(), fs.getRepeatMotif(),
                fs.getForwardRepeatMotif(),
                fs.getChangedBaseList().stream().map(changedBaseMapper::toChangedBaseDto)
                        .collect(Collectors.toList()))).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ISNPResponseList getStatsISNP(Integer size, Integer page, String searchWord) {
        Integer offset = size * page;
        String queryLike = String.format("%%%s%%", searchWord);

        HashMap<String, List<GenotypeAmount>> locusMap = new HashMap<>();
        HashMap<String, Integer> locusTotalMap = new HashMap<>();

        List<Object[]> result = forenseqSequenceRepository.findStatByISNPGroupByLocusAndAllele(size, offset,
                queryLike);
        Optional<Integer> total = forenseqSequenceRepository.countStatByISNPGroupByLocusAndAllele(queryLike);

        for (Object[] objects : result) {
            String locus = objects[0].toString();
            String genotype = objects[1].toString();
            int amount = Integer.parseInt(objects[2].toString());

            Integer locusTotal = locusTotalMap.get(locus);
            if (locusTotal != null) {
                locusMap.get(locus).add(new GenotypeAmount(genotype, amount));
                locusTotalMap.put(locus, locusTotal + amount);
            } else {
                List<GenotypeAmount> genotypeList = new ArrayList<>();

                genotypeList.add(new GenotypeAmount(genotype, amount));
                locusMap.put(locus, genotypeList);
                locusTotalMap.put(locus, amount);
            }
        }

        ISNPResponseList responseList = new ISNPResponseList();
        List<ISNPDetails> iSNPDetailList = new ArrayList<>();

        for (Map.Entry<String, List<GenotypeAmount>> entry : locusMap.entrySet()) {
            String locus = entry.getKey();
            List<GenotypeAmount> detail = entry.getValue();
            Integer iSNPTotal = locusTotalMap.get(locus);

            ISNPDetails iSNPDetail = new ISNPDetails(locus, detail, iSNPTotal);
            iSNPDetailList.add(iSNPDetail);
        }

        responseList.setIsnpList(iSNPDetailList);
        responseList.setTotalEntities(total.orElse(0));

        return responseList;
    }
}
