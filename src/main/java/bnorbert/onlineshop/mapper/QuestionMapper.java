package bnorbert.onlineshop.mapper;

import bnorbert.onlineshop.domain.Product;
import bnorbert.onlineshop.domain.Question;
import bnorbert.onlineshop.domain.User;
import bnorbert.onlineshop.transfer.questionsAndAnswers.QuestionDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public abstract class QuestionMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdDate", expression = "java(java.time.Instant.now())")
    @Mapping(target = "text", source = "questionDto.text")
    @Mapping(target = "product", source = "product")
    @Mapping(target = "user", source = "user")
    public abstract Question map(QuestionDto questionDto, Product product, User user);

}
