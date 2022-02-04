package bnorbert.onlineshop.service;

import bnorbert.onlineshop.domain.TestAES;
import bnorbert.onlineshop.exception.ResourceNotFoundException;
import bnorbert.onlineshop.repository.TestAESRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

@Service
@AllArgsConstructor
public class TestAESService {

    private final TestAESRepository testAESRepository;

    public void createText() throws BadPaddingException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, IllegalBlockSizeException, NoSuchPaddingException, InvalidKeyException, InvalidKeySpecException {
        TestAES testAES = new TestAES();
        testAES.setText("Hello!");
        testAESRepository.save(testAES);

        TestAES testAES2 = new TestAES();
        testAES2.setText("871f-4606-9efc-8dbc-8f54-3a06");
        testAESRepository.save(testAES2);
    }

    public TestAES getText(long id) {
        return testAESRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(id + " not found."));
    }


}
