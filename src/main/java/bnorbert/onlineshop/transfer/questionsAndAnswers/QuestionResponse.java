package bnorbert.onlineshop.transfer.questionsAndAnswers;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class QuestionResponse {
    private Long id;
    private String text;
    private Instant createdDate;
    private Long userId;
    private Long productId;
}
