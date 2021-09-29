package bnorbert.onlineshop.controller;

import bnorbert.onlineshop.service.PantryService;
import org.apache.mahout.cf.taste.common.TasteException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.sql.SQLException;

@CrossOrigin
@RestController
@RequestMapping("/pantry")
public class PantryController {

    private final PantryService service;

    public PantryController(PantryService pantryService) {
        this.service = pantryService;
    }

    @PutMapping("/findSimilarItems")
    public ResponseEntity<Void> findSimilarItems() throws SQLException, TasteException {
        service.findSimilarItems();
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/createPantry")
    public ResponseEntity<Void> createPantry() throws SQLException, TasteException, IOException {
        service.createPantry();
        return new ResponseEntity<>(HttpStatus.OK);
    }


}
