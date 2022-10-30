package th.ac.chula.fims.services.interfaces;

import org.springframework.web.multipart.MultipartFile;
import th.ac.chula.fims.models.Enum.UploadProfileMode;
import th.ac.chula.fims.payload.dto.CountryTableDto;
import th.ac.chula.fims.payload.dto.SearchProfileDto;
import th.ac.chula.fims.payload.response.MessageResponse;

import java.io.IOException;
import java.util.List;

public interface ProfileSearchService {
    MessageResponse uploadExcelSearchProfileAll(MultipartFile file) throws IOException;

    MessageResponse uploadCSVSearchProfileAll(MultipartFile file);

    MessageResponse uploadExcelSearchProfileByCountry(MultipartFile file, String country, UploadProfileMode mode) throws IOException;

    MessageResponse uploadCSVSearchProfileByCountry(MultipartFile file, String country, UploadProfileMode mode);

    List<CountryTableDto> searchProfileAll(String algorithm, List<SearchProfileDto> searchProfileDtoList);
}
