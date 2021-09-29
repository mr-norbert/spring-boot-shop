package bnorbert.onlineshop.transfer.questionsAndAnswers;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class CreateQuestionRequest {

    private Long productId;
    private String text;
}
