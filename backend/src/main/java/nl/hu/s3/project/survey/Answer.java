package nl.hu.s3.project.survey;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

@Entity
public class Answer {
    @Id
    @GeneratedValue
    private Long id;

    private String label;

    protected Answer(){}

    public Answer(String label) {
        this.label = label;
    }

    public Long getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }
}
