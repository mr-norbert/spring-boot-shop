package bnorbert.onlineshop.transfer.questionsAndAnswers;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.Instant;

@Getter
@Setter
@ToString
public class QuestionResponse {
    private Long id;
    private String text;
    private Instant createdDate;
    private Long userId;
    private Long productId;
}
