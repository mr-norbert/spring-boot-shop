package bnorbert.onlineshop.transfer.questionsAndAnswers;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class AnswerDto {

    private Long questionId;
    private String text;
}
