package bnorbert.onlineshop.transfer.vote;

import bnorbert.onlineshop.domain.AnswersVoteType;
import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class VoteAnswersDto {

    private AnswersVoteType answersVoteType;
    private Long answerId;


}
