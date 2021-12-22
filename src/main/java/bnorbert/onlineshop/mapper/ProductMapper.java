package bnorbert.onlineshop.mapper;

import bnorbert.onlineshop.domain.Product;
import bnorbert.onlineshop.transfer.product.CreateProductRequest;
import bnorbert.onlineshop.transfer.product.ProductResponse;
import bnorbert.onlineshop.transfer.product.UpdateResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring")
public abstract class ProductMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "name", source = "createProductRequest.name")
    @Mapping(target = "price", source = "createProductRequest.price")
    @Mapping(target = "description", source = "createProductRequest.description")
    @Mapping(target = "imagePath", source = "createProductRequest.imagePath")
    @Mapping(target = "unitInStock", source = "createProductRequest.unitInStock")
    @Mapping(target = "isAvailable", source = "createProductRequest.isAvailable")
    @Mapping(target = "createdDate", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "hits", source = "createProductRequest.hits", defaultValue = "0")
    @Mapping(target = "color", source = "createProductRequest.color")
    @Mapping(target = "categoryName", source = "createProductRequest.categoryName")
    @Mapping(target = "brandName", source = "createProductRequest.brandName")
    @Mapping(target = "specification", source = "createProductRequest.specification")
    @Mapping(target = "secondSpec", source = "createProductRequest.secondSpec")
    public abstract Product map(CreateProductRequest createProductRequest);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "product.name")
    @Mapping(target = "price", source = "product.price")
    @Mapping(target = "description", source = "product.description")
    @Mapping(target = "imagePath", source = "product.imagePath")
    @Mapping(target = "unitInStock", source = "product.unitInStock")
    @Mapping(target = "createdDate", source = "product.createdDate")
    @Mapping(target = "categoryName", source = "product.categoryName")
    @Mapping(target = "brandName", source = "product.brandName")
    public abstract ProductResponse mapToProductResponse(Product product);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "product.name")
    @Mapping(target = "price", source = "product.price")
    @Mapping(target = "description", source = "product.description")
    @Mapping(target = "imagePath", source = "product.imagePath")
    @Mapping(target = "unitInStock", source = "product.unitInStock")
    @Mapping(target = "createdDate", source = "product.createdDate")
    @Mapping(target = "lastModifiedBy", source = "product.lastModifiedBy")
    @Mapping(target = "lastModifiedDate", source = "product.lastModifiedDate")
    public abstract UpdateResponse mapToUpdateResponse(Product product);

    public abstract List<ProductResponse> entitiesToEntityDTOs(List<Product> products);

    public abstract LinkedHashSet<ProductResponse> entitiesToDTOs(Set<Product> products);
}
