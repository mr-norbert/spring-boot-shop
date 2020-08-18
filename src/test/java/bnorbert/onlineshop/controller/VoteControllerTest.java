package bnorbert.onlineshop.controller;

import bnorbert.onlineshop.domain.AnswersVoteType;
import bnorbert.onlineshop.domain.CommentsVoteType;
import bnorbert.onlineshop.service.VoteService;
import bnorbert.onlineshop.transfer.vote.VoteAnswersDto;
import bnorbert.onlineshop.transfer.vote.VoteDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.http.ResponseEntity;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

class VoteControllerTest {

    @Mock
    private VoteService mockVoteService;

    private VoteController voteControllerUnderTest;

    @BeforeEach
    void setUp() {
        initMocks(this);
        voteControllerUnderTest = new VoteController(mockVoteService);
    }

    @Test
    void testVoteComments() {
        final VoteDto request = new VoteDto(CommentsVoteType.HELPFUL, 1L);

        final ResponseEntity<Void> result = voteControllerUnderTest.voteComments(request);

        verify(mockVoteService).voteComments(any(VoteDto.class));
    }

    @Test
    void testVoteAnswers() {
        final VoteAnswersDto request = new VoteAnswersDto(AnswersVoteType.UPVOTE, 1L);

        final ResponseEntity<Void> result = voteControllerUnderTest.voteAnswers(request);

        verify(mockVoteService).voteAnswers(any(VoteAnswersDto.class));
    }
}
