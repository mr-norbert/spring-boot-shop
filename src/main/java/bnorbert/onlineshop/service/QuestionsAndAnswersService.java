package bnorbert.onlineshop.service;

import bnorbert.onlineshop.domain.*;
import bnorbert.onlineshop.exception.ResourceNotFoundException;
import bnorbert.onlineshop.mapper.AnswerMapper;
import bnorbert.onlineshop.mapper.QuestionMapper;
import bnorbert.onlineshop.repository.AnswerRepository;
import bnorbert.onlineshop.repository.QuestionsRepository;
import bnorbert.onlineshop.transfer.questionsAndAnswers.AnswerDto;
import bnorbert.onlineshop.transfer.questionsAndAnswers.QuestionDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class QuestionsAndAnswersService {

    private final ProductService productService;
    private final QuestionsRepository questionsRepository;
    private final AnswerRepository answerRepository;
    private final UserService authService;
    private final QuestionMapper questionMapper;
    private final AnswerMapper answerMapper;

    public QuestionsAndAnswersService(ProductService productService, QuestionsRepository questionsRepository,
                                      AnswerRepository answerRepository, UserService authService, QuestionMapper questionMapper,
                                      AnswerMapper answerMapper) {
        this.productService = productService;
        this.questionsRepository = questionsRepository;
        this.answerRepository = answerRepository;
        this.authService = authService;
        this.questionMapper = questionMapper;
        this.answerMapper = answerMapper;
    }

    @Transactional
    public void saveQuestion(QuestionDto request) {
        log.info("Creating question: {}", request);
        if(authService.isLoggedIn()) {
            Product product = productService.getProduct(request.getProductId());

            questionsRepository.save(questionMapper.map(request, product, authService.getCurrentUser()));
        }
    }

    @Transactional
    public void saveAnswer(AnswerDto request) {
        log.info("Creating answer: {}", request);
        Question question = questionsRepository.findById(request.getQuestionId())
                .orElseThrow(() -> new ResourceNotFoundException("Question " + request.getQuestionId() + "id not found"));
        Answer answer = answerMapper.map(request, question, authService.getCurrentUser());
        answerRepository.save(answer);

    }



}
