package bnorbert.onlineshop.controller;

import bnorbert.onlineshop.service.QuestionsAndAnswersService;
import bnorbert.onlineshop.transfer.questionsAndAnswers.AnswerDto;
import bnorbert.onlineshop.transfer.questionsAndAnswers.AnswerResponse;
import bnorbert.onlineshop.transfer.questionsAndAnswers.QuestionDto;
import bnorbert.onlineshop.transfer.questionsAndAnswers.QuestionResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

class QuestionsAndAnswersControllerTest {

    @Mock
    private QuestionsAndAnswersService mockQuestionsAndAnswersService;

    private QuestionsAndAnswersController questionsAndAnswersControllerUnderTest;

    @BeforeEach
    void setUp() {
        initMocks(this);
        questionsAndAnswersControllerUnderTest = new QuestionsAndAnswersController(mockQuestionsAndAnswersService);
    }

    @Test
    void testCreateQuestion() {
        final QuestionDto request = new QuestionDto();

        final ResponseEntity<Void> result = questionsAndAnswersControllerUnderTest.createQuestion(request);

        verify(mockQuestionsAndAnswersService).saveQuestion(any(QuestionDto.class));
    }

    @Test
    void testCreateAnswer() {
        final AnswerDto request = new AnswerDto();

        final ResponseEntity<Void> result = questionsAndAnswersControllerUnderTest.createAnswer(request);

        verify(mockQuestionsAndAnswersService).saveAnswer(any(AnswerDto.class));
    }

    @Test
    void testGetQuestionsForProduct() {

        final Optional<Integer> page = Optional.of(0);

        final Page<QuestionResponse> questionResponses = new PageImpl<>(Collections.singletonList(new QuestionResponse()));
        when(mockQuestionsAndAnswersService.getQuestionsForProduct(1L, 0)).thenReturn(questionResponses);


        final ResponseEntity<Page<QuestionResponse>> result = questionsAndAnswersControllerUnderTest.getQuestionsForProduct(1L, page);
    }

    @Test
    void testGetAnswersForQuestion() {

        final Optional<Integer> page = Optional.of(0);

        final Page<AnswerResponse> answerResponses = new PageImpl<>(Collections.singletonList(new AnswerResponse()));
        when(mockQuestionsAndAnswersService.getAnswersForQuestion(1L, 0)).thenReturn(answerResponses);


        final ResponseEntity<Page<AnswerResponse>> result = questionsAndAnswersControllerUnderTest.getAnswersForQuestion(1L, page);
    }
}
