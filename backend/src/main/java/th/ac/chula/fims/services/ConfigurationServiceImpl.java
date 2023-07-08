package th.ac.chula.fims.services;

import org.apache.commons.collections4.map.HashedMap;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import th.ac.chula.fims.constants.ConfigurationConstant;
import th.ac.chula.fims.constants.EntityConstant;
import th.ac.chula.fims.exceptions.BadFormatException;
import th.ac.chula.fims.exceptions.ResourceNotFoundException;
import th.ac.chula.fims.models.tables.PatternAlignmentAllele;
import th.ac.chula.fims.models.tables.PatternAlignmentMotif;
import th.ac.chula.fims.models.tables.PatternAlignmentLocus;
import th.ac.chula.fims.models.tables.configuration.Configuration;
import th.ac.chula.fims.payload.response.MessageResponse;
import th.ac.chula.fims.repository.tables.PatternAlignmentLocusRepository;
import th.ac.chula.fims.repository.configuration.ConfigurationRepository;
import th.ac.chula.fims.repository.tables.PatternAlignmentAlleleRepository;
import th.ac.chula.fims.repository.tables.PatternAlignmentMotifRepository;
import th.ac.chula.fims.services.interfaces.ConfigurationService;
import th.ac.chula.fims.utils.CSVUtils;
import th.ac.chula.fims.utils.SequenceUtils;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ConfigurationServiceImpl implements ConfigurationService {
    private final ConfigurationRepository configurationRepository;
    private final PatternAlignmentLocusRepository patternAlignmentLocusRepository;
    private final PatternAlignmentAlleleRepository patternAlignmentAlleleRepository;
    private final PatternAlignmentMotifRepository patternAlignmentMotifRepository;


    public ConfigurationServiceImpl(ConfigurationRepository configurationRepository,
                                    PatternAlignmentLocusRepository patternAlignmentLocusRepository,
                                    PatternAlignmentAlleleRepository patternAlignmentAlleleRepository,
                                    PatternAlignmentMotifRepository patternAlignmentMotifRepository) {
        this.configurationRepository = configurationRepository;
        this.patternAlignmentLocusRepository = patternAlignmentLocusRepository;
        this.patternAlignmentAlleleRepository = patternAlignmentAlleleRepository;
        this.patternAlignmentMotifRepository = patternAlignmentMotifRepository;
    }

    @Override
    @Transactional
    public Map<String, String> getConfigurationByConfigurationKey(List<String> configurationKey) {
        List<Configuration> configuration = configurationRepository.findByConfigurationKeyIn(configurationKey);
        Map<String, String> configurationMap = new HashedMap<>();

        for (Configuration config : configuration) {
            configurationMap.put(config.getConfigurationKey(), config.getConfigurationValue());
        }

        return configurationMap;
    }

    @Override
    @Transactional
    public MessageResponse updateConfigurationByKeys(List<Configuration> configurations) throws Exception {
        for (Configuration config : configurations) {
            Optional<Configuration> curConfig =
                    configurationRepository.findByConfigurationKey(config.getConfigurationKey());
            if (curConfig.isPresent()) {
                curConfig.get().setConfigurationValue(config.getConfigurationValue());
                configurationRepository.save(curConfig.get());
            } else {
                throw new ResourceNotFoundException(EntityConstant.CONFIGURATION_KEY, config.getConfigurationKey());
            }
        }
        return new MessageResponse("Success");
    }

    @Override
    @Transactional
    public MessageResponse updateReferencePatterns(MultipartFile file) throws IOException {
        Workbook workbook = WorkbookFactory.create(file.getInputStream());
        int numberOfSheets = workbook.getNumberOfSheets();

        // Clear old data
        patternAlignmentMotifRepository.deleteAll();
        patternAlignmentAlleleRepository.deleteAll();
        patternAlignmentLocusRepository.deleteAll();

        for (int i = 0; i < numberOfSheets; i++) {
            Sheet sheet = workbook.getSheetAt(i);

            // Skip header
            for (int j = 1; j <= sheet.getLastRowNum(); j++) {
                List<String> data = CSVUtils.getCells(sheet.getRow(j));
                String locus = data.get(0);
                String sequence = data.get(1);
                float allele = resolveAllele(data.get(2));
                Boolean isReverse = resolveSampleSequencePattern(data.get(3));

                Optional<PatternAlignmentLocus> SALocus = patternAlignmentLocusRepository.findByLocus(locus);
                PatternAlignmentLocus patternAlignmentLocus;

                if (SALocus.isEmpty()) {
                    patternAlignmentLocus = new PatternAlignmentLocus();
                    patternAlignmentLocus.setLocus(locus);
                    patternAlignmentLocus.setPatternAlignmentAlleles(new ArrayList<>());
                    patternAlignmentLocus.setIsReverse(isReverse);
                    patternAlignmentLocusRepository.save(patternAlignmentLocus);
                } else {
                    patternAlignmentLocus = SALocus.get();
                    patternAlignmentLocus.setIsReverse(isReverse);
                }

                PatternAlignmentAllele toSaveOrUpdatePatternAlignmentAllele;

                if (patternAlignmentLocus.getPatternAlignmentAlleles() == null || patternAlignmentLocus.getPatternAlignmentAlleles().isEmpty()) {
                    toSaveOrUpdatePatternAlignmentAllele = new PatternAlignmentAllele();
                    toSaveOrUpdatePatternAlignmentAllele.setPatternAlignmentLocus(patternAlignmentLocus);
                    toSaveOrUpdatePatternAlignmentAllele.setAllele(allele);
                    toSaveOrUpdatePatternAlignmentAllele.setMotifs(new ArrayList<>());
                    patternAlignmentAlleleRepository.save(toSaveOrUpdatePatternAlignmentAllele);
                } else {
                    toSaveOrUpdatePatternAlignmentAllele = patternAlignmentLocus.getPatternAlignmentAlleles().stream()
                            .filter(a -> a.getAllele().equals(allele)).findFirst().orElse(null);
                }

                if (toSaveOrUpdatePatternAlignmentAllele == null) {
                    toSaveOrUpdatePatternAlignmentAllele = new PatternAlignmentAllele();
                    toSaveOrUpdatePatternAlignmentAllele.setPatternAlignmentLocus(patternAlignmentLocus);
                    toSaveOrUpdatePatternAlignmentAllele.setAllele(allele);
                    toSaveOrUpdatePatternAlignmentAllele.setMotifs(new ArrayList<>());
                    patternAlignmentAlleleRepository.save(toSaveOrUpdatePatternAlignmentAllele);
                }

                if (!toSaveOrUpdatePatternAlignmentAllele.getMotifs().isEmpty()) {
                    patternAlignmentMotifRepository.deleteAll(toSaveOrUpdatePatternAlignmentAllele.getMotifs());
                }

                List<PatternAlignmentMotif> motifDtoList = SequenceUtils.convertRepeatMotifsToListOfMotif(sequence);
                List<PatternAlignmentMotif> motifList = new ArrayList<>();

                for (int k = 0; k < motifDtoList.size(); k++) {
                    PatternAlignmentMotif toSaveMotif = new PatternAlignmentMotif();
                    Integer amount = motifDtoList.get(k).getAmount();
                    toSaveMotif.setPatternAlignmentAllele(toSaveOrUpdatePatternAlignmentAllele);
                    toSaveMotif.setSeqNo(k);
                    toSaveMotif.setMotif(motifDtoList.get(k).getMotif());
                    if (amount > 0) {
                        toSaveMotif.setAmount(amount);
                    }
                    motifList.add(toSaveMotif);
                }

                patternAlignmentMotifRepository.saveAll(motifList);
            }
        }

        Optional<Configuration> thereNewReferencedPattern =
                configurationRepository.findByConfigurationKey(ConfigurationConstant.IS_THERE_NEW_REFERENCED_PATTERN);
        Optional<Configuration> notificationEnabled =
                configurationRepository.findByConfigurationKey(ConfigurationConstant.REQUIRED_UPDATE_PATTERN_NOTIFICATION_ENABLED);
        if (thereNewReferencedPattern.isPresent()) {
            Configuration configuration = thereNewReferencedPattern.get();
            configuration.setConfigurationValue("1");
            configurationRepository.save(configuration);
        }
        if (notificationEnabled.isPresent()) {
            Configuration configuration = notificationEnabled.get();
            configuration.setConfigurationValue("1");
            configurationRepository.save(configuration);
        }

        return new MessageResponse("Success");
    }

    private static Boolean resolveSampleSequencePattern(String data) {
        if (data.equalsIgnoreCase("Reverse")) {
            return true;
        } else if (data.equalsIgnoreCase("Forward")) {
            return false;
        }
        throw new BadFormatException("Sample Sequence Pattern's value is invalid");
    }

    private static float resolveAllele(String allele) {
        try {
            return Float.parseFloat(allele);
        } catch (Exception ex) {
            return 0f;
        }
    }
}
