package bnorbert.onlineshop.mapper;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public abstract class CategoryMapper {
/*
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "name", source = "categoryDto.name")
    public abstract Category map(CategoryDto categoryDto);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "category.name")
    public abstract CategoryResponse mapToDto2(Category category);

    public abstract List<CategoryResponse> entitiesToEntityDTOs(List<Category> categories);

 */
}
