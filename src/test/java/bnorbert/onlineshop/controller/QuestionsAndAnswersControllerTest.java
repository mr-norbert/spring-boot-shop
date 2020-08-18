package bnorbert.onlineshop.controller;

import bnorbert.onlineshop.service.QuestionsAndAnswersService;
import bnorbert.onlineshop.transfer.questionsAndAnswers.AnswerDto;
import bnorbert.onlineshop.transfer.questionsAndAnswers.QuestionDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.http.ResponseEntity;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
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
}
