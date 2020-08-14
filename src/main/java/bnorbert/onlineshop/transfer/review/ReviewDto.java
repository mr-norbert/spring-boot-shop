package bnorbert.onlineshop.transfer.review;

import bnorbert.onlineshop.exception.ResourceNotFoundException;
import lombok.ToString;

@ToString
public class ReviewDto {

    private Long productId;
    private String intent;
    private String content;
    private int rating;

    public String getIntent() {
        return intent;
    }

    public void setIntent(String intent) {
        this.intent = intent;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getRating() {
        if (rating > 5) {
            throw new ResourceNotFoundException("Try 1.I do not recommend, " +
                    " 2.Disappointing, 3.Decent, 4.Good, 5.Very good");
        } else if (rating < 1){
            throw new ResourceNotFoundException("Try 1.I do not recommend, " +
                    " 2.Disappointing, 3.Decent, 4.Good, 5.Very good");
    } else return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

}
