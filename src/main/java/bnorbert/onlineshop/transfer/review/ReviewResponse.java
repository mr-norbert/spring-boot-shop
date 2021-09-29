package bnorbert.onlineshop.transfer.review;

import java.time.Instant;


public class ReviewResponse {
    private Long id;
    private String email;
    private String intent;
    private String content;
    private int rating;
    private Long userId;
    private Instant createdDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

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
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Instant getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Instant createdDate) {
        this.createdDate = createdDate;
    }

    @Override
    public String toString() {
        return "ReviewResponse{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", intent='" + intent + '\'' +
                ", content='" + content + '\'' +
                ", rating=" + rating +
                ", userId=" + userId +
                ", createdDate=" + createdDate +
                '}';
    }
}
