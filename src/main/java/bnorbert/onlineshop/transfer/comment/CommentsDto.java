package bnorbert.onlineshop.transfer.comment;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class CommentsDto {

    private Long reviewId;
    private String text;
}
