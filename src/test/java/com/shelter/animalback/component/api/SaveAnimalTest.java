package com.shelter.animalback.component.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shelter.animalback.repository.AnimalRepository;
import jdk.jfr.ContentType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.hamcrest.MatcherAssert.assertThat;

@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class SaveAnimalTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AnimalRepository animalRepository;

    @Test
    @SneakyThrows
    public void createAnimalSuccessful() {
        var animal = new CreateAnimalRequestBody();
        animal.setName("Pupito");
        animal.setBreed("Mestizo");
        animal.setGender("Male");
        animal.setVaccinated(false);
        var createAnimalRequestBody = new ObjectMapper().writeValueAsString(animal);

        var response = mockMvc.perform(
                post("/animals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createAnimalRequestBody))
                .andReturn()
                .getResponse();

        var stringResponse = response.getContentAsString();
        var animalResponse = new ObjectMapper().readValue(stringResponse, CreateAnimalResponse.class);

        assertThat(animalResponse.getName(), equalTo("Pupito"));
        assertThat(animalResponse.getBreed(), equalTo("Mestizo"));
        assertThat(animalResponse.getGender(), equalTo("Male"));
        assertThat(animalResponse.isVaccinated(), equalTo(false));

        assertThat(animalResponse.getId(), notNullValue());

        var dbQuery = animalRepository.findById(animalResponse.getId());
        assertThat(dbQuery.isPresent(), is(true));

        var animalDB = dbQuery.get();
        assertThat(animalDB.getName(), equalTo("Pupito"));
        assertThat(animalDB.getBreed(), equalTo("Mestizo"));
        assertThat(animalDB.getGender(), equalTo("Male"));
        assertThat(animalDB.isVaccinated(), equalTo(false));
    }


    // ------------------------ DTO HELPERS ------------------------ //
    @Getter
    @Setter
    @NoArgsConstructor
    public class CreateAnimalRequestBody {
        private String name;
        private String gender;
        private String breed;
        private boolean vaccinated;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class CreateAnimalResponse {
        private long id;
        private String name;
        private String gender;
        private String breed;
        private boolean vaccinated;
        private List<String> vaccines;
    }
}
