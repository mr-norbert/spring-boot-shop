package bnorbert.onlineshop.service;

import bnorbert.onlineshop.domain.*;
import bnorbert.onlineshop.exception.ResourceNotFoundException;
import bnorbert.onlineshop.mapper.AnswerMapper;
import bnorbert.onlineshop.mapper.QuestionMapper;
import bnorbert.onlineshop.repository.AnswerRepository;
import bnorbert.onlineshop.repository.QuestionsRepository;
import bnorbert.onlineshop.transfer.questionsAndAnswers.CreateAnswerRequest;
import bnorbert.onlineshop.transfer.questionsAndAnswers.AnswerResponse;
import bnorbert.onlineshop.transfer.questionsAndAnswers.CreateQuestionRequest;
import bnorbert.onlineshop.transfer.questionsAndAnswers.QuestionResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class QAndAService {

    private final ProductService productService;
    private final QuestionsRepository questionsRepository;
    private final AnswerRepository answerRepository;
    private final UserService userService;
    private final QuestionMapper questionMapper;
    private final AnswerMapper answerMapper;

    public QAndAService(ProductService productService, QuestionsRepository questionsRepository,
                        AnswerRepository answerRepository, UserService userService, QuestionMapper questionMapper,
                        AnswerMapper answerMapper) {
        this.productService = productService;
        this.questionsRepository = questionsRepository;
        this.answerRepository = answerRepository;
        this.userService = userService;
        this.questionMapper = questionMapper;
        this.answerMapper = answerMapper;
    }

    @Transactional
    public void createQuestion(CreateQuestionRequest request) {
        log.info("Creating question: {}", request);
        if(userService.isLoggedIn()) {
            Product product = productService.getProduct(request.getProductId());

            questionsRepository.save
                    (questionMapper.map(request, product, userService.getCurrentUser()));
        }
    }

    @Transactional
    public void createAnswer(CreateAnswerRequest request) {
        log.info("Creating answer: {}", request);
        Question question = questionsRepository.findById(request.getQuestionId())
                .orElseThrow(() -> new ResourceNotFoundException
                        ("Question " + request.getQuestionId() + "id not found"));

        Answer answer = answerMapper.map(request, question, userService.getCurrentUser());
        answerRepository.save(answer);

    }

    @Transactional
    public Page<QuestionResponse> getQuestionsForProduct(Long product_id, Integer page) {
        log.info("Retrieving questions");
        Product product = productService.getProduct(product_id);

        return questionsRepository.
                findByProduct(product, PageRequest.of(page, 8))
                .map(questionMapper::mapToQuestionResponse);
    }


    @Transactional
    public Page<AnswerResponse> getAnswersForQuestion(Long question_id, Integer page) {
        log.info("Retrieving answers");
        Question question = questionsRepository.findById(question_id)
                .orElseThrow(() -> new ResourceNotFoundException
                        ("Question with id: " + question_id + "not found"));
        return answerRepository
                .findByQuestion(question, PageRequest.of(page, 8))
                .map(answerMapper::mapToAnswerResponse);
    }



}
