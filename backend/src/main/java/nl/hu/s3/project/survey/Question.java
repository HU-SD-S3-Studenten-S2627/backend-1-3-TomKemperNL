package nl.hu.s3.project.survey;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

@Entity
public class Question {

    protected Question() {

    }

    public Long getId() {
        return id;
    }

    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "questionLabel")
    private String label;

    public Question(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
