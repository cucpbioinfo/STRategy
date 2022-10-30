package th.ac.chula.fims.services;

import org.springframework.stereotype.Service;
import th.ac.chula.fims.payload.dto.ProvinceDto;
import th.ac.chula.fims.repository.tables.ProvinceRepository;
import th.ac.chula.fims.services.interfaces.ProvinceService;
import th.ac.chula.fims.utils.mapper.ProvinceMapper;

import java.util.List;

@Service
public class ProvinceServiceImpl implements ProvinceService {
    private final ProvinceRepository provinceRepository;
    private final ProvinceMapper provinceMapper;

    public ProvinceServiceImpl(ProvinceRepository provinceRepository, ProvinceMapper provinceMapper) {
        this.provinceRepository = provinceRepository;
        this.provinceMapper = provinceMapper;
    }

    @Override
    public List<ProvinceDto> getProvincesByIds(List<Integer> ids) {
        return provinceMapper.toProvinceDto(provinceRepository.findAllById(ids));
    }
}
