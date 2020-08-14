package bnorbert.onlineshop.domain;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Base64;

@Entity
public class VerificationToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String verificationToken;
    private LocalDateTime createdDate;
    private LocalDateTime expirationDate;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    public VerificationToken() {
    }

    public VerificationToken(User user) {
        this.user = user;
        verificationToken = Base64.getUrlEncoder().withoutPadding().toString()
                .replace("java.util.Base64$Encoder@", "");
        createdDate = LocalDateTime.now();
        expirationDate = LocalDateTime.now().plusMinutes(5);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getVerificationToken() {
        return verificationToken;
    }

    public void setVerificationToken(String verificationToken) {
        this.verificationToken = verificationToken;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public LocalDateTime getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(LocalDateTime expirationDate) {
        this.expirationDate = expirationDate;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VerificationToken that = (VerificationToken) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }
}
