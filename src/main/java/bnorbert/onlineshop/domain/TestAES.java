package bnorbert.onlineshop.domain;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
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
        String password = "$passwordf-L4606-9efEsXc-8dbvoc8f543O";
        String salt = "12345678";
        byte [] counter = new byte[16];
        IvParameterSpec iv;
        iv = new IvParameterSpec(counter);
        SecretKey key = AES.getKeyFromPassword(password, salt);

        return AES.decryptPasswordBased(text, key, iv);
    }

    public void setText(String text) throws InvalidKeySpecException, NoSuchAlgorithmException, IllegalBlockSizeException, InvalidKeyException, BadPaddingException, InvalidAlgorithmParameterException, NoSuchPaddingException {
        String password = "$passwordf-L4606-9efEsXc-8dbvoc8f543O";
        String salt = "12345678";
        //IvParameterSpec ivParameterSpec = AES.generateIv();
        IvParameterSpec iv;
        SecretKey key = AES.getKeyFromPassword(password, salt);

        //String encodedIv = Base64.getEncoder().encodeToString(ivParameterSpec.getIV());

        //String encodedKey = Base64.getEncoder().encodeToString(key.getEncoded());
        //byte[] decodedKey = Base64.getDecoder().decode(encodedKey);
        //SecretKey replica = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");

        byte [] counter = new byte[16];
        iv = new IvParameterSpec(counter);

        this.text = AES.encryptPasswordBased(text, key, iv);
    }
}
