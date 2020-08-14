package bnorbert.onlineshop.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Base64;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class VerificationToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String verificationToken;
    private LocalDateTime createdDate;
    private LocalDateTime expirationDate;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    public VerificationToken(User user) {
        this.user = user;
        verificationToken = Base64.getUrlEncoder().withoutPadding().toString()
                .replace("java.util.Base64$Encoder@", "");
        createdDate = LocalDateTime.now();
        expirationDate = LocalDateTime.now().plusMinutes(5);
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
