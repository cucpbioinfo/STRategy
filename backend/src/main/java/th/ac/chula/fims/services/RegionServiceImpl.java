package th.ac.chula.fims.services;

import org.springframework.stereotype.Service;
import th.ac.chula.fims.models.tables.Country;
import th.ac.chula.fims.models.tables.Province;
import th.ac.chula.fims.models.tables.Region;
import th.ac.chula.fims.payload.dto.RegionDto;
import th.ac.chula.fims.payload.response.MessageResponse;
import th.ac.chula.fims.repository.tables.CountryRepository;
import th.ac.chula.fims.repository.tables.ProvinceRepository;
import th.ac.chula.fims.repository.tables.RegionRepository;
import th.ac.chula.fims.services.interfaces.RegionService;

import java.util.List;
import java.util.Optional;

@Service
public class RegionServiceImpl implements RegionService {
    private final RegionRepository regionRepository;
    private final CountryRepository countryRepository;
    private final ProvinceRepository provinceRepository;

    public RegionServiceImpl(RegionRepository regionRepository, CountryRepository countryRepository, ProvinceRepository provinceRepository) {
        this.regionRepository = regionRepository;
        this.countryRepository = countryRepository;
        this.provinceRepository = provinceRepository;
    }

    @Override
    public List<Region> getRegionsByCountryId(Integer countryId) {
        return regionRepository.findAllByCountryId(countryId);
    }

    @Override
    public MessageResponse addNewRegionByCountryId(int countryId, RegionDto regionDto) {
        Optional<Country> country = countryRepository.findById(countryId);

        if (country.isPresent()) {
            Country countryEntity = country.get();
            Region newRegion = new Region();
            newRegion.setCountry(countryEntity);
            newRegion.setRegion(regionDto.getRegion());
            regionRepository.save(newRegion);

            return new MessageResponse("Region has been added");
        }

        return new MessageResponse(String.format("Country ID: %d not found", countryId));
    }

    @Override
    public MessageResponse updateRegionById(RegionDto regionDto) {
        Optional<Region> region = regionRepository.findById(regionDto.getId());

        if (region.isPresent()) {
            Region regionEntity = region.get();
            regionEntity.setRegion(regionDto.getRegion());
            regionRepository.save(regionEntity);

            return new MessageResponse("Region has been updated");
        }

        return new MessageResponse(String.format("Region ID: %d not found", regionDto.getId()));
    }

    @Override
    public MessageResponse deleteRegionById(int regionId) {
        Optional<Region> region = regionRepository.findById(regionId);

        if (region.isPresent()) {
            regionRepository.delete(region.get());

            return new MessageResponse("Region has been deleted");
        }

        return new MessageResponse(String.format("Region ID: %d not found", regionId));
    }

    @Override
    public List<Province> getProvincesByRegionId(Integer regionId) {
        return provinceRepository.findAllByRegionId(regionId);
    }
}
