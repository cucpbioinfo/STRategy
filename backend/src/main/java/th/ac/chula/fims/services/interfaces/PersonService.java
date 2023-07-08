package th.ac.chula.fims.services.interfaces;

import org.springframework.data.domain.Pageable;
import th.ac.chula.fims.models.Enum.EGender;
import th.ac.chula.fims.models.tables.Person;
import th.ac.chula.fims.payload.request.PersonCustom;
import th.ac.chula.fims.payload.response.MessageResponse;
import th.ac.chula.fims.payload.response.person.PersonForenseq;
import th.ac.chula.fims.payload.response.person.PersonsPages;

import javax.transaction.Transactional;
import java.util.List;

public interface PersonService {
    @Transactional
    String updatePerson(Integer id, PersonCustom body);

    Person getPersonById(Integer id);

    PersonsPages getAllPersonByPageAndSize(List<EGender> gender, String race, String country, String province, Pageable pageable);

    @Transactional
    PersonForenseq getForenseqBySampleId(String id);

    MessageResponse deletePersonById(Integer id);
}
