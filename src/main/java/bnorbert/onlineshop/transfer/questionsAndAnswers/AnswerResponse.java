package bnorbert.onlineshop.transfer.questionsAndAnswers;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class AnswerResponse {

    private Long id;
    private String text;
    private Integer voteCount;
    private Instant createdDate;
    private Long userId;
    private Long questionId;

    @Override
    public String toString() {
        return "AnswerResponse{" +
                "id=" + id +
                ", text='" + text + '\'' +
                ", voteCount=" + voteCount +
                ", createdDate=" + createdDate +
                ", userId=" + userId +
                ", questionId=" + questionId +
                '}';
    }
}
