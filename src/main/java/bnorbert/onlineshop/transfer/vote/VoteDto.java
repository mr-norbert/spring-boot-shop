package bnorbert.onlineshop.transfer.vote;

import bnorbert.onlineshop.domain.CommentsVoteType;
import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class VoteDto {

    private CommentsVoteType commentsVoteType;
    private Long commentId;
}
