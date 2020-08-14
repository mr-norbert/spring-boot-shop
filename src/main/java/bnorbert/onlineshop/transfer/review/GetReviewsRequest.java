package bnorbert.onlineshop.transfer.review;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class GetReviewsRequest {
    private Long productId;
    private Integer rating;

}
