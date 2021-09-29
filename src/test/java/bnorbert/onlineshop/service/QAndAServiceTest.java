package bnorbert.onlineshop.service;

import bnorbert.onlineshop.domain.Answer;
import bnorbert.onlineshop.domain.Product;
import bnorbert.onlineshop.domain.Question;
import bnorbert.onlineshop.domain.User;
import bnorbert.onlineshop.mapper.AnswerMapper;
import bnorbert.onlineshop.mapper.QuestionMapper;
import bnorbert.onlineshop.repository.AnswerRepository;
import bnorbert.onlineshop.repository.QuestionsRepository;
import bnorbert.onlineshop.transfer.questionsAndAnswers.CreateAnswerRequest;
import bnorbert.onlineshop.transfer.questionsAndAnswers.AnswerResponse;
import bnorbert.onlineshop.transfer.questionsAndAnswers.CreateQuestionRequest;
import bnorbert.onlineshop.transfer.questionsAndAnswers.QuestionResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.Instant;
import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class QAndAServiceTest {

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

    private QAndAService qAndAServiceUnderTest;

    @BeforeEach
    void setUp() {
        qAndAServiceUnderTest = new QAndAService(mockProductService, mockQuestionsRepository, mockAnswerRepository, mockAuthService, mockQuestionMapper, mockAnswerMapper);
    }

    @Test
    void testCreateQuestion() {
        CreateQuestionRequest request = new CreateQuestionRequest();
        request.setProductId(1L);
        request.setText("text");

        User user = new User();
        user.setId(1L);
        user.setEmail("email@gmail.com");

        when(mockAuthService.isLoggedIn()).thenReturn(true);

        Product product = new Product();
        product.setId(1L);
        product.setName("name");
        product.setPrice(5.0);
        product.setDescription("description");
        product.setImagePath("imagePath");
        product.setUnitInStock(90);
        product.setCreatedDate(Instant.now());
        product.setCreatedBy("createdBy");
        product.setLastModifiedBy("lastModifiedBy");
        when(mockProductService.getProduct(1L)).thenReturn(product);

        when(mockQuestionsRepository.save(any(Question.class))).thenReturn(new Question());

        when(mockQuestionMapper.map(any(CreateQuestionRequest.class), any(Product.class), any(User.class))).thenReturn(new Question());

        when(mockAuthService.getCurrentUser()).thenReturn(user);

        qAndAServiceUnderTest.createQuestion(request);
    }

    @Test
    void testCreateAnswer() {
        CreateAnswerRequest request = new CreateAnswerRequest();
        request.setQuestionId(1L);
        request.setText("text");
        User user = new User();
        user.setId(1L);
        user.setEmail("test@gmail..com");

        Question question = new Question();
        question.setId(1L);
        when(mockQuestionsRepository.findById(1L)).thenReturn(Optional.of(question));

        when(mockAnswerMapper.map(any(CreateAnswerRequest.class), any(Question.class), any(User.class))).thenReturn(new Answer());

        when(mockAuthService.getCurrentUser()).thenReturn(user);

        when(mockAnswerRepository.save(any(Answer.class))).thenReturn(new Answer());

        qAndAServiceUnderTest.createAnswer(request);
    }



    @Test
    void testGetQuestionsForProduct() {
        Product product = new Product();
        product.setId(1L);
        product.setName("name");
        product.setPrice(5.0);
        product.setDescription("description");
        product.setImagePath("imagePath");
        product.setUnitInStock(90);
        product.setCreatedDate(Instant.now());
        product.setCreatedBy("createdBy");
        product.setLastModifiedBy("lastModifiedBy");
        when(mockProductService.getProduct(1L)).thenReturn(product);

        Question question = new Question();
        question.setId(1L);
        Page<Question> questions = new PageImpl<>(Collections.singletonList(question));
        when(mockQuestionsRepository.findByProduct(any(Product.class), eq(PageRequest.of(0, 8)))).thenReturn(questions);

        when(mockQuestionMapper.mapToQuestionResponse(any(Question.class))).thenReturn(new QuestionResponse());


        final Page<QuestionResponse> result = qAndAServiceUnderTest.getQuestionsForProduct(1L, 0);
    }




    @Test
    void testGetAnswersForQuestion() {

        Question question = new Question();
        question.setId(1L);
        when(mockQuestionsRepository.findById(1L)).thenReturn(Optional.of(question));

        Answer answer = new Answer();
        answer.setId(1L);
        Page<Answer> answers = new PageImpl<>(Collections.singletonList(answer));
        when(mockAnswerRepository.findByQuestion(any(Question.class), eq(PageRequest.of(0, 8)))).thenReturn(answers);

        when(mockAnswerMapper.mapToAnswerResponse(any(Answer.class))).thenReturn(new AnswerResponse());

        final Page<AnswerResponse> result = qAndAServiceUnderTest.getAnswersForQuestion(1L, 0);
    }





}
