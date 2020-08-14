package bnorbert.onlineshop.mapper;

import bnorbert.onlineshop.domain.Category;
import bnorbert.onlineshop.transfer.category.CategoryDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "name", source = "categoryDto.name")
    Category map(CategoryDto categoryDto);
}
