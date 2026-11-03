package nl.hu.s3.project.survey;

import java.util.List;

public record NewQuestionRequest(String label, List<String> answers) {
}
