package bnorbert.onlineshop.controller;

import bnorbert.onlineshop.service.QuestionsAndAnswersService;
import bnorbert.onlineshop.transfer.questionsAndAnswers.AnswerDto;
import bnorbert.onlineshop.transfer.questionsAndAnswers.QuestionDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}
