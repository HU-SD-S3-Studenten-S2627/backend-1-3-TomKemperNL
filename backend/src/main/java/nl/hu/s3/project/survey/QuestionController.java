package nl.hu.s3.project.survey;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/questions")
@Transactional
public class QuestionController {

    private EntityManager entities;

    public QuestionController(EntityManager entities) {
        this.entities = entities;
    }

    @GetMapping
    public List<Question> getQuestions(){
        return this.entities
                .createQuery("select q from Question q", Question.class)
                .getResultList();
    }

    @GetMapping("/{id}")
    public Question getQuestion(@PathVariable  Long id){
        return this.entities.find(Question.class, id);
    }

    @PostMapping
    public Question newQuestion(@RequestBody NewQuestionRequest dto){
        Question q = new Question(dto.label());
        for(String answer: dto.answers()){
            Answer newAnswer = new Answer(answer);
//            entities.persist(newAnswer);
            q.getAnswers().add(newAnswer);
        }
        entities.persist(q);
        return q;
    }

    @PutMapping("/{id}")
    public Question updateQuestion(@PathVariable  Long id, @RequestBody NewQuestionRequest dto){
        Question q = this.entities.find(Question.class, id);
        q.getAnswers().clear();
        for(String answer: dto.answers()){
            Answer newAnswer = new Answer(answer);
            q.getAnswers().add(newAnswer);
        }
        q.setLabel(dto.label());
        return q;
    }
}
