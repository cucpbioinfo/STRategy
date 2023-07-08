package th.ac.chula.fims.services.bean;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import th.ac.chula.fims.exceptions.ResourceNotFoundException;
import th.ac.chula.fims.payload.dto.CountryTableDto;
import th.ac.chula.fims.payload.dto.SearchProfileDto;
import th.ac.chula.fims.services.interfaces.searchprofile.ProfileSearchAlgorithm;

import java.util.List;

@Service
public class BeanFactoryDynamicAutowireService {
    private static final String SERVICE_NAME_SUFFIX = "ProfileSearchAlgorithm";
    private final BeanFactory beanFactory;

    @Autowired
    public BeanFactoryDynamicAutowireService(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    public List<CountryTableDto> searchByProfile(String algorithmName, List<SearchProfileDto> searchProfileDtoList) {
        try {
            ProfileSearchAlgorithm service = beanFactory.getBean(getRegionServiceBeanName(algorithmName), ProfileSearchAlgorithm.class);
            return service.performProfileSearch(searchProfileDtoList);
        } catch (NoSuchBeanDefinitionException exception) {
            throw new ResourceNotFoundException(String.format("Algorithm %s not found", algorithmName));
        }
    }

    private String getRegionServiceBeanName(String algorithm) {
        return algorithm + SERVICE_NAME_SUFFIX;
    }
}