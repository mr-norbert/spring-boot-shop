package bnorbert.onlineshop.transfer.questionsAndAnswers;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class CreateAnswerRequest {

    private Long questionId;
    private String text;

    @Override
    public String toString() {
        return "CreateAnswerRequest{" +
                "questionId=" + questionId +
                ", text='" + text + '\'' +
                '}';
    }
}
