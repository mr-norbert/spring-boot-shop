package bnorbert.onlineshop.mapper;

import bnorbert.onlineshop.domain.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public abstract class ViewMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdDate", expression = "java(java.time.Instant.now())")
    @Mapping(target = "product", source = "product")
    @Mapping(target = "user", source = "user")
    public abstract View map(Product product, User user);
}
