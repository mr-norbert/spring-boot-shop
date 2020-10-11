package bnorbert.onlineshop.mapper;

import bnorbert.onlineshop.domain.Category;
import bnorbert.onlineshop.domain.Product;
import bnorbert.onlineshop.transfer.category.CategoryDto;
import bnorbert.onlineshop.transfer.category.CategoryResponse;
import bnorbert.onlineshop.transfer.product.ProductResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public abstract class CategoryMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "name", source = "categoryDto.name")
    public abstract Category map(CategoryDto categoryDto);

    //optional
    /*
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "product.name")
    @Mapping(target = "price", source = "product.price")
    @Mapping(target = "description", source = "product.description")
    @Mapping(target = "imagePath", source = "product.imagePath")
    @Mapping(target = "unitInStock", source = "product.unitInStock")
    @Mapping(target = "createdDate", source = "product.createdDate")
    @Mapping(target = "categoryName", source = "category.name")
    @Mapping(target = "categoryId", source = "category.id")
    public abstract ProductResponse mapToDto(Product product);
     */

    public abstract List<CategoryResponse> entitiesToEntityDTOs(List<Category> categories);
}
