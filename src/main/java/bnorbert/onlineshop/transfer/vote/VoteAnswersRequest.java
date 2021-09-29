package bnorbert.onlineshop.transfer.vote;

import bnorbert.onlineshop.domain.AnswersVoteType;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VoteAnswersRequest {

    private AnswersVoteType answersVoteType;
    private Long answerId;


}
