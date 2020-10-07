package bnorbert.onlineshop.mapper;

import bnorbert.onlineshop.domain.Brand;
import bnorbert.onlineshop.transfer.brand.BrandDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BrandMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "name", source = "brandDto.name")
    Brand map(BrandDto brandDto);
}
