package th.ac.chula.fims.services.interfaces;

import org.springframework.web.multipart.MultipartFile;
import th.ac.chula.fims.models.tables.configuration.Configuration;
import th.ac.chula.fims.payload.response.MessageResponse;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface ConfigurationService {
    Map<String, String> getConfigurationByConfigurationKey(List<String> configurationKey);

    MessageResponse updateConfigurationByKeys(List<Configuration> configurations) throws Exception;

    MessageResponse updateReferencePatterns(MultipartFile file) throws IOException;
}
