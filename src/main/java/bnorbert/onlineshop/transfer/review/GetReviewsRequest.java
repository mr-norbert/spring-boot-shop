package bnorbert.onlineshop.transfer.review;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetReviewsRequest {
    private Long productId;
    private Integer rating;

    @Override
    public String toString() {
        return "GetReviewsRequest{" +
                "productId=" + productId +
                ", rating=" + rating +
                '}';
    }
}
