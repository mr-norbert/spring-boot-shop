package bnorbert.onlineshop.controller;

import bnorbert.onlineshop.domain.TestAES;
import bnorbert.onlineshop.service.TestAESService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

@CrossOrigin
@RestController
@RequestMapping("/aes")
@AllArgsConstructor
public class TestAESController {

    private final TestAESService testAESService;

    @PostMapping
    public ResponseEntity<Void> createText() throws BadPaddingException, InvalidKeyException, NoSuchAlgorithmException, IllegalBlockSizeException, NoSuchPaddingException, InvalidAlgorithmParameterException, InvalidKeySpecException {
        testAESService.createText();
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("/decryption/{id}")
    public TestAES get(@PathVariable long id) {
        return testAESService.getText(id);
    }


}
