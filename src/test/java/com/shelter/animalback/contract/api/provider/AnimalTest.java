package com.shelter.animalback.contract.api.provider;

import au.com.dius.pact.provider.junit5.PactVerificationContext;
import au.com.dius.pact.provider.junit5.PactVerificationInvocationContextProvider;
import au.com.dius.pact.provider.junitsupport.Provider;
import au.com.dius.pact.provider.junitsupport.State;
import au.com.dius.pact.provider.junitsupport.loader.PactBroker;
import au.com.dius.pact.provider.junitsupport.loader.PactBrokerAuth;
import au.com.dius.pact.provider.spring.junit5.MockMvcTestTarget;
import com.shelter.animalback.controller.AnimalController;
import com.shelter.animalback.domain.Animal;
import com.shelter.animalback.service.interfaces.AnimalService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.assertTrue;

@PactBroker(
        url = "${PACT_BROKER_URL}",
        authentication = @PactBrokerAuth(token = "${PACT_BROKER_TOKEN}")
)
@Provider("AnimalShelterBack")
@ExtendWith(MockitoExtension.class)
public class AnimalTest {

    @Mock
    private AnimalService animalService;
    @InjectMocks
    private AnimalController animalController;

    @TestTemplate
    @ExtendWith(PactVerificationInvocationContextProvider.class)
    public void pactVerificationTestTemplate(PactVerificationContext context) {
        context.verifyInteraction();
    }

    @BeforeEach
    public void changeContext(PactVerificationContext context) {
        MockMvcTestTarget testTarget = new MockMvcTestTarget();
        testTarget.setControllers(animalController);
        context.setTarget(testTarget);
    }

    @State("create animal")
    public void createAnimal() {
        Animal animal = new Animal();

        animal.setName("Manchitas");
        animal.setBreed("Criollo");
        animal.setGender("Female");
        animal.setVaccinated(true);
        Mockito.when(animalService.save(Mockito.any(Animal.class))).thenReturn(animal);
    }

    @State("has animals")
    public void addAnimals() {
        Animal animal = new Animal();
        animal.setName("Manchas");
        animal.setBreed("Bengali");
        animal.setGender("Female");
        animal.setVaccinated(true);

        ArrayList<Animal> animals = new ArrayList<Animal>();
        animals.add(animal);

        Mockito.when(animalService.getAll()).thenReturn(animals);
    }

    @State("delete animal")
    public void deleteAnimal(){
        String animalName = "Manchas";
        Mockito.doAnswer((i) -> {
            assertTrue(animalName.equals(i.getArgument(0)));
            return null;
        }).when(animalService).delete(animalName);
    }
}
