package bnorbert.onlineshop.domain;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

@Entity
public class TestAES {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String text;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getText() throws InvalidKeySpecException, NoSuchAlgorithmException, IllegalBlockSizeException, InvalidKeyException, BadPaddingException, InvalidAlgorithmParameterException, NoSuchPaddingException {
        String password = "$L4606-9efEsXc-8dbvoc8f543OAdsD#@!ldsaj.AS9@200";
        String salt = "12337281";
        SecretKey key = AES.getKeyFromPassword(password, salt);

        return AES.decryptPasswordBased(text, key);
    }

    public void setText(String text) throws InvalidKeySpecException, NoSuchAlgorithmException, IllegalBlockSizeException, InvalidKeyException, BadPaddingException, InvalidAlgorithmParameterException, NoSuchPaddingException {
        String password = "$L4606-9efEsXc-8dbvoc8f543OAdsD#@!ldsaj.AS9@200";
        String salt = "12337281";
        SecretKey key = AES.getKeyFromPassword(password, salt);

        this.text = AES.encryptPasswordBased(text, key);
    }
}
