package bnorbert.onlineshop.mapper;

import bnorbert.onlineshop.domain.Answer;
import bnorbert.onlineshop.domain.Question;
import bnorbert.onlineshop.domain.User;
import bnorbert.onlineshop.transfer.questionsAndAnswers.CreateAnswerRequest;
import bnorbert.onlineshop.transfer.questionsAndAnswers.AnswerResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public abstract class AnswerMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdDate", expression = "java(java.time.Instant.now())")
    @Mapping(target = "text", source = "createAnswerRequest.text")
    @Mapping(target = "question", source = "question")
    @Mapping(target = "user", source = "user")
    public abstract Answer map(CreateAnswerRequest createAnswerRequest, Question question, User user);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "text", source = "answer.text")
    @Mapping(target = "voteCount", source = "answer.voteCount")
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "questionId", source = "question.id")
    @Mapping(target = "createdDate", source = "answer.createdDate")
    public abstract AnswerResponse mapToAnswerResponse(Answer answer);

}
