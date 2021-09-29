package bnorbert.onlineshop.transfer.vote;

import bnorbert.onlineshop.domain.CommentsVoteType;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateVoteRequest {

    private CommentsVoteType commentsVoteType;
    private Long commentId;
}
