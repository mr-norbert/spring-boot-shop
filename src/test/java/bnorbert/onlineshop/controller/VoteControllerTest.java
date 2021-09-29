package bnorbert.onlineshop.controller;

import bnorbert.onlineshop.domain.AnswersVoteType;
import bnorbert.onlineshop.domain.CommentsVoteType;
import bnorbert.onlineshop.service.VoteService;
import bnorbert.onlineshop.transfer.vote.CreateVoteRequest;
import bnorbert.onlineshop.transfer.vote.VoteAnswersRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class VoteControllerTest {

    @Mock
    private VoteService mockVoteService;

    private VoteController voteControllerUnderTest;

    @BeforeEach
    void setUp() {
        voteControllerUnderTest = new VoteController(mockVoteService);
    }

    @Test
    void testVoteComments() {
        final CreateVoteRequest request = new CreateVoteRequest(CommentsVoteType.HELPFUL, 1L);

        final ResponseEntity<Void> result = voteControllerUnderTest.voteComments(request);

        verify(mockVoteService).voteComments(any(CreateVoteRequest.class));
    }

    @Test
    void testVoteAnswers() {
        final VoteAnswersRequest request = new VoteAnswersRequest(AnswersVoteType.UPVOTE, 1L);

        final ResponseEntity<Void> result = voteControllerUnderTest.voteAnswers(request);

        verify(mockVoteService).voteAnswers(any(VoteAnswersRequest.class));
    }
}
