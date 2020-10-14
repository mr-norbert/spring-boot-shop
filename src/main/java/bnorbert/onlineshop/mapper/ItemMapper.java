package bnorbert.onlineshop.mapper;

import bnorbert.onlineshop.domain.CopyOfTheProduct;
import bnorbert.onlineshop.transfer.cart.AddToCartResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public abstract class ItemMapper {
    @Mapping(target = "id", source = "id")
    public abstract AddToCartResponse mapToDto(CopyOfTheProduct copyOfTheProducts);

    public abstract List<AddToCartResponse> entitiesToEntityDTOs(List<CopyOfTheProduct> copyOfTheProducts);
}
