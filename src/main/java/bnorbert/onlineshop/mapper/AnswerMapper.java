package bnorbert.onlineshop.mapper;

import bnorbert.onlineshop.domain.Answer;
import bnorbert.onlineshop.domain.Question;
import bnorbert.onlineshop.domain.User;
import bnorbert.onlineshop.transfer.questionsAndAnswers.AnswerDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public abstract class AnswerMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdDate", expression = "java(java.time.Instant.now())")
    @Mapping(target = "text", source = "answerDto.text")
    @Mapping(target = "question", source = "question")
    @Mapping(target = "user", source = "user")
    public abstract Answer map(AnswerDto answerDto, Question question, User user);
}
