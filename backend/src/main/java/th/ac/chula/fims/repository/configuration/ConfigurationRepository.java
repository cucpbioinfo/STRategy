package th.ac.chula.fims.repository.configuration;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import th.ac.chula.fims.models.tables.configuration.Configuration;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConfigurationRepository extends JpaRepository<Configuration, Long> {
    List<Configuration> findByConfigurationKeyIn(List<String> configurationKey);

    Optional<Configuration> findByConfigurationKey(String key);
}
