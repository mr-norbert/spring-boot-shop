package bnorbert.onlineshop.service;

import bnorbert.onlineshop.domain.Answer;
import bnorbert.onlineshop.domain.Product;
import bnorbert.onlineshop.domain.Question;
import bnorbert.onlineshop.domain.User;
import bnorbert.onlineshop.mapper.AnswerMapper;
import bnorbert.onlineshop.mapper.QuestionMapper;
import bnorbert.onlineshop.repository.AnswerRepository;
import bnorbert.onlineshop.repository.QuestionsRepository;
import bnorbert.onlineshop.transfer.questionsAndAnswers.AnswerDto;
import bnorbert.onlineshop.transfer.questionsAndAnswers.AnswerResponse;
import bnorbert.onlineshop.transfer.questionsAndAnswers.QuestionDto;
import bnorbert.onlineshop.transfer.questionsAndAnswers.QuestionResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

class QuestionsAndAnswersServiceTest {

    @Mock
    private ProductService mockProductService;
    @Mock
    private QuestionsRepository mockQuestionsRepository;
    @Mock
    private AnswerRepository mockAnswerRepository;
    @Mock
    private UserService mockAuthService;
    @Mock
    private QuestionMapper mockQuestionMapper;
    @Mock
    private AnswerMapper mockAnswerMapper;

    private QuestionsAndAnswersService questionsAndAnswersServiceUnderTest;

    @BeforeEach
    void setUp() {
        initMocks(this);
        questionsAndAnswersServiceUnderTest = new QuestionsAndAnswersService(mockProductService, mockQuestionsRepository, mockAnswerRepository, mockAuthService, mockQuestionMapper, mockAnswerMapper);
    }

    @Test
    void testSaveQuestion() {

        final QuestionDto request = new QuestionDto();
        request.setProductId(1L);
        request.setText("text");

        when(mockAuthService.isLoggedIn()).thenReturn(true);
        when(mockProductService.getProduct(1L)).thenReturn(new Product());
        when(mockQuestionsRepository.save(any(Question.class))).thenReturn(new Question());
        when(mockQuestionMapper.map(any(QuestionDto.class), any(Product.class), eq(new User()))).thenReturn(new Question());
        when(mockAuthService.getCurrentUser()).thenReturn(new User());

        questionsAndAnswersServiceUnderTest.saveQuestion(request);
    }

    @Test
    void testSaveAnswer() {
        final AnswerDto request = new AnswerDto();
        request.setQuestionId(1L);
        request.setText("text");

        when(mockQuestionsRepository.findById(1L)).thenReturn(Optional.of(new Question()));
        when(mockAnswerMapper.map(any(AnswerDto.class), any(Question.class), eq(new User()))).thenReturn(new Answer());
        when(mockAuthService.getCurrentUser()).thenReturn(new User());
        when(mockAnswerRepository.save(any(Answer.class))).thenReturn(new Answer());


        questionsAndAnswersServiceUnderTest.saveAnswer(request);
    }

    @Test
    void testGetQuestionsForProduct() {

        when(mockProductService.getProduct(1L)).thenReturn(new Product());

        final Page<Question> questions = new PageImpl<>(Collections.singletonList(new Question()));
        when(mockQuestionsRepository.findByProduct(any(Product.class), eq(PageRequest.of(0, 8)))).thenReturn(questions);

        when(mockQuestionMapper.mapToDto(any(Question.class))).thenReturn(new QuestionResponse());


        final Page<QuestionResponse> result = questionsAndAnswersServiceUnderTest.getQuestionsForProduct(1L, 0);
    }

    @Test
    void testGetAnswersForQuestion() {

        when(mockQuestionsRepository.findById(1L)).thenReturn(Optional.of(new Question()));

        final Page<Answer> answers = new PageImpl<>(Collections.singletonList(new Answer()));
        when(mockAnswerRepository.findByQuestion(any(Question.class), eq(PageRequest.of(0, 8)))).thenReturn(answers);

        when(mockAnswerMapper.mapToDto(any(Answer.class))).thenReturn(new AnswerResponse());

        final Page<AnswerResponse> result = questionsAndAnswersServiceUnderTest.getAnswersForQuestion(1L, 0);
    }
}
