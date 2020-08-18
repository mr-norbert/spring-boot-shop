package bnorbert.onlineshop.transfer.questionsAndAnswers;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class QuestionDto {

    private Long productId;
    private String text;
}
