package bnorbert.onlineshop.controller;

import bnorbert.onlineshop.service.QuestionsAndAnswersService;
import bnorbert.onlineshop.transfer.questionsAndAnswers.AnswerDto;
import bnorbert.onlineshop.transfer.questionsAndAnswers.AnswerResponse;
import bnorbert.onlineshop.transfer.questionsAndAnswers.QuestionDto;
import bnorbert.onlineshop.transfer.questionsAndAnswers.QuestionResponse;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

import static org.springframework.http.HttpStatus.CREATED;

@CrossOrigin
@RestController
@RequestMapping("/q&a")

public class QuestionsAndAnswersController {

    private final QuestionsAndAnswersService questionsAndAnswersService;

    public QuestionsAndAnswersController(QuestionsAndAnswersService questionsAndAnswersService) {
        this.questionsAndAnswersService = questionsAndAnswersService;
    }


    @PostMapping
    public ResponseEntity<Void> createQuestion(@RequestBody QuestionDto request) {
        questionsAndAnswersService.saveQuestion(request);
        return new ResponseEntity<>(CREATED);
    }

    @PostMapping("/answers")
    public ResponseEntity<Void> createAnswer(@RequestBody AnswerDto request) {
        questionsAndAnswersService.saveAnswer(request);
        return new ResponseEntity<>(CREATED);
    }

    @GetMapping("/getQForProducts/{product_id}")
    public ResponseEntity<Page<QuestionResponse>> getQuestionsForProduct
            (@PathVariable("product_id") Long product_id,
             @RequestParam Optional<Integer> page) {
        return new ResponseEntity<>(questionsAndAnswersService.getQuestionsForProduct(product_id, page.orElse(0)), HttpStatus.OK);
    }

    @GetMapping("/getAnswersForQ/{question_id}")
    public ResponseEntity<Page<AnswerResponse>> getAnswersForQuestion
            (@PathVariable("question_id") Long question_id,
             @RequestParam Optional<Integer> page) {
        return new ResponseEntity<>(questionsAndAnswersService.getAnswersForQuestion(question_id, page.orElse(0)), HttpStatus.OK);
    }
}
