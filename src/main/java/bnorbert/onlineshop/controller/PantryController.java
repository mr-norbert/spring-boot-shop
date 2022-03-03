package bnorbert.onlineshop.controller;

import bnorbert.onlineshop.service.PantryService;
import org.apache.mahout.cf.taste.common.TasteException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;

@CrossOrigin
@RestController
@RequestMapping("/pantry")
public class PantryController {

    private final PantryService service;

    public PantryController(PantryService pantryService) {
        this.service = pantryService;
    }

    @PostMapping("/developers/creators")
    public ResponseEntity<Void> generateIds() {
        service.generateIds();
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @DeleteMapping("/developers/removal")
    public ResponseEntity<Void> wipe() {
        service.deleteAll();
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/developers/partition")
    public ResponseEntity<Void> findSimilarProducts() throws SQLException, TasteException {
        service.findSimilarProducts();
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
