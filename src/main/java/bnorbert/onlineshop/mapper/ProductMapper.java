package bnorbert.onlineshop.mapper;

import bnorbert.onlineshop.domain.Category;
import bnorbert.onlineshop.domain.Product;
import bnorbert.onlineshop.transfer.product.ProductDto;
import bnorbert.onlineshop.transfer.product.ProductResponse;
import bnorbert.onlineshop.transfer.product.UpdateResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public abstract class ProductMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "quantity", source = "productDto.quantity")
    @Mapping(target = "name", source = "productDto.name")
    @Mapping(target = "price", source = "productDto.price")
    @Mapping(target = "description", source = "productDto.description")
    @Mapping(target = "imagePath", source = "productDto.imagePath")
    @Mapping(target = "unitInStock", source = "productDto.unitInStock")
    @Mapping(target = "createdDate", expression = "java(java.time.Instant.now())")
    @Mapping(target = "category", source = "category")
    public abstract Product map(ProductDto productDto, Category category);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "quantity", source = "product.quantity")
    @Mapping(target = "name", source = "product.name")
    @Mapping(target = "price", source = "product.price")
    @Mapping(target = "description", source = "product.description")
    @Mapping(target = "imagePath", source = "product.imagePath")
    @Mapping(target = "unitInStock", source = "product.unitInStock")
    @Mapping(target = "createdDate", source = "product.createdDate")
    @Mapping(target = "categoryId", source = "category.id")
    public abstract ProductResponse mapToDto(Product product);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "quantity", source = "product.quantity")
    @Mapping(target = "name", source = "product.name")
    @Mapping(target = "price", source = "product.price")
    @Mapping(target = "description", source = "product.description")
    @Mapping(target = "imagePath", source = "product.imagePath")
    @Mapping(target = "unitInStock", source = "product.unitInStock")
    @Mapping(target = "createdDate", source = "product.createdDate")
    @Mapping(target = "categoryId", source = "category.id")
    @Mapping(target = "lastModifiedBy", source = "product.lastModifiedBy")
    @Mapping(target = "lastModifiedDate", source = "product.lastModifiedDate")
    public abstract UpdateResponse mapToDto2(Product product);

    public abstract List<ProductResponse> entitiesToEntityDTOs(List<Product> products);
}
