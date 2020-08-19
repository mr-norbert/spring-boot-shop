package bnorbert.onlineshop.transfer.questionsAndAnswers;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.Instant;

@Getter
@Setter
@ToString
public class AnswerResponse {

    private Long id;
    private String text;
    private Integer voteCount;
    private Instant createdDate;
    private Long userId;
    private Long questionId;
}
