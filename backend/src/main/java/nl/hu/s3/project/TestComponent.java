package nl.hu.s3.project;

import jakarta.persistence.EntityManager;
import nl.hu.s3.project.survey.Question;
import org.springframework.stereotype.Component;

@Component
public class TestComponent
{
    private EntityManager entities;

    public TestComponent(EntityManager entities){
        this.entities = entities;
    }

    public void doiets(){
        System.out.println("Stekker zit erin");
        Question testVraag = new Question("Snapt men JPA?");
        this.entities.persist(testVraag);

    }
}
