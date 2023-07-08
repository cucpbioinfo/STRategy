package th.ac.chula.fims.services;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import th.ac.chula.fims.constants.EntityConstant;
import th.ac.chula.fims.exceptions.ResourceNotFoundException;
import th.ac.chula.fims.models.Enum.EGender;
import th.ac.chula.fims.models.tables.*;
import th.ac.chula.fims.payload.request.PersonCustom;
import th.ac.chula.fims.payload.response.MessageResponse;
import th.ac.chula.fims.payload.response.person.ForenseqRow;
import th.ac.chula.fims.payload.response.person.ForenseqSequenceRow;
import th.ac.chula.fims.payload.response.person.PersonForenseq;
import th.ac.chula.fims.payload.response.person.PersonsPages;
import th.ac.chula.fims.repository.tables.*;
import th.ac.chula.fims.services.interfaces.PersonService;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
public class PersonServiceImpl implements PersonService {

    private final PersonRepository personRepository;

    private final CountryRepository countryRepository;

    private final RegionRepository regionRepository;

    private final ProvinceRepository provinceRepository;

    private final RaceRepository raceRepository;

    private final ForenseqRepository forenseqRepositiry;

    private final ForenseqSequenceRepository forenseqSequenceRepository;

    private final SampleRepository sampleRepository;

    public PersonServiceImpl(PersonRepository personRepository, CountryRepository countryRepository,
                             RegionRepository regionRepository, ProvinceRepository provinceRepository,
                             RaceRepository raceRepository, ForenseqRepository forenseqRepositiry,
                             ForenseqSequenceRepository forenseqSequenceRepository, SampleRepository sampleRepository) {
        this.personRepository = personRepository;
        this.countryRepository = countryRepository;
        this.regionRepository = regionRepository;
        this.provinceRepository = provinceRepository;
        this.raceRepository = raceRepository;
        this.forenseqRepositiry = forenseqRepositiry;
        this.forenseqSequenceRepository = forenseqSequenceRepository;
        this.sampleRepository = sampleRepository;
    }

    @Override
    @Transactional
    public String updatePerson(Integer id, PersonCustom body) {
        int raceId = body.getRace_id();
        int countryId = body.getCountry_id();
        int regionId = body.getRegion_id();
        int provinceId = body.getProvince_id();

        if (raceRepository.existsById(raceId) && countryRepository.existsById(countryId)
                && regionRepository.existsById(regionId) && provinceRepository.existsById(provinceId)) {

            Person person = personRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException(EntityConstant.PERSON, id));

            person.setGender(body.getGender());

            Country country = countryRepository.findById(countryId)
                    .orElseThrow(() -> new ResourceNotFoundException(EntityConstant.COUNTRY, countryId));
            Region region = regionRepository.findById(regionId)
                    .orElseThrow(() -> new ResourceNotFoundException(EntityConstant.REGION, regionId));
            Province province = provinceRepository.findById(provinceId)
                    .orElseThrow(() -> new ResourceNotFoundException(EntityConstant.PROVINCE, provinceId));
            Race race = raceRepository.findById(raceId)
                    .orElseThrow(() -> new ResourceNotFoundException(EntityConstant.RACE, raceId));

            person.setCountry(country);
            person.setRegion(region);
            person.setProvince(province);
            person.setRace(race);

            personRepository.save(person);

            return "OK";
        }

        return null;
    }

    @Override
    public Person getPersonById(Integer id) {
        return personRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(EntityConstant.PERSON, id));
    }

    @Override
    public PersonsPages getAllPersonByPageAndSize(List<EGender> gender, String race, String country, String province,
                                                  Pageable pageable) {
        List<Person> personList;
        Long count;
        if (gender == null) {
            personList = personRepository.findByRace_RaceLikeAndCountry_CountryLikeAndProvince_ProvinceLike(race,
                    country, province, pageable);
            count = personRepository.countByRace_RaceLikeAndCountry_CountryLikeAndProvince_ProvinceLike(race, country
                    , province);
        } else {
            personList =
                    personRepository.findByGenderInAndRace_RaceLikeAndCountry_CountryLikeAndProvince_ProvinceLike(gender, race, country, province, pageable);
            count = personRepository.countByGenderInAndRace_RaceLikeAndCountry_CountryLikeAndProvince_ProvinceLike(gender, race, country, province);
        }

        return new PersonsPages(personList, count);
    }

    @Override
    @Transactional
    public PersonForenseq getForenseqBySampleId(String id) {
        Sample sample = sampleRepository.findTopBySampleId(id)
                .orElseThrow(() -> new ResourceNotFoundException(EntityConstant.SAMPLE, id));
        List<ForenseqRow> fsA = new ArrayList<>();
        List<ForenseqRow> fsY = new ArrayList<>();
        List<ForenseqRow> fsX = new ArrayList<>();
        List<ForenseqRow> fsI = new ArrayList<>();
        List<ForenseqSequenceRow> fssA = new ArrayList<>();
        List<ForenseqSequenceRow> fssY = new ArrayList<>();
        List<ForenseqSequenceRow> fssX = new ArrayList<>();
        List<ForenseqSequenceRow> fssI = new ArrayList<>();
        List<Object[]> forenseqRowList = forenseqRepositiry.findAllForenseqBySampleId(sample.getId());
        List<Object[]> forenseqSequenceList =
                forenseqSequenceRepository.findAllForenseqSequenceBySampleId(sample.getId());

        for (Object[] value : forenseqRowList) {
            switch (value[0].toString()) {
                case "Autosome":
                    fsA.add(new ForenseqRow(value[2].toString(),
                            value[1].toString(),
                            value[3].toString()));
                    break;
                case "Y":
                    fsY.add(new ForenseqRow(value[2].toString(),
                            value[1].toString(),
                            value[3].toString()));
                    break;
                case "X":
                    fsX.add(new ForenseqRow(value[2].toString(),
                            value[1].toString(),
                            value[3].toString()));
                    break;
                default:
                    fsI.add(new ForenseqRow(value[2].toString(),
                            value[1].toString(),
                            value[3].toString()));
                    break;
            }
        }

        for (Object[] objects : forenseqSequenceList) {
            switch (objects[0].toString()) {
                case "Autosome":
                    fssA.add(new ForenseqSequenceRow(objects[1].toString(),
                            objects[2].toString(),
                            objects[3].toString(),
                            Integer.parseInt(objects[4].toString())));
                    break;
                case "Y":
                    fssY.add(new ForenseqSequenceRow(objects[1].toString(),
                            objects[2].toString(),
                            objects[3].toString(),
                            Integer.parseInt(objects[4].toString())));
                    break;
                case "X":
                    fssX.add(new ForenseqSequenceRow(objects[1].toString(),
                            objects[2].toString(),
                            objects[3].toString(),
                            Integer.parseInt(objects[4].toString())));
                    break;
                default:
                    fssI.add(new ForenseqSequenceRow(objects[1].toString(),
                            objects[2].toString(),
                            objects[3].toString(),
                            Integer.parseInt(objects[4].toString())));
                    break;
            }
        }

        return new PersonForenseq(sample.getPerson(), fsA, fsX, fsY, fsI, fssA, fssX, fssY, fssI);
    }

    @Override
    public MessageResponse deletePersonById(Integer id) {
        personRepository.deleteById(id);
        return new MessageResponse("Success");
    }

}
