package th.ac.chula.fims.controllers;

import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import th.ac.chula.fims.models.Enum.EGender;
import th.ac.chula.fims.models.tables.Person;
import th.ac.chula.fims.payload.request.PersonCustom;
import th.ac.chula.fims.payload.response.person.PersonsPages;
import th.ac.chula.fims.services.interfaces.PersonService;

import java.util.List;

@RestController
@RequestMapping("/api/persons")
public class PersonController {
    private final PersonService personService;

    public PersonController(PersonService personService) {
        this.personService = personService;
    }

    @GetMapping("/")
    public ResponseEntity<?> getPersonsWithPagination(@RequestParam(required = false, defaultValue = "%") String firstname,
                                                      @RequestParam(required = false, defaultValue = "%") String lastname,
                                                      @RequestParam(required = false) List<EGender> gender,
                                                      @RequestParam(required = false) Integer age,
                                                      @RequestParam(required = false, defaultValue = "%") String race,
                                                      @RequestParam(required = false, defaultValue = "%") String country,
                                                      @RequestParam(required = false, defaultValue = "%") String province,
                                                      Pageable pageable) {
        PersonsPages personRes = personService.getAllPersonByPageAndSize(
                gender,
                race,
                country,
                province,
                pageable);
        return ResponseEntity.status(HttpStatus.OK).body(personRes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getPersonById(@PathVariable Integer id) {
        Person person = personService.getPersonById(id);
        return ResponseEntity.status(HttpStatus.OK).body(person);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updatePerson(@PathVariable Integer id, @RequestBody PersonCustom body) {
        String message = personService.updatePerson(id, body);

        if (message != null) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("{id}")
    public ResponseEntity<?> deletePerson(@PathVariable Integer id) {
        return ResponseEntity.status(HttpStatus.OK).body(personService.deletePersonById(id));
    }
}
