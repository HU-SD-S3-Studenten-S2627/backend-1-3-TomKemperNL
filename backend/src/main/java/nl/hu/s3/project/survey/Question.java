package nl.hu.s3.project.survey;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

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


    @OneToMany(cascade = CascadeType.ALL)
    private List<Answer> answers = new ArrayList<>();

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

    public List<Answer> getAnswers() {
        return answers;
    }
}
