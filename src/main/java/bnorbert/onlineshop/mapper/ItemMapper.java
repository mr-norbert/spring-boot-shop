package bnorbert.onlineshop.mapper;

import bnorbert.onlineshop.domain.Pantry;
import bnorbert.onlineshop.transfer.cart.AddToCartResponse;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public abstract class ItemMapper {

    public abstract AddToCartResponse mapToCartResponse(Pantry pantries);

    public abstract List<AddToCartResponse> entitiesToEntityDTOs(List<Pantry> pantries);
}
