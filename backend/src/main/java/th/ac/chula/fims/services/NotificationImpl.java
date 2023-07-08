package th.ac.chula.fims.services;

import org.springframework.stereotype.Service;
import th.ac.chula.fims.constants.ConfigurationConstant;
import th.ac.chula.fims.models.tables.configuration.Configuration;
import th.ac.chula.fims.payload.response.notification.NotificationMenu;
import th.ac.chula.fims.repository.configuration.ConfigurationRepository;
import th.ac.chula.fims.services.interfaces.NotificationService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class NotificationImpl implements NotificationService {
    private final ConfigurationRepository configurationRepository;

    public NotificationImpl(ConfigurationRepository configurationRepository) {
        this.configurationRepository = configurationRepository;
    }

    @Override
    public NotificationMenu getPatternAlignmentNotification() {
        List<String> keys = new ArrayList<>();
        int count = 0;
        keys.add(ConfigurationConstant.NUMBER_OF_NEW_SAMPLES_SINCE_UPDATE_PATTERN_ALIGNMENT);
        keys.add(ConfigurationConstant.IS_THERE_NEW_REFERENCED_PATTERN);
        keys.add(ConfigurationConstant.REQUIRED_UPDATE_PATTERN_NOTIFICATION_ENABLED);
        List<Configuration> configurationList = configurationRepository.findByConfigurationKeyIn(keys);
        if (!configurationList.isEmpty()) {
            String configurationValue1 = configurationList.get(0).getConfigurationValue();
            String configurationValue2 = configurationList.get(1).getConfigurationValue();
            if (!configurationValue2.equals("0") || !configurationValue1.equals("0")) {
                count = 1;
            }
            Optional<Configuration> configAck = configurationList.stream().filter(e -> e.getConfigurationKey().equals(ConfigurationConstant.REQUIRED_UPDATE_PATTERN_NOTIFICATION_ENABLED)).findFirst();
            if (configAck.isPresent()) {
                String acknowledge = configAck.get().getConfigurationValue();
                if (!acknowledge.equals("1")) {
                    count = 0;
                }
            }
        }
        return new NotificationMenu("Pattern Alignment", count);
    }
}
