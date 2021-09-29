package bnorbert.onlineshop.controller;

import bnorbert.onlineshop.service.QAndAService;
import bnorbert.onlineshop.transfer.questionsAndAnswers.AnswerResponse;
import bnorbert.onlineshop.transfer.questionsAndAnswers.CreateAnswerRequest;
import bnorbert.onlineshop.transfer.questionsAndAnswers.CreateQuestionRequest;
import bnorbert.onlineshop.transfer.questionsAndAnswers.QuestionResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class QAndAControllerTest {

    @Mock
    private QAndAService mockQAndAService;

    private QAndAController qAndAControllerUnderTest;

    @BeforeEach
    void setUp() {

        qAndAControllerUnderTest = new QAndAController(mockQAndAService);
    }

    @Test
    void testCreateQuestion() {
        final CreateQuestionRequest request = new CreateQuestionRequest();

        final ResponseEntity<Void> result = qAndAControllerUnderTest.createQuestion(request);

        verify(mockQAndAService).createQuestion(any(CreateQuestionRequest.class));
    }

    @Test
    void testCreateAnswer() {
        final CreateAnswerRequest request = new CreateAnswerRequest();

        final ResponseEntity<Void> result = qAndAControllerUnderTest.createAnswer(request);

        verify(mockQAndAService).createAnswer(any(CreateAnswerRequest.class));
    }

    @Test
    void testGetQuestionsForProduct() {

        final Optional<Integer> page = Optional.of(0);

        final Page<QuestionResponse> questionResponses = new PageImpl<>(Collections.singletonList(new QuestionResponse()));
        when(mockQAndAService.getQuestionsForProduct(1L, 0)).thenReturn(questionResponses);


        final ResponseEntity<Page<QuestionResponse>> result = qAndAControllerUnderTest.getQuestionsForProduct(1L, page);
    }

    @Test
    void testGetAnswersForQuestion() {

        final Optional<Integer> page = Optional.of(0);

        final Page<AnswerResponse> answerResponses = new PageImpl<>(Collections.singletonList(new AnswerResponse()));
        when(mockQAndAService.getAnswersForQuestion(1L, 0)).thenReturn(answerResponses);


        final ResponseEntity<Page<AnswerResponse>> result = qAndAControllerUnderTest.getAnswersForQuestion(1L, page);
    }
}
