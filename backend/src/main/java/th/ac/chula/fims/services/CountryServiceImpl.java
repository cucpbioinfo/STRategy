package th.ac.chula.fims.services;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import th.ac.chula.fims.exceptions.BadFormatException;
import th.ac.chula.fims.models.tables.Country;
import th.ac.chula.fims.models.tables.Province;
import th.ac.chula.fims.models.tables.Region;
import th.ac.chula.fims.payload.dto.CountryDto;
import th.ac.chula.fims.payload.dto.ProvinceDto;
import th.ac.chula.fims.payload.dto.ProvinceUpdateDto;
import th.ac.chula.fims.payload.response.MessageResponse;
import th.ac.chula.fims.repository.tables.CountryRepository;
import th.ac.chula.fims.repository.tables.ProvinceRepository;
import th.ac.chula.fims.repository.tables.RegionRepository;
import th.ac.chula.fims.services.interfaces.CountryService;
import th.ac.chula.fims.utils.mapper.CountryMapper;
import th.ac.chula.fims.utils.mapper.ProvinceMapper;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class CountryServiceImpl implements CountryService {
    private final CountryRepository countryRepository;
    private final CountryMapper countryMapper;
    private final ProvinceRepository provinceRepository;
    private final ProvinceMapper provinceMapper;
    private final RegionRepository regionRepository;

    public CountryServiceImpl(CountryRepository countryRepository, CountryMapper countryMapper, ProvinceRepository provinceRepository, ProvinceMapper provinceMapper, RegionRepository regionRepository) {
        this.countryRepository = countryRepository;
        this.countryMapper = countryMapper;
        this.provinceRepository = provinceRepository;
        this.provinceMapper = provinceMapper;
        this.regionRepository = regionRepository;
    }

    @Override
    public List<CountryDto> getAllCountries() {
        List<Country> countryList = countryRepository.findAll();
        return countryMapper.toCountryDtoList(countryList);
    }

    @Override
    public MessageResponse updateCountryById(CountryDto country) {
        int countryId = country.getId();
        Optional<Country> toUpdateCountry = countryRepository.findById(countryId);

        if (toUpdateCountry.isPresent()) {
            Country countryEntity = toUpdateCountry.get();
            String countryName = StringUtils.capitalize(country.getCountry());
            countryEntity.setCountry(countryName);
            countryRepository.save(countryEntity);
            return new MessageResponse(String.format("Country id: %d has been updated", countryId));
        }

        return new MessageResponse(String.format("Country id: %d not found", countryId));
    }

    @Override
    public MessageResponse addNewCountry(CountryDto country) {
        String countryName = StringUtils.capitalize(country.getCountry());

        Country byCountry = countryRepository.findByCountry(countryName);

        if (byCountry != null) {
            throw new BadFormatException(String.format("Country: %s is already existed", countryName));
        }

        Country newCountry = new Country();
        newCountry.setCountry(countryName);
        countryRepository.save(newCountry);

        return new MessageResponse("Success");
    }

    @Override
    public CountryDto getCountryById(int countryId) {
        Optional<Country> country = countryRepository.findById(countryId);
        if (country.isPresent()) {
            return countryMapper.toCountry(country.get());
        }
        return new CountryDto();
    }

    @Override
    public List<ProvinceDto> getProvincesByCountryId(int countryId) {
        List<Province> provinceList = provinceRepository.findAllByCountryId(countryId);
        return provinceMapper.toProvinceDto(provinceList);
    }

    @Override
    public void updateProvincesByCountryId(int countryId, ProvinceUpdateDto provinceUpdateDto) {
        List<ProvinceDto> provinceToUpdateList = provinceUpdateDto.getProvinceToUpdateList();

        Map<Integer, Province> hashedMapProvince = new HashMap<>();
        Map<Integer, Region> hashedMapRegion = new HashMap<>();

        List<Integer> toUpdatedProvinceIds = provinceToUpdateList.stream().map(ProvinceDto::getId).collect(Collectors.toList());
        List<Province> toDeleteProvinces = provinceRepository.findAllById(provinceUpdateDto.getDeleteProvinceIds());
        List<Province> toUpdateProvinces = new ArrayList<>();

        List<Region> regions = regionRepository.findAllByCountryId(countryId);
        for (Region region : regions) {
            hashedMapRegion.put(region.getId(), region);
        }

        List<Province> provincesInDatabase = provinceRepository.findAllById(toUpdatedProvinceIds);
        for (Province toUpdateProvince : provincesInDatabase) {
            hashedMapProvince.put(toUpdateProvince.getId(), toUpdateProvince);
        }

        for (ProvinceDto provinceDto : provinceToUpdateList) {
            Province province = hashedMapProvince.get(provinceDto.getId());

            if (province == null) {
                province = new Province();
            }

            Region region = hashedMapRegion.get(provinceDto.getRegion().getId());
            province.setProvince(provinceDto.getProvince());
            province.setLatitude(provinceDto.getLatitude());
            province.setLongitude(provinceDto.getLongitude());
            province.setNativeName(provinceDto.getNativeName());
            province.setRegion(region);

            toUpdateProvinces.add(province);
        }


        provinceRepository.saveAll(toUpdateProvinces);
        provinceRepository.deleteAll(toDeleteProvinces);
    }
}
