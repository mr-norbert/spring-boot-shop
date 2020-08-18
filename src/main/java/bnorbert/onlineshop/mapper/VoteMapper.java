package bnorbert.onlineshop.mapper;

import bnorbert.onlineshop.domain.Answer;
import bnorbert.onlineshop.domain.Comment;
import bnorbert.onlineshop.domain.User;
import bnorbert.onlineshop.domain.Vote;
import bnorbert.onlineshop.transfer.vote.VoteAnswersDto;
import bnorbert.onlineshop.transfer.vote.VoteDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface VoteMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "commentsVoteType", source = "voteDto.commentsVoteType")
    @Mapping(target = "comment", source = "comment")
    @Mapping(target = "user", source = "user")
    Vote map(VoteDto voteDto, Comment comment, User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "answersVoteType", source = "voteAnswersDto.answersVoteType")
    @Mapping(target = "answer", source = "answer")
    @Mapping(target = "user", source = "user")
    Vote map2(VoteAnswersDto voteAnswersDto, Answer answer, User user);
}
