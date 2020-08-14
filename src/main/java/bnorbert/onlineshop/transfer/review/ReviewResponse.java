package bnorbert.onlineshop.transfer.review;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.Instant;

@Getter
@Setter
@ToString
public class ReviewResponse {
    private Long id;
    private String email;
    private String intent;
    private String content;
    private int rating;
    private Long userId;
    private Instant createdDate;

}
