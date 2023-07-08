package th.ac.chula.fims.services.interfaces;

import org.springframework.web.multipart.MultipartFile;
import th.ac.chula.fims.payload.dto.CoreLocusDto;
import th.ac.chula.fims.payload.response.MessageResponse;

import java.io.IOException;
import java.util.List;

public interface CoreLociService {
    List<CoreLocusDto> getCoreLociByCountry(String country);

    MessageResponse uploadCoreLociByCountry(MultipartFile file) throws IOException;

    List<String> getAllCountryInCoreLoci();
}
