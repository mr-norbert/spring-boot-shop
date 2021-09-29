package bnorbert.onlineshop.transfer.comment;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class CommentResponse {
    private Long id;
    private String text;
    private Integer voteCount;
    private String userEmail;
    private Long userId;
    private Long reviewId;

}
