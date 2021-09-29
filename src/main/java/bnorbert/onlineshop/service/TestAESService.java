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
        testAES.setText("_text");
        testAESRepository.save(testAES);

        TestAES _testAES = new TestAES();
        _testAES.setText("871f-4606-9efc-8dbc8f543a06");
        testAESRepository.save(_testAES);
    }

    public TestAES get(long id) {
        return testAESRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException(id + " not found."));
    }


}