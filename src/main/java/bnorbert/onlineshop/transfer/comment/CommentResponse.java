package bnorbert.onlineshop.transfer.comment;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class CommentResponse {
    private Long id;
    private String text;
    private Integer voteCount;
    private String userEmail;
    private Long userId;
    private Long reviewId;

}
