package bnorbert.onlineshop.controller;

import bnorbert.onlineshop.service.VoteService;
import bnorbert.onlineshop.transfer.vote.VoteAnswersDto;
import bnorbert.onlineshop.transfer.vote.VoteDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/votes")
@CrossOrigin

public class VoteController {

    private final VoteService voteService;

    public VoteController(VoteService voteService) {
        this.voteService = voteService;
    }

    @PostMapping
    public ResponseEntity<Void> voteComments(@RequestBody VoteDto request) {
        voteService.voteComments(request);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/answers")
    public ResponseEntity<Void> voteAnswers(@RequestBody VoteAnswersDto request) {
        voteService.voteAnswers(request);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}