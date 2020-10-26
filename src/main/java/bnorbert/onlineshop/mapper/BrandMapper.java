package bnorbert.onlineshop.mapper;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BrandMapper {
/*
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "name", source = "brandDto.name")
    Brand map(BrandDto brandDto);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "brand.name")
    BrandResponse mapToDto(Brand brand);

 */

}
