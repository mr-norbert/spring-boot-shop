package bnorbert.onlineshop.service;

import bnorbert.onlineshop.domain.*;
import bnorbert.onlineshop.mapper.VoteMapper;
import bnorbert.onlineshop.repository.AnswerRepository;
import bnorbert.onlineshop.repository.CommentRepository;
import bnorbert.onlineshop.repository.VoteRepository;
import bnorbert.onlineshop.transfer.vote.CreateVoteRequest;
import bnorbert.onlineshop.transfer.vote.VoteAnswersRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VoteServiceTest {

    @Mock
    private VoteRepository mockVoteRepository;
    @Mock
    private AnswerRepository mockAnswerRepository;
    @Mock
    private CommentRepository mockCommentRepository;
    @Mock
    private UserService mockAuthService;
    @Mock
    private VoteMapper mockVoteMapper;

    private VoteService voteServiceUnderTest;

    @BeforeEach
    void setUp() {
        voteServiceUnderTest = new VoteService(mockVoteRepository, mockAnswerRepository, mockCommentRepository, mockAuthService, mockVoteMapper);
    }

    @Test
    void testVoteComments() {
        CreateVoteRequest request = new CreateVoteRequest();

        request.setCommentsVoteType(CommentsVoteType.HELPFUL);
        request.setCommentId(1L);

        Comment comment = new Comment();
        comment.setId(1L);

        User user = new User();
        user.setId(1L);
        user.setEmail("email@gmail.com");

        when(mockCommentRepository.findById(comment.getId())).thenReturn(Optional.of(comment));


        //Vote vote = new Vote();
        //when(mockVoteRepository.findTopByCommentAndUserOrderByIdDesc(any(Comment.class), any(User.class))).thenReturn(Optional.of(vote));

        when(mockAuthService.getCurrentUser()).thenReturn(user);


        when(mockVoteMapper.map(any(CreateVoteRequest.class), any(Comment.class), any(User.class))).thenReturn(new Vote());


        when(mockVoteRepository.save(any(Vote.class))).thenReturn(new Vote());

        when(mockCommentRepository.save(any(Comment.class))).thenReturn(comment);


        voteServiceUnderTest.voteComments(request);
    }


    @Test
    void testVoteAnswers() {

       VoteAnswersRequest request = new VoteAnswersRequest();
       request.setAnswersVoteType(AnswersVoteType.UPVOTE);
       request.setAnswerId(1L);

       Answer answer = new Answer();
       answer.setId(1L);

       User user = new User();
       user.setId(1L);
       user.setEmail("email@gmail.com");

       when(mockAnswerRepository.findById(answer.getId())).thenReturn(Optional.of(answer));

       //Vote vote = new Vote();

       //when(mockVoteRepository.findTopByAnswerAndUserOrderByIdDesc(eq(answer), eq(user))).thenReturn(Optional.of(new Vote()));

       when(mockAuthService.getCurrentUser()).thenReturn(user);

       when(mockVoteMapper.map2(any(VoteAnswersRequest.class), any(Answer.class), any(User.class))).thenReturn(new Vote());

       when(mockVoteRepository.save(any(Vote.class))).thenReturn(new Vote());

       when(mockAnswerRepository.save(any(Answer.class))).thenReturn(answer);

       voteServiceUnderTest.voteAnswers(request);
    }


}
